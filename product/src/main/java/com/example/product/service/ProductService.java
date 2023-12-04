package com.example.product.service;

import com.example.product.domain.entity.ProductEntity;
import com.example.product.domain.model.ProductDto;
import com.example.product.factory.ProductFactory;
import com.example.product.repository.ProductRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductFactory productFactory;


    public Optional<ProductDto> getProduct(Long id) {
        return productRepository.findByIdAndDeletedFalse(id).map(productFactory::toProductDto);
    }

    public List<ProductDto> getAllByCategory(String category, Pageable pageable) {
        return category == null ? productRepository.findAllByDeletedIsFalse(pageable).getContent().stream().map(productFactory::toProductDto).toList() :
                productRepository.findByCategoryAndDeletedIsFalse(category, pageable).getContent().stream().map(productFactory::toProductDto).toList();
    }

    public ProductDto addProduct(ProductDto product) {
        try {
            ProductEntity savedProductEntity = productRepository.save(
                    ProductEntity.builder()
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .category(product.getCategory())
                            .deleted(false)
                            .build());
            return productFactory.toProductDto(savedProductEntity);
        } catch (ConstraintViolationException e) {
            log.error("Error while saving product", e);
            throw e;
        }

    }

    public void updateProduct(ProductDto product, ProductDto productUpdate) {
        productRepository.save(
                ProductEntity.builder()
                        .id(product.getId())
                        .name(productUpdate.getName())
                        .description(productUpdate.getDescription())
                        .price(productUpdate.getPrice())
                        .category(productUpdate.getCategory())
                        .deleted(product.getDeleted())
                        .build()
        );
    }

    public void deleteProduct(Long id) {
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        if (productEntity.isPresent()) {
            ProductEntity productEntity1 = productEntity.get();
            productRepository.save(
                    ProductEntity.builder()
                            .id(id)
                            .name(productEntity1.getName())
                            .description(productEntity1.getDescription())
                            .price(productEntity1.getPrice())
                            .category(productEntity1.getCategory())
                            .deleted(true)
                            .build());
        }
    }

    public Boolean isExist(Long id) {
        return productRepository.existsByIdAndDeletedFalse(id);
    }

    public String getProductName(Long id) {
        return productRepository.findByIdAndDeletedFalse(id).map(ProductEntity::getName).orElse(null);
    }
}
