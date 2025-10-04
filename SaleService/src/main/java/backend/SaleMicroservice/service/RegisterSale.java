package backend.SaleMicroservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.client.IClientClient;
import backend.SaleMicroservice.client.IProductClient;
import backend.SaleMicroservice.client.IStockClient;
import backend.SaleMicroservice.domain.ProductQuantity;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.dto.ProductDTO;
import backend.SaleMicroservice.dto.ProductQuantityDTO;
import backend.SaleMicroservice.dto.SaleRequestDTO;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
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

    private void validateClient(String clientId) {
        try {
            clientClient.getClientById(clientId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("CLIENTE NÃO ENCONTRADO: " + clientId);
        }
    }

    private void validateProduct(String productCode) {
        try {
            productClient.getProductByCode(productCode);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("PRODUTO NÃO ENCONTRADO: " + productCode);
        }
    }

    public Sale registerSale(SaleRequestDTO requestSale) {
        validateClient(requestSale.getClientId());
        Sale sale = new Sale(requestSale.getClientId());
        sale.setCode(requestSale.getCode());

        for (ProductQuantityDTO productQty : requestSale.getProductList()) {
            ProductDTO product = productClient.getProductById(productQty.getProductId());
            if (product == null) {
                throw new RuntimeException("PRODUTO NÃO ENCONTRADO: " + productQty.getProductId());
            }
            Boolean isEnough = stockClient.isEnough(productQty.getProductId(), productQty.getQuantity());
            if (!isEnough) {
                throw new RuntimeException("ESTOQUE INSUFICIENTE: " + productQty.getProductId());
            }
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
        validateProduct(productCode);
        ProductDTO productDTO = productClient.getProductByCode(productCode);
        Optional<ProductQuantity> optional = requestSale.getProductList().stream().filter(
            (f) -> f.getProductId().equals(productDTO.getId())
        ).findAny();
        Boolean isEnough;
        if (optional.isPresent()) {
            isEnough = stockClient.isEnough(productDTO.getId(), optional.get().getQuantity() + quantity);
        } else {
            isEnough = stockClient.isEnough(productDTO.getId(), quantity);
        }
        if (!isEnough) {
            throw new RuntimeException("ESTOQUE INSUFICIENTE");
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
        validateProduct(productCode);
        ProductDTO productDTO = productClient.getProductByCode(productCode);

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
