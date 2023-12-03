package com.example.stock.service;

import com.example.stock.domain.entity.StockEntity;
import com.example.stock.domain.model.StockDto;
import com.example.stock.factory.StockFactory;
import com.example.stock.repository.StockRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final StockFactory stockFactory;


    public Optional<StockDto> getStock(Long id) {
        return stockRepository.findById(id).map(stockFactory::toStockDto);
    }

    public StockDto addStock(StockDto stock) {
        try {
            ResponseEntity<Boolean> response
                    = new RestTemplate().getForEntity("http://localhost:8081/product/isExist/" + stock.getProductId(), Boolean.class);
            if (!response.getBody()) {
                throw new InvalidDataAccessApiUsageException("Product does not exist");
            }
            if (stockRepository.existsByProductId(stock.getProductId())) {
                throw new InvalidDataAccessApiUsageException("Product already exists in stock");
            }
            StockEntity stockEntity = stockFactory.toStockEntity(stock);
            StockEntity savedStockEntity = stockRepository.save(stockEntity);
            return stockFactory.toStockDto(savedStockEntity);
        } catch (ConstraintViolationException e) {
            log.error("Error while saving stock", e);
            throw e;
        }

    }

    public void updateStock(StockDto stock, StockDto stockUpdate) {
        stockRepository.save(
                StockEntity.builder()
                        .id(stock.getId())
                        .productId(stock.getProductId())
                        .stock(stockUpdate.getStock())
                        .build()
        );
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
}
