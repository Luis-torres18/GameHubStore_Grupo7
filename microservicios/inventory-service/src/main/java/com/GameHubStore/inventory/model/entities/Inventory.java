package com.GameHubStore.inventory.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name= "inventories")
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private  long productId;

    @Column(nullable = false)
    private  Integer availableStock;

    @Column(nullable = false)
    private Integer reservedStock;

    @Column(nullable = false)
    private Integer minimumStock;

    private  String Location;

}
