package com.example.stock.repository;

import com.example.stock.domain.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Boolean existsByProductId(Long productId);
}
