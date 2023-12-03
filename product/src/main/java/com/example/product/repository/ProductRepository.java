package com.example.product.repository;

import com.example.product.domain.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Boolean existsByIdAndDeletedFalse(Long id);
    Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

    Page<ProductEntity> findAllByDeletedIsFalse(Pageable pageable);

    Page<ProductEntity> findByCategoryAndDeletedIsFalse(String category, Pageable pageable);
}
