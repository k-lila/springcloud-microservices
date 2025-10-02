package backend.StockMicroservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.StockMicroservice.domain.Stock;

@Repository
public interface IStockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByProductId(String productId);
}
