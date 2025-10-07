package backend.ProductMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.ProductMicroservice.domain.Product;
import backend.ProductMicroservice.exception.DomainEntityNotFound;
import backend.ProductMicroservice.repository.IProductRepository;
import jakarta.validation.Valid;

@Service
public class RegisterProduct {

    private IProductRepository productRepository;
    @Autowired
    public RegisterProduct(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product registerProduct(@Valid Product product) {
        return this.productRepository.insert(product);
    }

    public Product updateProduct(@Valid Product product) {
        if (!productRepository.findById(product.getId()).isPresent()) {
            throw new DomainEntityNotFound(Product.class,"id", product.getId());
        }
        return this.productRepository.save(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new DomainEntityNotFound(Product.class,"id", id);
        }
        this.productRepository.deleteById(id);
    }
}
