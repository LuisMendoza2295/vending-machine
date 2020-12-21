package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.Preconditions;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.TransactionType;
import com.proximity.vending.domain.vo.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Transaction {

    private final TransactionID transactionID;
    private final ProductID productID;
    private final VendingMachineID vendingMachineID;
    private final TransactionType type;
    private final TransactionIssuer issuer;
    private final MoneyAmount amount;
    private final LocalDateTime dateTime;
    private final Map<Denomination, Integer> insertedDenomination;

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TransactionBuilder {
        private String uuid;
        private String productID;
        private String vendingMachineID;
        private String type;
        private String issuer;
        private BigDecimal amount;
        private LocalDateTime dateTime;

        public TransactionBuilder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public TransactionBuilder withGeneratedUUID() {
            this.uuid = UUID.randomUUID().toString();
            return this;
        }

        public TransactionBuilder productID(String productID) {
            this.productID = productID;
            return this;
        }

        public TransactionBuilder vendingMachineID(String vendingMachineID) {
            this.vendingMachineID = vendingMachineID;
            return this;
        }

        public TransactionBuilder type(String type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder dateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Transaction build() {
            Preconditions.checkNotNull(this.uuid, () -> new InvalidDataException(this.uuid));
            Preconditions.checkNotNull(this.productID, () -> new InvalidDataException(this.productID));
            Preconditions.checkNotNull(this.vendingMachineID, () -> new InvalidDataException(this.vendingMachineID));
            Preconditions.checkNotNull(this.type, () -> new InvalidDataException(this.type));
            Preconditions.checkNotNull(this.amount, () -> new InvalidDataException(this.amount));
            Preconditions.checkNotNull(this.dateTime, () -> new InvalidDataException(this.dateTime));
            Preconditions.checkArgument(this.amount.compareTo(BigDecimal.ZERO) < 0, () -> new InvalidDataException(this.amount));

            TransactionID transactionID = TransactionID.of(this.uuid);
            ProductID productID = ProductID.of(this.productID);
            VendingMachineID vendingMachineID = VendingMachineID.of(this.vendingMachineID);
            TransactionType type = TransactionType.fromCode(this.type);
            MoneyAmount moneyAmount = MoneyAmount.of(this.amount);

            return new Transaction(
                    transactionID,
                    productID,
                    vendingMachineID,
                    type,
                    type.equals(TransactionType.CASH)
                            ? TransactionIssuer.of(TransactionType.CASH.getCode())
                            : TransactionIssuer.of(this.issuer),
                    moneyAmount,
                    this.dateTime,
                    new HashMap<>()
            );
        }
    }

    public Transaction addDenomination(Denomination denomination, int count) {
        this.insertedDenomination.put(denomination, count);
        return this;
    }

    public TransactionBuilder toBuilder() {
        return builder()
                .uuid(this.transactionID.getValue())
                .productID(this.productID.getValue())
                .vendingMachineID(this.vendingMachineID.getValue())
                .type(this.type.getCode())
                .amount(this.amount.getValue());
    }
}
