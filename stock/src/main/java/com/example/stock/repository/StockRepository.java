package com.example.stock.repository;

import com.example.stock.domain.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findByProductId(Long productId);

    Boolean existsByProductId(Long productId);

    void deleteByProductId(Long productId);

}
