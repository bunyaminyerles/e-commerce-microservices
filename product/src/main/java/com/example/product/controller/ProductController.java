package com.example.product.controller;

import com.example.product.domain.jsonView.ViewRole;
import com.example.product.domain.model.ProductDto;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    @JsonView(ViewRole.ViewRequest.class)
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        Optional<ProductDto> product = productService.getProduct(id);
        return product.isPresent() ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    @JsonView(ViewRole.ViewRequest.class)
    public ResponseEntity<List<ProductDto>> findByCategory(@RequestParam(required = false) String category, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllByCategory(category, pageable));
    }

    @PostMapping
    @JsonView(ViewRole.AddRequest.class)
    public ResponseEntity<Void> addProduct(@RequestBody ProductDto product, UriComponentsBuilder ucb) {
        ProductDto savedProduct = productService.addProduct(product);
        URI locationOfNewProduct = ucb.path("product/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProduct).build();
    }

    @PutMapping("/{id}")
    @JsonView(ViewRole.UpsertRequest.class)
    private ResponseEntity<Void> putProduct(@PathVariable Long id, @RequestBody ProductDto productUpdate) {
        Optional<ProductDto> product = productService.getProduct(id);
        if (product.isPresent()) {
            ProductDto productDto = product.get();
            productService.updateProduct(productDto, productUpdate);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id) {
        Optional<ProductDto> product = productService.getProduct(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/isExist/{id}")
    public ResponseEntity<Boolean> isExist(@PathVariable Long id) {
        return ResponseEntity.ok(productService.isExist(id));
    }

}
