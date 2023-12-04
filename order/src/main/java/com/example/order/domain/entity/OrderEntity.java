package com.example.order.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "orders")
@SequenceGenerator(name = "order_seq_gen", allocationSize = 1, sequenceName = "order_seq")
public class OrderEntity extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
    private Long id;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetailEntityList = new ArrayList<>();
    @Column(name = "total_amount")
    @NotNull(message = "Total amount is required")
    private Double totalAmount;
    @Column(name = "discount")
    @NotNull(message = "Discount is required")
    private Double discount;
    @Column(name = "payment_amount")
    @NotNull(message = "Payment amount is required")
    private Double paymentAmount;
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private StatusType status;
}
