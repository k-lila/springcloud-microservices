package backend.StockMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.StockMicroservice.client.IProductClient;
import backend.StockMicroservice.domain.Stock;
import backend.StockMicroservice.repository.IStockRepository;

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
        productClient.getProductById(stock.getProductId());
        return stockRepository.save(stock);
    }

    public Stock updateQuantity(String productId, Integer newQuantity) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado para produto " + productId));
        stock.setQuantity(stock.getQuantity() + newQuantity);
        return stockRepository.save(stock);
    }

    public void deleteStock(String id) {
        stockRepository.deleteById(id);
    }

}