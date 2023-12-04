package com.example.order.domain.entity;

public record Product(Long id, String name, Double price, String category, String description) {
}
