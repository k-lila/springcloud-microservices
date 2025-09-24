package backend.ProductMicroservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.ProductMicroservice.domain.Product;
import backend.ProductMicroservice.exception.DomainEntityNotFound;
import backend.ProductMicroservice.repository.IProductRepository;



@Service
public class SearchProduct {

    private IProductRepository productRepository;
    @Autowired
    public SearchProduct(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> search(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product searchById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new DomainEntityNotFound(Product.class, "id", id));
    }

    public Boolean isRegistered(String id) {
        Optional<Product> product = productRepository.findById(id);
        return product.isPresent() ? true : false;
    }

    public Product searchByCode(String code) {
        return productRepository.findByCode(code).orElseThrow(() -> new DomainEntityNotFound(Product.class, "code", code));
    }
}
