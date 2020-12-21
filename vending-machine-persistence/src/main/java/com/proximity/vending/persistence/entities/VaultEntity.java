package com.proximity.vending.persistence.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "vaults")
public class VaultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vault_id")
    private long id;

    @Column(name = "count")
    private int count;

    @Column(name = "currency_type")
    private String currencyType;

    @Column(name = "currency_denomination")
    private String currencyDenomination;

    @ManyToOne
    @JoinColumn(name = "vending_machine_id")
    private VendingMachineEntity vendingMachineEntity;
}
