package com.example.order.factory;

import com.example.order.domain.entity.OrderDetailEntity;
import com.example.order.domain.entity.OrderEntity;
import com.example.order.domain.model.OrderDetailDto;
import com.example.order.domain.model.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFactory {

    public OrderEntity toOrderEntity(OrderDto orderDto) {
        return OrderEntity.builder()
                .orderDetailEntityList(orderDto.getOrderDetailEntityList().stream().map(this::toOrderDetailEntity).toList())
                .discount(orderDto.getDiscount())
                .build();
    }

    public OrderDto toOrderDto(OrderEntity orderEntity) {
        return OrderDto.builder()
                .id(orderEntity.getId())
                .orderDetailEntityList(orderEntity.getOrderDetailEntityList().stream().map(this::toOrderDetailDto).toList())
                .discount(orderEntity.getDiscount())
                .totalAmount(orderEntity.getTotalAmount())
                .paymentAmount(orderEntity.getPaymentAmount())
                .status(orderEntity.getStatus())
                .build();
    }

    public OrderDetailEntity toOrderDetailEntity(OrderDetailDto orderDetailDto) {
        return OrderDetailEntity.builder()
                .quantity(orderDetailDto.getQuantity())
                .productId(orderDetailDto.getProductId())
                .build();
    }

    public OrderDetailDto toOrderDetailDto(OrderDetailEntity orderDetailEntity) {
        return OrderDetailDto.builder()
                .id(orderDetailEntity.getId())
                .quantity(orderDetailEntity.getQuantity())
                .productId(orderDetailEntity.getProductId())
                .productName(orderDetailEntity.getProductName())
                .unitAmount(orderDetailEntity.getUnitAmount())
                .build();
    }

}
