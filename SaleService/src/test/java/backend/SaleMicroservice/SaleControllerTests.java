package backend.SaleMicroservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import backend.SaleMicroservice.client.IClientClient;
import backend.SaleMicroservice.client.IProductClient;
import backend.SaleMicroservice.client.IStockClient;
import backend.SaleMicroservice.controller.SaleController;
import backend.SaleMicroservice.domain.ProductQuantity;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.domain.Sale.SaleStatus;
import backend.SaleMicroservice.dto.ClientDTO;
import backend.SaleMicroservice.dto.ProductDTO;
import backend.SaleMicroservice.dto.SaleRequestDTO;
import backend.SaleMicroservice.dto.SaleResponseDTO;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
import backend.SaleMicroservice.exception.ExternalServiceException;
import backend.SaleMicroservice.repository.ISaleRepository;
import backend.SaleMicroservice.service.RegisterSale;
import backend.SaleMicroservice.service.SearchSale;
import backend.SaleMicroservice.service.UpdateSaleStatus;
import feign.FeignException;

@SpringBootTest
@ActiveProfiles("test")
public class SaleControllerTests {

    @Autowired
    private ISaleRepository saleRepository;
    private RegisterSale registerSale;
    private SearchSale searchSale;
    private UpdateSaleStatus updateSaleStatus;
    private SaleController saleController;
    private IClientClient clientClientMock;
    private IProductClient productClientMock;
    private IStockClient stockClientMock;

    @Autowired
    public SaleControllerTests(ISaleRepository saleRepository) {
        clientClientMock = Mockito.mock(IClientClient.class);
        productClientMock = Mockito.mock(IProductClient.class);
        stockClientMock = Mockito.mock(IStockClient.class);
        registerSale = new RegisterSale(saleRepository, clientClientMock, productClientMock, stockClientMock);
        searchSale = new SearchSale(saleRepository, clientClientMock);
        updateSaleStatus = new UpdateSaleStatus(saleRepository, stockClientMock);
        saleController = new SaleController(registerSale, searchSale, updateSaleStatus);
    }

    private ClientDTO createClientDTO(String id) {
        ClientDTO client = new ClientDTO(id, id, id);
        Mockito.when(clientClientMock.getClientById(id)).thenReturn(client);
        return client;
    }
    
    private ProductDTO createProductDTO(String productId) {
        ProductDTO productDTO = new ProductDTO(productId, productId, BigDecimal.ONE);
        Mockito.when(productClientMock.getProductById(productId)).thenReturn(productDTO);
        Mockito.when(productClientMock.getProductByCode(productId)).thenReturn(productDTO);
        return productDTO;
    }

    private SaleResponseDTO createSale() {
        ClientDTO client = createClientDTO("clientMock");
        ProductDTO product = createProductDTO("productMock");
        Sale sale = new Sale(client.getId());
        sale.setCode("saleMock");
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductId(product.getId());
        productQuantity.setQuantity(1);
        productQuantity.setPrice(BigDecimal.ONE);
        sale.addProduct(productQuantity);
        Mockito.when(stockClientMock.isEnough(product.getCode(), 1)).thenReturn(true);
        return SaleResponseDTO.fromEntity(sale);
    }

    @AfterEach
    void clean() {
        saleRepository.deleteAll();
    }

    @Test
    void contextLoad() {}

