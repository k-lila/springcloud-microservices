package backend.SaleMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.client.IStockClient;
import backend.SaleMicroservice.domain.ProductQuantity;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.domain.Sale.SaleStatus;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
import backend.SaleMicroservice.exception.ExternalServiceException;
import backend.SaleMicroservice.repository.ISaleRepository;
import feign.FeignException;

@Service
public class UpdateSaleStatus {
    private final ISaleRepository saleRepository;
    private final IStockClient stockClient;

    @Autowired
    public UpdateSaleStatus(ISaleRepository saleRepository, IStockClient stockClient) {
        this.saleRepository = saleRepository;
        this.stockClient = stockClient;
    }

    private Sale validateSale(String saleId) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(
            () -> new DomainEntityNotFound(Sale.class, "saleId", saleId)
        );
        return sale;
    }

    private Boolean checkStock(String productId, Integer quantity) {
        if (stockClient.isEnough(productId, quantity)) {
            return true;
        } else {
            throw new RuntimeException("ESTOQUE INSUFICIENTE: " + productId);
        }
    }

    private Sale changeStatus(Sale sale, SaleStatus newStatus) {
        sale.setStatus(newStatus);
        return saleRepository.save(sale);
    }

    public Sale closeSale(String saleId) {
        Sale sale = validateSale(saleId);
		if (sale.getStatus() == SaleStatus.INICIADA) {
            for (ProductQuantity product : sale.getProductList()) {
                checkStock(product.getProductId(), product.getQuantity());
            }
            for (ProductQuantity product : sale.getProductList()) {
                try {
                    stockClient.updateQuantity(product.getProductId(), -product.getQuantity());
                } catch (FeignException e) {
                    throw new ExternalServiceException("stock-service", e);
                }
            }
        return changeStatus(sale, SaleStatus.FINALIZADA);
        } else {
			throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR VENDA");
        }
    }

    public Sale cancelSale(String saleId) {
        Sale sale = validateSale(saleId);
        if (sale.getStatus() == SaleStatus.FINALIZADA) {
            for (ProductQuantity product : sale.getProductList()) {
                try {
                    stockClient.updateQuantity(product.getProductId(), product.getQuantity());
                } catch (FeignException e) {
                    throw new ExternalServiceException("stock-service", e);
                }
            }
            return changeStatus(sale, SaleStatus.CANCELADA);
        } else {
            throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR VENDA");
        }
    }

}
