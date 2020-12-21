package com.proximity.vending.persistence.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "transaction_details")
public class TransactionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_detail_id")
    private long id;

    @Column(name = "currency_type")
    private String currencyType;

    @Column(name = "currency_denomination")
    private String currencyDenomination;

    @Column(name = "count")
    private int count;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transactionEntity;
}
