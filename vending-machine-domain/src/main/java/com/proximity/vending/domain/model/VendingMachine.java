package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.CashChangeException;
import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.OutOfStockException;
import com.proximity.vending.domain.exception.Preconditions;
import com.proximity.vending.domain.type.*;
import com.proximity.vending.domain.vo.MoneyAmount;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class VendingMachine {

    public static final BigDecimal MONEY_PICKUP_THRESHOLD = BigDecimal.valueOf(100);
    public static final String DEFAULT_ACCESS_CODE = "0000";

    private final VendingMachineID vendingMachineID;
    private final VendingMachineAccessCode accessCode;
    private final VendingMachineStatus status;
    private final VendingMachineType type;
    private final LocalDateTime lastMoneyPickUp;
    private final Map<ProductID, Integer> products;
    private final Map<ProductID, MoneyAmount> prices;
    private final Map<Denomination, Integer> denominationCount;

    public static VendingMachineBuilder builder() {
        return new VendingMachineBuilder();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VendingMachineBuilder {
        private String code;
        private String accessCode;
        private String status;
        private String type;
        private LocalDateTime lastMoneyPickUp;

        public VendingMachineBuilder code(String code) {
            this.code = code;
            return this;
        }

        public VendingMachineBuilder withDefaultAccessCode() {
            this.accessCode = DEFAULT_ACCESS_CODE;
            return this;
        }

        public VendingMachineBuilder accessCode(String accessCode) {
            this.accessCode = accessCode;
            return this;
        }

        public VendingMachineBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VendingMachineBuilder type(String type) {
            this.type = type;
            return this;
        }

        public VendingMachineBuilder lastMoneyPickUp(LocalDateTime lastMoneyPickUp) {
            this.lastMoneyPickUp = lastMoneyPickUp;
            return this;
        }

        public VendingMachine build() {
            Preconditions.checkNotNull(this.code, () -> new InvalidDataException(this.code));
            Preconditions.checkNotNull(this.accessCode, () -> new InvalidDataException(this.accessCode));
            Preconditions.checkNotNull(this.status, () -> new InvalidDataException(this.status));
            Preconditions.checkNotNull(this.type, () -> new InvalidDataException(this.type));

            VendingMachineID vendingMachineID = VendingMachineID.of(this.code);
            VendingMachineAccessCode accessCode = VendingMachineAccessCode.of(this.accessCode);
            VendingMachineStatus status = VendingMachineStatus.fromCode(this.status);
            VendingMachineType type = VendingMachineType.fromCode(this.type);

            Map<Denomination, Integer> denominationCount = Arrays.stream(Denomination.values())
                    .collect(Collectors.toMap(denomination -> denomination, denomination -> 0));

            return new VendingMachine(
                    vendingMachineID,
                    accessCode,
                    status,
                    type,
                    this.lastMoneyPickUp,
                    new HashMap<>(),
                    new HashMap<>(),
                    denominationCount
            );
        }
    }

    public VendingMachine dispenseProduct(ProductID productID) {
        Preconditions.checkArgument(!this.hasStock(productID), () -> OutOfStockException.builder()
                .productID(productID)
                .vendingMachineID(this.vendingMachineID)
                .build());

        int actualValue = this.products.get(productID);
        this.products.put(productID, actualValue - 1);
        return this;
    }

    public VendingMachine removeProduct(ProductID productID) {
        this.products.remove(productID);
        return this;
    }

    public VendingMachine putProduct(ProductID productID) {
        this.products.put(productID, 0);
        return this;
    }

    public VendingMachine putProduct(ProductID productID, int count) {
        this.products.put(productID, count);
        return this;
    }

    public VendingMachine putCurrencyCount(Denomination denomination, int count) {
        this.denominationCount.put(denomination, count);
        return this;
    }

    public VendingMachine addCurrencyCount(Denomination denomination, int count){
        int actualCount = this.denominationCount.get(denomination);
        this.denominationCount.put(denomination, actualCount + count);
        return this;
    }

    public VendingMachine putMoneyAmountsAsPrices(ProductID productID, BigDecimal price) {
        this.prices.put(productID, MoneyAmount.of(price));
        return this;
    }

    public VendingMachine putMoneyAmounts(ProductID productID, MoneyAmount moneyAmount) {
        this.prices.put(productID, moneyAmount);
        return this;
    }

    public boolean supportsTransactionType(TransactionType transactionType) {
        return this.type.getTransactionTypes()
                .contains(transactionType);
    }

    public boolean hasStock(ProductID productID) {
        return this.products.containsKey(productID) && this.products.get(productID) > 0;
    }

    public BigDecimal getProductPrice(ProductID productID) {
        if (this.prices.containsKey(productID)) {
            return this.prices.get(productID).getValue();
        }
        throw new InvalidDataException(productID);
    }

    public boolean hasChange(BigDecimal changeAmount) {
        return this.getTotalVault().compareTo(changeAmount) >= 0;
    }

    public VendingMachineBuilder toBuilder() {
        return builder()
                .code(this.vendingMachineID.getValue())
                .accessCode(this.accessCode.getValue())
                .status(this.status.getCode())
                .type(this.type.getCode());
    }

    public boolean isOverMoneyPickupThreshold() {
        return this.getTotalVault().compareTo(MONEY_PICKUP_THRESHOLD) > 0;
    }

    public BigDecimal getTotalVault() {
        return this.denominationCount
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Denomination, Integer> calculateChange(BigDecimal amount) {
        List<CurrencyCount> currencyCountList = this.denominationCount.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getCurrencyType().equals(CurrencyType.COIN))
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new CurrencyCount(entry.getKey(), entry.getValue()))
                .sorted((CurrencyCount c1, CurrencyCount c2) -> c2.getDenomination().getValue().compareTo(c1.getDenomination().getValue()))
                .collect(Collectors.toList());

        return new CoinChangeCalculator(vendingMachineID, currencyCountList, Integer.MAX_VALUE)
                .calculateChange(amount)
                .stream()
                .collect(Collectors.toMap(CurrencyCount::getDenomination, CurrencyCount::getCount));
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class CurrencyCount {
        private final Denomination denomination;
        private final int count;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class CoinChangeCalculator {
        private final VendingMachineID vendingMachineID;
        private final List<CurrencyCount> currencyCountList;
        private int min;

        private List<CurrencyCount> calculateChangeRecursive(int amount, int index, int coinCount) {
            if (amount == 0) {
                if (min == Integer.MAX_VALUE || coinCount < min) {
                    min = coinCount;
                    return new ArrayList<>();
                }
                return null;
            }

            if (index >= this.currencyCountList.size()) {
                return null;
            }

            List<CurrencyCount> bestChange = null;
            CurrencyCount currency = currencyCountList.get(index);

            BigDecimal denominationValue = currency.getDenomination().getCoinCentiValue();
            BigDecimal amountValue = BigDecimal.valueOf(amount);
            int canTake = Math.min(amountValue.divide(denominationValue, RoundingMode.FLOOR).intValue(), currency.getCount());

            for (int i = canTake; i >= 0; i--) {
                int newAmount = amount - (currency.getDenomination().getCoinCentiValue().intValue() * i);
                List<CurrencyCount> change = calculateChangeRecursive(newAmount, index + 1, coinCount + 1);

                if (change != null) {
                    if (i != 0) {
                        change.add(new CurrencyCount(currency.getDenomination(), i));
                    }
                    bestChange = new ArrayList<>(change);
                }
            }

            return bestChange;
        }

        public List<CurrencyCount> calculateChange(BigDecimal amount) {
            int centiAmount = Denomination.getCentiValue(amount).intValue();
            List<CurrencyCount> change = calculateChangeRecursive(centiAmount, 0, 0);

            if (change != null) {
                return change;
            } else {
                throw CashChangeException.builder()
                        .vendingMachineID(vendingMachineID)
                        .build();
            }
        }
    }
}
