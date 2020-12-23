package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.Preconditions;
import com.proximity.vending.domain.vo.MoneyAmount;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.ProductName;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Product {

    private final ProductID productID;
    private final ProductName name;
    private final MoneyAmount price;

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProductBuilder {
        private String code;
        private String name;
        private BigDecimal price;

        public ProductBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Product build() {
            Preconditions.checkArgument(StringUtils.isBlank(this.code), () -> new InvalidDataException(this.code));
            Preconditions.checkArgument(StringUtils.isBlank(this.name), () -> new InvalidDataException(this.name));
            Preconditions.checkNotNull(this.price, () -> new InvalidDataException(this.price));
            Preconditions.checkArgument(this.price.compareTo(BigDecimal.ZERO) <= 0, () -> new InvalidDataException(this.price));

            ProductID productID = ProductID.of(this.code);
            ProductName productName = ProductName.of(this.name);
            MoneyAmount moneyAmount = MoneyAmount.of(this.price);

            return new Product(
                    productID,
                    productName,
                    moneyAmount
            );
        }
    }

    public ProductBuilder toBuilder() {
        return builder()
                .code(this.productID.getValue())
                .name(this.name.getValue())
                .price(this.price.getValue());
    }
}
