package backend.StockMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.StockMicroservice.client.IProductClient;
import backend.StockMicroservice.domain.Stock;
import backend.StockMicroservice.exception.DomainEntityNotFound;
import backend.StockMicroservice.exception.ExternalServiceException;
import backend.StockMicroservice.repository.IStockRepository;
import feign.FeignException;

@Service
public class RegisterStock {

    private final IStockRepository stockRepository;
    private final IProductClient productClient;

    @Autowired
    public RegisterStock(IStockRepository stockRepository, IProductClient productClient) {
        this.stockRepository = stockRepository;
        this.productClient = productClient;
    }

    public Stock registerStock(Stock stock) {
        try {
            productClient.getProductById(stock.getProductId());
        } catch (FeignException e) {
            throw new ExternalServiceException("product-service", e);
        }
        return stockRepository.save(stock);
    }

    public Stock updateQuantity(String productId, Integer newQuantity) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new DomainEntityNotFound(Stock.class,"productId", productId));
                
        stock.setQuantity(stock.getQuantity() + newQuantity);
        return stockRepository.save(stock);
    }

    public void deleteStock(String id) {
        stockRepository.findById(id).orElseThrow(
            () -> new DomainEntityNotFound(Stock.class, "id", id)
        );
        stockRepository.deleteById(id);
    }

}