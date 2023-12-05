package com.example.order.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "order_detail")
@SequenceGenerator(name = "order_detail_seq_gen", allocationSize = 1, sequenceName = "order_detail_seq")
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_detail_seq_gen")
    private Long id;
    @Column(name = "quantity")
    @Min(value = 1, message = "Product quantity should be greater than 0")
    private Integer quantity;
    @Column(name = "amount")
    private Double unitAmount;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity orderEntity;

}
