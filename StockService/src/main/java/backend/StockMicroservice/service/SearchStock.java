package backend.StockMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.StockMicroservice.domain.Stock;
import backend.StockMicroservice.exception.DomainEntityNotFound;
import backend.StockMicroservice.repository.IStockRepository;

@Service
public class SearchStock {
    private final IStockRepository stockRepository;

    @Autowired
    public SearchStock(IStockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Page<Stock> searchStock(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    public Stock searchStockById(String id) {
        return stockRepository.findById(id).orElseThrow(() -> new DomainEntityNotFound(Stock.class, "id", id));
    }

    public Stock searchStockByProductId(String productId) {
        return stockRepository.findByProductId(productId).orElseThrow(() -> new DomainEntityNotFound(Stock.class, "productId", productId));
    }

    public Boolean isRegistered(String id) {
        return stockRepository.findById(id).isPresent();
    }

    public Boolean isRegisteredByProductId(String productId) {
        return stockRepository.findByProductId(productId).isPresent();
    }

    public Boolean isEnough(String productId, Integer quantity) {
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new DomainEntityNotFound(Stock.class, "productId", productId));
        return stock.getQuantity() >= quantity;
    }
}