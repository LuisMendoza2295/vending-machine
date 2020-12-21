package com.proximity.vending.persistence.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "vending_machine_product")
public class VendingMachineProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vending_machine_product_id")
    private long id;

    @Column(name = "count")
    private int count;

    @ManyToOne
    @JoinColumn(name = "vending_machine_id")
    private VendingMachineEntity vendingMachineEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
