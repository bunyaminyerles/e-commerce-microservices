package com.example.order.controller;


import com.example.order.domain.jsonView.ViewRole;
import com.example.order.domain.model.OrderDto;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    @JsonView(ViewRole.ViewRequest.class)
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        Optional<OrderDto> order = orderService.getOrder(id);
        return order.isPresent() ? ResponseEntity.ok(order.get()) : ResponseEntity.notFound().build();
    }

/*    @GetMapping("/all")
    @JsonView(ViewRole.ViewRequest.class)
    public ResponseEntity<List<OrderDto>> findByCategory(@RequestParam(required = false) String category, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllByCategory(category, pageable));
    }*/

    @PostMapping
    @JsonView(ViewRole.AddRequest.class)
    public ResponseEntity<Void> addOrder(@RequestBody OrderDto order, UriComponentsBuilder ucb) {
        OrderDto savedOrder = orderService.addOrder(order);
        URI locationOfNewOrder = ucb.path("order/{id}")
                .buildAndExpand(savedOrder.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewOrder).build();
    }

    @PostMapping("/cancel/{id}")
    @JsonView(ViewRole.DeleteRequest.class)
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        Optional<OrderDto> order = orderService.getOrder(id);
        if (order.isPresent()) {
            orderService.cancelOrder(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @JsonView(ViewRole.UpsertRequest.class)
    private ResponseEntity<Void> putOrder(@PathVariable Long id, @RequestBody OrderDto orderUpdate) {
        Optional<OrderDto> order = orderService.getOrder(id);
        if (order.isPresent()) {
            orderService.modifyOrder(id, orderUpdate);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
