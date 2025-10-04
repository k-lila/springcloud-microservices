package backend.StockMicroservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import backend.StockMicroservice.client.IProductClient;
import backend.StockMicroservice.controller.StockController;
import backend.StockMicroservice.domain.Stock;
import backend.StockMicroservice.dto.ProductDTO;
import backend.StockMicroservice.exception.DomainEntityNotFound;
import backend.StockMicroservice.repository.IStockRepository;
import backend.StockMicroservice.service.RegisterStock;
import backend.StockMicroservice.service.SearchStock;

@SpringBootTest
@ActiveProfiles("test")
public class StockControllerTests {

    @Autowired
    private IStockRepository stockRepository;
    private RegisterStock registerStock;
    private SearchStock searchStock;
    private StockController stockController;
    private IProductClient productClientMock;
    private ProductDTO mockProduct;

    @Autowired
    public StockControllerTests(IStockRepository stockRepository) {
        productClientMock = Mockito.mock(IProductClient.class);
        registerStock = new RegisterStock(stockRepository, productClientMock);
        searchStock = new SearchStock(stockRepository);
        stockController = new StockController(registerStock, searchStock);
        ProductDTO mockProduct = createProductDTO("mock");
        Mockito.when(productClientMock.getProductById("mock")).thenReturn(mockProduct);
    }

    ProductDTO createProductDTO(String code) {
        ProductDTO mockProduct = new ProductDTO();
        mockProduct.setId(code);
        mockProduct.setName("mockName");
        mockProduct.setPrice(BigDecimal.ONE);
        return mockProduct;
    }

    @AfterEach
    void clean() {
        stockRepository.deleteAll();
    }

    @Test
    void contextLoad() {
    }

    @Test
    void registerTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        ResponseEntity<Stock> register = stockController.registerStock(stock);
        assertEquals("mock", register.getBody().getProductId());
        assertEquals(10, register.getBody().getQuantity());
    }

    @Test
    void searchByTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        ResponseEntity<Stock> register = stockController.registerStock(stock);
        assertEquals(HttpStatus.OK, register.getStatusCode());
        ResponseEntity<Stock> searchedById = stockController.searchById(register.getBody().getId());
        ResponseEntity<Stock> searchedByProductId = stockController.searchByProductId("mock");
        assertEquals(HttpStatus.OK, searchedById.getStatusCode());
        assertEquals(HttpStatus.OK, searchedByProductId.getStatusCode());
        assertEquals("mock", searchedById.getBody().getProductId());
        assertEquals("mock", searchedByProductId.getBody().getProductId());
    }

    @Test
    void searchTest() throws Exception {
        for(int i = 0; i < 5; i++) {
            Stock stock = new Stock("mock" + i, 10);
            mockProduct = createProductDTO("mock" + i);
            Mockito.when(productClientMock.getProductById("mock" + i)).thenReturn(mockProduct);
            stockController.registerStock(stock);
        }
        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<Page<Stock>> response = stockController.search(pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getTotalElements());
    }

    @Test
    void isRegisteredTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        ResponseEntity<Stock> register = stockController.registerStock(stock);
        ResponseEntity<Boolean> registered = stockController.isRegistered(register.getBody().getId());
        ResponseEntity<Boolean> notRegistered = stockController.isRegistered("-");
        ResponseEntity<Boolean> pIdRegistered = stockController.isRegisteredByProductId("mock");
        ResponseEntity<Boolean> pIdNotRegistered = stockController.isRegisteredByProductId("-");
        assertEquals(true, registered.getBody());
        assertEquals(false, notRegistered.getBody());
        assertEquals(true, pIdRegistered.getBody());
        assertEquals(false, pIdNotRegistered.getBody());
    }

    @Test
    void updateQuantityTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        ResponseEntity<Stock> register = stockController.registerStock(stock);
        ResponseEntity<Stock> update = stockController.updateQuantity(register.getBody().getProductId(), -9);
        ResponseEntity<Stock> verify = stockController.searchById(update.getBody().getId());
        assertEquals(1, verify.getBody().getQuantity());
    }

    @Test
    void isEnoughTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        stockController.registerStock(stock);
        ResponseEntity<Boolean> isEnough = stockController.isEnough("mock", 5); 
        ResponseEntity<Boolean> isNotEnough = stockController.isEnough("mock", 50);
        assertEquals(true, isEnough.getBody());
        assertEquals(false, isNotEnough.getBody());
    }

    @Test
    void deleteTest() throws Exception {
        Stock stock = new Stock("mock", 10);
        ResponseEntity<Stock> register = stockController.registerStock(stock);
        assertEquals(HttpStatus.OK, register.getStatusCode());
        stockController.removeStock(register.getBody().getId());
        ResponseEntity<Boolean> isDeleted = stockController.isRegistered(register.getBody().getId());
        assertEquals(false, isDeleted.getBody());
    }

    @Test
    void DomainEntityNotFoundExceptionTest() {
        assertThrows(DomainEntityNotFound.class, () -> {
            stockController.searchById("inexistente");
        });
    }

}