    @Test
    void registerSaleTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        assertEquals(HttpStatus.OK, saleResponse.getStatusCode());
        assertEquals(saleDTO.getClientId(), saleResponse.getBody().getClientId());
        assertEquals(saleDTO.getCode(), saleResponse.getBody().getCode());
    }

    @Test
    void searchByTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        ResponseEntity<SaleResponseDTO> searchedById = saleController.searchById(saleResponse.getBody().getId());
        ResponseEntity<SaleResponseDTO> searchedByCode = saleController.searchByCode(saleResponse.getBody().getCode());
        Pageable pageable = Pageable.ofSize(10);
        ResponseEntity<Page<SaleResponseDTO>> searchedByClient = saleController.searchByClient(saleResponse.getBody().getClientId(), pageable);
        assertEquals(saleResponse.getBody().getId(), searchedById.getBody().getId());
        assertEquals(saleResponse.getBody().getId(), searchedByCode.getBody().getId());
        assertEquals(saleResponse.getBody().getId(), searchedByClient.getBody().getContent().get(0).getId());
    }

    @Test
    void addProductTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        ProductDTO newProduct = createProductDTO("newProduct");
        Mockito.when(stockClientMock.isEnough("productMock", 5)).thenReturn(true);
        Mockito.when(stockClientMock.isEnough(newProduct.getId(), 3)).thenReturn(true);
        ResponseEntity<SaleResponseDTO> add1 = saleController.addProduct(saleResponse.getBody().getId(), "productMock", 4);
        ResponseEntity<SaleResponseDTO> add2 = saleController.addProduct(saleResponse.getBody().getId(), newProduct.getCode(), 3);
        assertEquals(HttpStatus.OK, add1.getStatusCode());
        assertEquals(HttpStatus.OK, add2.getStatusCode());
        assertEquals(5, add1.getBody().getProductList().get(0).getQuantity());
        assertEquals(3, add2.getBody().getProductList().get(1).getQuantity());
    }

    @Test
    void removeProductTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        ProductDTO newProduct = createProductDTO("newProduct");
        Mockito.when(stockClientMock.isEnough(newProduct.getId(), 4)).thenReturn(true);
        saleController.addProduct(saleResponse.getBody().getId(), "newProduct", 4);
        ResponseEntity<SaleResponseDTO> remove1 = saleController.removeProduct(saleResponse.getBody().getId(), "productMock", 1);
        ResponseEntity<SaleResponseDTO> remove2 = saleController.removeProduct(saleResponse.getBody().getId(), "newProduct", 1);
        assertEquals(3, remove2.getBody().getProductList().get(0).getQuantity());
    }

    @Test
    void removeAllProductsTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        ProductDTO newProduct = createProductDTO("newProduct");
        Mockito.when(stockClientMock.isEnough("productMock", 4)).thenReturn(true);
        Mockito.when(stockClientMock.isEnough(newProduct.getId(), 3)).thenReturn(true);
        saleController.addProduct(saleResponse.getBody().getId(), "productMock", 3);
        saleController.addProduct(saleResponse.getBody().getId(), newProduct.getCode(), 3);
        ResponseEntity<SaleResponseDTO> before = saleController.searchById(saleResponse.getBody().getId());
        Integer total = before.getBody().getProductList().stream().map((p) -> p.getQuantity()).reduce(0, (a, b) -> a + b);
        assertEquals(7, total);
        saleController.removeAll(saleResponse.getBody().getId());
        ResponseEntity<SaleResponseDTO> after = saleController.searchById(saleResponse.getBody().getId());
        Integer totalAfter = after.getBody().getProductList().stream().map((p) -> p.getQuantity()).reduce(0, (a, b) -> a + b);
        assertEquals(0, totalAfter);
    }

    @Test
    void searchAllTest() {
        for (int i = 0; i < 5; i++) {
            SaleResponseDTO saleDTO = createSale();
            saleDTO.setCode(String.valueOf(i));
            SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
            saleController.registerSale(saleRequest);
        }
        Pageable pageable = Pageable.ofSize(10);
        ResponseEntity<Page<SaleResponseDTO>> response = saleController.searchAllSales(pageable);
        assertEquals(5, response.getBody().getNumberOfElements());
    }

    @Test
    void chageStatusTest() {
        SaleResponseDTO saleDTO = createSale();
        SaleRequestDTO saleRequest = new SaleRequestDTO(saleDTO.getClientId(), saleDTO.getCode(), saleDTO.getProductList());
        ResponseEntity<SaleResponseDTO> saleResponse = saleController.registerSale(saleRequest);
        saleController.closeSale(saleResponse.getBody().getId());
        ResponseEntity<SaleResponseDTO> closed = saleController.searchById(saleResponse.getBody().getId());
        assertEquals(SaleStatus.FINALIZADA, closed.getBody().getStatus());
        saleController.cancelSale(saleResponse.getBody().getId());
        ResponseEntity<SaleResponseDTO> canceled = saleController.searchById(saleResponse.getBody().getId());
        assertEquals(SaleStatus.CANCELADA, canceled.getBody().getStatus());
    }

    @Test
    void DomainEntityNotFoundExceptionTest() {
        assertThrows(DomainEntityNotFound.class, () -> {
            saleController.searchById("inexistente");
        });
    }

    @Test
    void ExternalServiceExceptionTest() {
        Mockito.when(clientClientMock.getClientById("inexistente")).thenThrow(FeignException.class);
        Pageable pageable = Pageable.ofSize(1);
        assertThrows(ExternalServiceException.class, () -> {
            saleController.searchByClient("inexistente", pageable);
        });
    }
}
