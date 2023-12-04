package com.example.stock.controller;

import com.example.stock.domain.jsonView.ViewRole;
import com.example.stock.domain.model.StockDto;
import com.example.stock.service.StockService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stock")
@Slf4j
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/{id}")
    @JsonView(ViewRole.ViewRequest.class)
    public ResponseEntity<StockDto> getStock(@PathVariable Long id) {
        Optional<StockDto> stock = stockService.getStock(id);
        return stock.isPresent() ? ResponseEntity.ok(stock.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @JsonView(ViewRole.AddRequest.class)
    public ResponseEntity<Void> addStock(@RequestBody StockDto stock, UriComponentsBuilder ucb) {
        StockDto savedStock = stockService.addStock(stock);
        URI locationOfNewStock = ucb.path("stock/{id}")
                .buildAndExpand(savedStock.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewStock).build();
    }

    @PutMapping("/{id}")
    @JsonView(ViewRole.UpsertRequest.class)
    private ResponseEntity<Void> putStock(@PathVariable Long id, @RequestBody StockDto stockUpdate) {
        Optional<StockDto> stock = stockService.getStock(id);
        if (stock.isPresent()) {
            StockDto stockDto = stock.get();
            stockService.updateStock(stockDto, stockUpdate);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id) {
        Optional<StockDto> stock = stockService.getStock(id);
        if (stock.isPresent()) {
            stockService.deleteStock(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

/*    @PostMapping("/updateStock/{id}")
    @JsonView(ViewRole.UpsertRequest.class)
    private ResponseEntity<Void> updateStockByOrder(@PathVariable Long id, @RequestParam Integer quantity, @RequestParam Boolean isAdd) {
            stockService.updateStockByOrder(id, quantity, isAdd);
            return ResponseEntity.noContent().build();
    }*/

    @PostMapping("/updateStock/all")
    @JsonView(ViewRole.UpsertRequest.class)
    private ResponseEntity<Void> updateStockListByOrder(@RequestBody List<StockDto> stockList, @RequestParam Boolean isAdd) {
            stockService.updateStockListByOrder(stockList);
            return ResponseEntity.noContent().build();
    }


}
