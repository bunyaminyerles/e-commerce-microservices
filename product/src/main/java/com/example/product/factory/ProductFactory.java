package com.example.product.factory;

import com.example.product.domain.entity.ProductEntity;
import com.example.product.domain.model.ProductDto;
import org.springframework.stereotype.Service;

@Service
public class ProductFactory {

    public ProductEntity toProductEntity(ProductDto productDto) {
        return ProductEntity.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .category(productDto.getCategory())
                .deleted(productDto.getDeleted())
                .build();
    }

    public ProductDto toProductDto(ProductEntity productEntity) {
        return ProductDto.
                builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .category(productEntity.getCategory())
                .deleted(productEntity.getDeleted())
                .build();
    }

}
