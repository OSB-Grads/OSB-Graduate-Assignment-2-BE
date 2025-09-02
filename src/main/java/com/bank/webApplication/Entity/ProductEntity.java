package com.bank.webApplication.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="products")
public class ProductEntity {
    @Id

    private String productId;
    private double interestRate;
    private int fundingWindow;
    private int coolingPeriod;
    private int Tenure;
    private String description;


}
