package backend.StockMicroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import backend.StockMicroservice.dto.ProductDTO;

@FeignClient(name = "product-service")
public interface IProductClient {
    @GetMapping("/products/{id}")
    ProductDTO getProductById(@PathVariable("id") String id);
}
