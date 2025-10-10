package backend.SaleMicroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stock-service")
public interface IStockClient {
    @GetMapping("/stocks/product/{productId}/isEnough")
    Boolean isEnough(   
        @PathVariable("productId") String productId, 
        @RequestParam("quantity") Integer quantity
    );

    @PutMapping("/stocks/product/{productId}")
    void updateQuantity(
        @PathVariable("productId") String productId,
        @RequestParam("quantity") Integer quantity
    );
}