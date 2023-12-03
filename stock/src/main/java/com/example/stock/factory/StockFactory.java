package com.example.stock.factory;

import com.example.stock.domain.entity.StockEntity;
import com.example.stock.domain.model.StockDto;
import org.springframework.stereotype.Service;

@Service
public class StockFactory {

    public StockEntity toStockEntity(StockDto stockDto) {
        return StockEntity.builder()
                .productId(stockDto.getProductId())
                .stock(stockDto.getStock())
                .build();
    }

    public StockDto toStockDto(StockEntity stockEntity) {
        return StockDto.
                builder()
                .id(stockEntity.getId())
                .productId(stockEntity.getProductId())
                .stock(stockEntity.getStock())
                .build();
    }

}
