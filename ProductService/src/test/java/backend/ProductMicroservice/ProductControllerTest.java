package backend.ProductMicroservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import backend.ProductMicroservice.controller.ProductController;
import backend.ProductMicroservice.domain.Product;
import backend.ProductMicroservice.domain.Product.Status;
import backend.ProductMicroservice.exception.DomainEntityNotFound;
import backend.ProductMicroservice.repository.IProductRepository;

@SpringBootTest
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IProductRepository productRepository;

    @Autowired ProductController productController;

    private Product generateProduct(String code) {
        Product product = new Product();
        product.setName("produto teste");
        product.setCode(code);
        product.setDescription("descrição teste");
        product.setPrice(BigDecimal.valueOf(1.99));
        product.setStatus(Status.ACTIVE);
        return product;
    }

    @AfterEach
    void clean() {
        productRepository.deleteAll();
    }

    @Test
    void contextLoad() {
    }

    @Test
    void testMongoConnection() {
        String string_test = "teste produto";
        if (!mongoTemplate.collectionExists(string_test)) {
            mongoTemplate.createCollection(string_test);
        }
        Document doc = new Document();
        doc.put("message", "Teste Mongo");
        mongoTemplate.insert(doc, string_test);
		List<Document> found = mongoTemplate.findAll(Document.class, string_test);
        assertFalse(found.isEmpty());
        assertEquals("Teste Mongo", found.get(0).get("message"));
    }

    @Test
    void testRegisterProduct() {
        Product product = generateProduct("teste1");
        ResponseEntity<Product> response = productController.registerProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Product found = mongoTemplate.findById(response.getBody().getId(), Product.class);
        assertNotNull(found);
    }

    @Test
    void testSearchAll() {
        for (int i = 0; i < 5; i++) {
            Product p = generateProduct("teste" + i);
            ResponseEntity<Product> response = productController.registerProduct(p);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        PageRequest pageable = PageRequest.of(0, 10);
        ResponseEntity<Page<Product>> responsePage = productController.search(pageable);
        assertEquals(HttpStatus.OK, responsePage.getStatusCode());
        assertEquals(5, responsePage.getBody().getTotalElements());
    }

    @Test
    void testIfProductIsRegistered() {
        Product product = generateProduct("teste3");
        ResponseEntity<Product> response = productController.registerProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean isRegistered = productController.isRegistered(response.getBody().getId()).getBody();
        assertTrue(isRegistered);
        Boolean notRegistered = productController.isRegistered("nao-existe").getBody();
        assertFalse(notRegistered);
    }

    @Test
    void testSearchProductByIdAndCode() {
        Product product = generateProduct("teste4");
        ResponseEntity<Product> response = productController.registerProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Product foundById = productController.searchById(response.getBody().getId()).getBody();
        Product foundByCode = productController.searchByCode("teste4").getBody();
        assertEquals(foundById.getId(), foundByCode.getId());
        assertEquals(foundById.getDescription(), foundByCode.getDescription());
        assertEquals(product.getPrice(), foundByCode.getPrice());
    }

    @Test
    void testUpdateProduct() {
        Product product = generateProduct("teste5");
        ResponseEntity<Product> response = productController.registerProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Product updatedProduct = response.getBody();
        updatedProduct.setDescription("nova descrição");
        updatedProduct.setPrice(BigDecimal.valueOf(9.99));
        updatedProduct.setStatus(Status.INACTIVE);
        ResponseEntity<Product> updateResponse = productController.updateProduct(updatedProduct);
        assertEquals("nova descrição", updateResponse.getBody().getDescription());
        assertEquals(BigDecimal.valueOf(9.99), updateResponse.getBody().getPrice());
        assertEquals(Status.INACTIVE, updateResponse.getBody().getStatus());
    }

    @Test
    void testDeleteProduct() {
        Product product = generateProduct("teste6");
        ResponseEntity<Product> response = productController.registerProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<Void> deleteResponse = productController.removeProduct(response.getBody().getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertThrows(DomainEntityNotFound.class, () -> {
            productController.searchById(response.getBody().getId());
        });
    }
}
