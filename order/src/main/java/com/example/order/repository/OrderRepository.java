package com.example.order.repository;

import com.example.order.domain.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findById(Long id);

    Page<OrderEntity> findAll(Pageable pageable);

}
