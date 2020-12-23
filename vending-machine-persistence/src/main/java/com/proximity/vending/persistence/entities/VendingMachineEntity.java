package com.proximity.vending.persistence.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "vending_machine")
public class VendingMachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vending_machine_id")
    private long id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "access_code")
    private String accessCode;

    @Column(name = "access_attempts")
    private int accessAttempts = 0;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "last_money_pickup")
    private LocalDateTime lastMoneyPickUp;

    @Column(name = "last_ping")
    private LocalDateTime lastPing;

    @OneToMany(mappedBy = "vendingMachineEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<VendingMachineProductEntity> vendingMachineProductEntities = new HashSet<>();

    @OneToMany(mappedBy = "vendingMachineEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<VaultEntity> vaultEntities = new HashSet<>();

    public void addVaultEntity(VaultEntity vaultEntity) {
        this.vaultEntities.add(vaultEntity);
        vaultEntity.setVendingMachineEntity(this);
    }

    public void addVendingMachineProductEntity(VendingMachineProductEntity vendingMachineProductEntity) {
        this.vendingMachineProductEntities.add(vendingMachineProductEntity);
        vendingMachineProductEntity.setVendingMachineEntity(this);
    }
}
