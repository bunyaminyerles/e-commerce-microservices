package com.example.stock.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "stock", uniqueConstraints = {
        @UniqueConstraint(columnNames = "product_id")})
@SequenceGenerator(name = "stock_seq_gen", allocationSize = 1, sequenceName = "stock_seq")
public class StockEntity extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq_gen")
    private Long id;
    @Column(name = "product_id")
    @NotNull(message = "Product id is required")
    private Long productId;
    @Column(name = "stock")
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Long stock;

}
