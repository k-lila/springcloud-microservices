package backend.StockMicroservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.StockMicroservice.domain.Stock;
import backend.StockMicroservice.service.RegisterStock;
import backend.StockMicroservice.service.SearchStock;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final RegisterStock registerStock;
    private final SearchStock searchStock;

    @Autowired
    public StockController(RegisterStock registerStock, SearchStock searchStock) {
        this.registerStock = registerStock;
        this.searchStock = searchStock;
    }

    @PostMapping
    public ResponseEntity<Stock> registerStock(@RequestBody Stock stock) {
        return ResponseEntity.ok(registerStock.registerStock(stock));
    }

    @GetMapping
    public ResponseEntity<Page<Stock>> search(Pageable pageable) {
        return ResponseEntity.ok(searchStock.searchStock(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Stock> searchById(@PathVariable String id) {
        return ResponseEntity.ok(searchStock.searchStockById(id));
    }

    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<Stock> searchByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(searchStock.searchStockByProductId(productId));
    }

    @GetMapping(value = "isRegistered/{id}")
    public ResponseEntity<Boolean> isRegistered(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(searchStock.isRegistered(id));
    }

    @GetMapping(value = "isRegisteredByProductId/{productId}")
    public ResponseEntity<Boolean> isRegisteredByProductId(@PathVariable(value = "productId", required = true) String productId) {
        return ResponseEntity.ok(searchStock.isRegisteredByProductId(productId));
    }

    @PutMapping(value = "/product/{productId}")
    public ResponseEntity<Stock> updateQuantity(@PathVariable String productId, @RequestParam int quantity) {
        return ResponseEntity.ok(registerStock.updateQuantity(productId, quantity));
    }

    @GetMapping("/product/{productId}/isEnough")
    public ResponseEntity<Boolean> isEnough(@PathVariable String productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(searchStock.isEnough(productId, quantity));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> removeStock(@PathVariable(value = "id") String id) {
        registerStock.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

}