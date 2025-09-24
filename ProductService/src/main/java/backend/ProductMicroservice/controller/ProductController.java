package backend.ProductMicroservice.controller;

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
import org.springframework.web.bind.annotation.RestController;

import backend.ProductMicroservice.domain.Product;
import backend.ProductMicroservice.service.RegisterProduct;
import backend.ProductMicroservice.service.SearchProduct;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "products")
public class ProductController {
    private RegisterProduct registerProduct;
    private SearchProduct searchProduct;

    @Autowired
    public ProductController(RegisterProduct registerProduct, SearchProduct searchProduct) {
        this.registerProduct = registerProduct;
        this.searchProduct = searchProduct;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> search(Pageable pageable) {
        return ResponseEntity.ok(searchProduct.search(pageable));
    }

    @GetMapping(value = "isRegistered/{id}")
    public ResponseEntity<Boolean> isRegistered(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(searchProduct.isRegistered(id));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Search product by id")
    public ResponseEntity<Product> searchById(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(searchProduct.searchById(id));
    }

    @GetMapping(value = "/code/{code}")
    @Operation(summary = "Search product by code")
    public ResponseEntity<Product> searchByCode(@PathVariable(value = "code", required = true) String code) {
        return ResponseEntity.ok(searchProduct.searchByCode(code));
    }

	@PostMapping
	public ResponseEntity<Product> registerProduct(@RequestBody @Valid Product product) {
		return ResponseEntity.ok(registerProduct.registerProduct(product));
	}

    @PutMapping
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid Product product) {
        return ResponseEntity.ok(registerProduct.updateProduct(product));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "removes a product by its id")
    public ResponseEntity<Void> removeProduct(@PathVariable(value = "id") String id) {
        registerProduct.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
