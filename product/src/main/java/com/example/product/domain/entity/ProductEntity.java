package com.example.product.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "product")
@SequenceGenerator(name = "product_seq_gen", allocationSize = 1, sequenceName = "product_seq")
public class ProductEntity extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    private Long id;
    @Column(name = "name")
    @NotBlank(message = "Name is required")
    private String name;
    @Column(name = "description")
    @NotBlank(message = "Description is required")
    private String description;
    @Column(name = "price")
    @NotNull(message = "Price is required")
    private Double price;
    @Column(name = "category")
    @NotBlank(message = "Category is required")
    private String category;
}
