package com.example.order.service;

import com.example.order.domain.entity.*;
import com.example.order.domain.model.OrderDto;
import com.example.order.factory.OrderFactory;
import com.example.order.repository.OrderRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final Environment env;


    public Optional<OrderDto> getOrder(Long id) {
        return orderRepository.findById(id).map(orderFactory::toOrderDto);
    }

    public OrderDto addOrder(OrderDto order) {
        List<Stock> stockList;
        try {
            List<OrderDetailEntity> orderDetailEntityList = order.getOrderDetailEntityList().stream().map(orderDetail ->
            {
                final OrderDetailEntity orderDetailEntity1 = orderFactory.toOrderDetailEntity(orderDetail);
                final Product product = new RestTemplate().getForEntity(env.getProperty("service-address.product") + "/product/" + orderDetail.getProductId(), Product.class).getBody();
                assert product != null;
                orderDetailEntity1.setProductName(product.name());
                orderDetailEntity1.setUnitAmount(product.price());
                return orderDetailEntity1;
            }).toList();

            OrderEntity orderEntity = OrderEntity.builder()
                    .orderDetailEntityList(orderDetailEntityList)
                    .discount(order.getDiscount())
                    .build();

            final Double sum = orderEntity.getOrderDetailEntityList().stream().map(orderDetailDto -> orderDetailDto.getQuantity() * orderDetailDto.getUnitAmount()).reduce(0.0, Double::sum);
            orderEntity.setTotalAmount(sum);
            orderEntity.setPaymentAmount((sum * (100 - order.getDiscount())) / 100);
            orderEntity.setStatus(StatusType.CONFIRMED);

            stockList = orderEntity.getOrderDetailEntityList().stream().map(orderDetailDto ->
                    new Stock(orderDetailDto.getProductId(), orderDetailDto.getQuantity().longValue())
            ).toList();

            // update stock
            try {
                new RestTemplate().postForEntity(env.getProperty("service-address.stock") + "/stock/updateStock/all", stockList, Void.class);
            } catch (Exception e) {
                throw new InvalidDataAccessApiUsageException(e.getLocalizedMessage());
            }
            try {
                OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
                return orderFactory.toOrderDto(savedOrderEntity);
            } catch (Exception e) {
                // rollback stock
                stockList = stockList.stream().map(orderDetailDto ->
                        new Stock(orderDetailDto.productId(), -orderDetailDto.stock())
                ).toList();
                new RestTemplate().postForEntity(env.getProperty("service-address.stock") + "/stock/updateStock/all", stockList, Void.class);
                throw new InvalidDataAccessApiUsageException(e.getLocalizedMessage());
            }

        } catch (ConstraintViolationException e) {
            log.error("Error while saving product", e);
            throw e;
        }

    }

    public OrderDto cancelOrder(Long id) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isEmpty()) {
            throw new InvalidDataAccessApiUsageException("Order does not exist");
        }
        OrderEntity order = orderEntity.get();
        if (order.getStatus().equals(StatusType.CANCELLED)) {
            throw new InvalidDataAccessApiUsageException("Order already cancelled");
        }
        order.setStatus(StatusType.CANCELLED);
        orderRepository.save(order);

        // rollback stock
        List<Stock> stockList = order.getOrderDetailEntityList().stream().map(orderDetailDto ->
                new Stock(orderDetailDto.getProductId(), orderDetailDto.getQuantity().longValue() * -1)
        ).toList();
        new RestTemplate().postForEntity(env.getProperty("service-address.stock") + "/stock/updateStock/all", stockList, Void.class);

        return orderFactory.toOrderDto(order);
    }

    public void modifyOrder(Long id, OrderDto updatedOrderDto) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isEmpty()) {
            throw new InvalidDataAccessApiUsageException("Order does not exist");
        }
        OrderEntity order = orderEntity.get();
        if (order.getStatus().equals(StatusType.CANCELLED)) {
            throw new InvalidDataAccessApiUsageException("Order already cancelled");
        }

        // rollback stock list
        List<Stock> stockList = order.getOrderDetailEntityList().stream().map(orderDetailDto ->
                new Stock(orderDetailDto.getProductId(), orderDetailDto.getQuantity().longValue() * -1)
        ).toList();
        // create new order
        List<OrderDetailEntity> orderDetailEntityList = updatedOrderDto.getOrderDetailEntityList().stream().map(orderDetail ->
        {
            final OrderDetailEntity orderDetailEntity1 = orderFactory.toOrderDetailEntity(orderDetail);
            final Product product = new RestTemplate().getForEntity(env.getProperty("service-address.product") + "/product/" + orderDetail.getProductId(), Product.class).getBody();
            assert product != null;
            orderDetailEntity1.setProductName(product.name());
            orderDetailEntity1.setUnitAmount(product.price());
            return orderDetailEntity1;
        }).toList();

        OrderEntity orderEntity1 = OrderEntity.builder()
                .orderDetailEntityList(orderDetailEntityList)
                .discount(updatedOrderDto.getDiscount())
                .build();

        final Double sum = orderEntity1.getOrderDetailEntityList().stream().map(orderDetailDto -> orderDetailDto.getQuantity() * orderDetailDto.getUnitAmount()).reduce(0.0, Double::sum);
        orderEntity1.setId(order.getId());
        orderEntity1.setTotalAmount(sum);
        orderEntity1.setPaymentAmount((sum * (100 - updatedOrderDto.getDiscount())) / 100);
        orderEntity1.setStatus(StatusType.CONFIRMED);

        stockList = Stream.concat(orderEntity1.getOrderDetailEntityList().stream().map(orderDetailDto ->
                new Stock(orderDetailDto.getProductId(), orderDetailDto.getQuantity().longValue())
        ), stockList.stream()).toList();


        // update stock
        try {
            new RestTemplate().postForEntity(env.getProperty("service-address.stock") + "/stock/updateStock/all", stockList, Void.class);
        } catch (Exception e) {
            throw new InvalidDataAccessApiUsageException(e.getLocalizedMessage());
        }

        try {

            OrderEntity savedOrderEntity = orderRepository.save(orderEntity1);
            orderFactory.toOrderDto(savedOrderEntity);
        } catch (Exception e) {
            // rollback stock
            stockList = stockList.stream().map(orderDetailDto ->
                    new Stock(orderDetailDto.productId(), -orderDetailDto.stock())
            ).toList();
            new RestTemplate().postForEntity(env.getProperty("service-address.stock") + "/stock/updateStock/all", stockList, Void.class);
            throw new InvalidDataAccessApiUsageException(e.getLocalizedMessage());
        }
    }
}
