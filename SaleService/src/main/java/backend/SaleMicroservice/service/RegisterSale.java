package backend.SaleMicroservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.client.IClientClient;
import backend.SaleMicroservice.client.IProductClient;
import backend.SaleMicroservice.client.IStockClient;
import backend.SaleMicroservice.domain.ProductQuantity;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.dto.ClientDTO;
import backend.SaleMicroservice.dto.ProductDTO;
import backend.SaleMicroservice.dto.ProductQuantityDTO;
import backend.SaleMicroservice.dto.SaleRequestDTO;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
import backend.SaleMicroservice.exception.ExternalServiceException;
import backend.SaleMicroservice.repository.ISaleRepository;
import feign.FeignException;

@Service
public class RegisterSale {

    private final ISaleRepository saleRepository;
    private final IClientClient clientClient;
    private final IProductClient productClient;
    private final IStockClient stockClient;

    @Autowired
    public RegisterSale(ISaleRepository saleRepository,
                        IClientClient clientClient,
                        IProductClient productClient,
                        IStockClient stockClient) {
        this.saleRepository = saleRepository;
        this.clientClient = clientClient;
        this.productClient = productClient;
        this.stockClient = stockClient;
    }

    private ClientDTO validateClient(String clientId) {
        try {
            return clientClient.getClientById(clientId);
        } catch (FeignException e) {
            throw new ExternalServiceException("client-service", e);
        }
    }

    private ProductDTO validateProductByCode(String productCode) {
        try {
            return productClient.getProductByCode(productCode);
        } catch (FeignException e) {
            throw new ExternalServiceException("product-service", e);
        }
    }

    private ProductDTO validateProductById(String productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException e) {
            throw new ExternalServiceException("product-service", e);
        }
    }

    private Boolean checkStock(String productId, Integer quantity) {
        if (stockClient.isEnough(productId, quantity)) {
            return true;
        } else {
            throw new RuntimeException("ESTOQUE INSUFICIENTE: " + productId);
        }
    }

    public Sale registerSale(SaleRequestDTO requestSale) {
        validateClient(requestSale.getClientId());
        Sale sale = new Sale(requestSale.getClientId());
        sale.setCode(requestSale.getCode());
        for (ProductQuantityDTO productQty : requestSale.getProductList()) {
            ProductDTO product = validateProductById(productQty.getProductId());
            checkStock(productQty.getProductId(), productQty.getQuantity());
            ProductQuantity pqSale = new ProductQuantity();
            pqSale.setProductId(productQty.getProductId());
            pqSale.setPrice(product.getPrice());
            pqSale.setQuantity(productQty.getQuantity());
            sale.addProduct(pqSale);
        }
        sale.recalculateTotalPrice();
        Sale savedSale = saleRepository.save(sale);
        return savedSale;
    }

    public Sale addProduct(String saleId, String productCode, Integer quantity) {
        Sale requestSale = saleRepository.findById(saleId).orElseThrow(() -> new DomainEntityNotFound(Sale.class, "id", saleId));
        requestSale.validateStatus();
        ProductDTO productDTO = validateProductByCode(productCode);
        Optional<ProductQuantity> optional = requestSale.getProductList().stream().filter(
            (f) -> f.getProductId().equals(productDTO.getId())
        ).findAny();
        if (optional.isPresent()) {
            checkStock(productDTO.getId(), optional.get().getQuantity() + quantity);
        } else {
            checkStock(productDTO.getId(), quantity);
        }        
        ProductQuantity productSale = new ProductQuantity();
        productSale.setProductId(productDTO.getId());
        productSale.setPrice(productDTO.getPrice());
        productSale.setQuantity(quantity);
        requestSale.addProduct(productSale);
        requestSale.recalculateTotalPrice();
        Sale updatedSale = saleRepository.save(requestSale);
        return updatedSale;
    }

    public Sale removeProduct(String saleId, String productCode, Integer quantity) {
        Sale requestSale = saleRepository.findById(saleId).orElseThrow(() -> new DomainEntityNotFound(Sale.class, "id", saleId));
        requestSale.validateStatus();
        ProductDTO productDTO = validateProductByCode(productCode);
        ProductQuantity removeProduct = new ProductQuantity();
        removeProduct.setProductId(productDTO.getId());
        removeProduct.setPrice(productDTO.getPrice());
        removeProduct.setQuantity(quantity);
        requestSale.removeProduct(removeProduct);
        requestSale.recalculateTotalPrice();
        Sale updatedSale = saleRepository.save(requestSale);
        return updatedSale;
    }

    public Sale removeAll(String saleId) {
        Sale requestSale = saleRepository.findById(saleId).orElseThrow(() -> new DomainEntityNotFound(Sale.class, "id", saleId));
        requestSale.removeAll();
        Sale updatedSale = saleRepository.save(requestSale);
        return updatedSale;
    }

}
