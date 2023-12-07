package com.example.stock.service;

import com.example.stock.domain.entity.StockEntity;
import com.example.stock.domain.model.StockDto;
import com.example.stock.factory.StockFactory;
import com.example.stock.repository.StockRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StockService {
    private final StockRepository stockRepository;
    private final StockFactory stockFactory;
    private final Environment env;


    public Optional<StockDto> getStock(Long id) {
        return stockRepository.findByProductId(id).map(stockFactory::toStockDto);
    }

    public StockDto addStock(StockDto stock) {
        try {
            ResponseEntity<Boolean> response
                    = new RestTemplate().getForEntity(env.getProperty("service-address.product") + "/product/isExist/" + stock.getProductId(), Boolean.class);
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
        stockRepository.deleteByProductId(id);
    }


    public void updateStockListByOrder(List<StockDto> stockDtoList) {
        final List<StockEntity> stockEntityList = stockDtoList.stream().map(stockDto -> {
            Optional<StockEntity> stockEntity = stockRepository.findByProductId(stockDto.getProductId());
            if (stockEntity.isEmpty()) {
                throw new InvalidDataAccessApiUsageException("Product does not exist in stock");
            }
            StockEntity stock = stockEntity.get();
            if (stock.getStock() < stockDto.getStock()) {
                throw new InvalidDataAccessApiUsageException("Stock is not enough");
            }
            stock.setStock(stock.getStock() - stockDto.getStock());
            return stock;
        }).toList();

        stockRepository.saveAll(stockEntityList);
    }

}
