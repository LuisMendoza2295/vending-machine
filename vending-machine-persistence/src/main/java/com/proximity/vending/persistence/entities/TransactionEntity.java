package com.proximity.vending.persistence.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long id;

    @Column(name = "transaction_uuid", unique = true)
    private String uuid;

    @Column(name = "type")
    private String type;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "time")
    private LocalDateTime time;

    @OneToMany(mappedBy = "transactionEntity")
    private Set<TransactionDetailEntity> transactionDetailEntities;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "vending_machine_id")
    private VendingMachineEntity vendingMachineEntity;

    public void addTransactionDetailEntity(TransactionDetailEntity detailEntity) {
        this.transactionDetailEntities.add(detailEntity);
        detailEntity.setTransactionEntity(this);
    }
}
