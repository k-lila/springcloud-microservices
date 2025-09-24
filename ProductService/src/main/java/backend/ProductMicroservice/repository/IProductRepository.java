package backend.ProductMicroservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.ProductMicroservice.domain.Product;


@Repository
public interface IProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByCode(String code);
}
