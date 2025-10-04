package backend.SaleMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.client.IStockClient;
import backend.SaleMicroservice.domain.ProductQuantity;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.domain.Sale.SaleStatus;
import backend.SaleMicroservice.repository.ISaleRepository;

@Service
public class UpdateSaleStatus {
    private final ISaleRepository saleRepository;
    private final IStockClient stockClient;

    @Autowired
    public UpdateSaleStatus(ISaleRepository saleRepository, IStockClient stockClient) {
        this.saleRepository = saleRepository;
        this.stockClient = stockClient;
    }

    public Sale validateSale(String saleId) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(
            () -> new RuntimeException("VENDA NÃO ENCONTRADA, ID: " + saleId)
        );
        return sale;
    }

    public Sale changeStatus(Sale sale, SaleStatus newStatus) {
        sale.setStatus(newStatus);
        return saleRepository.save(sale);
    }

    public Sale closeSale(String saleId) {
        Sale sale = validateSale(saleId);
		if (sale.getStatus() == SaleStatus.INICIADA) {
            for (ProductQuantity product : sale.getProductList()) {
                Boolean isEnough = stockClient.isEnough(product.getProductId(), product.getQuantity());
                if (!isEnough) {
                    throw new RuntimeException("ESTOQUE INSUFICIENTE, ID: " + product.getProductId());
                }
                stockClient.updateQuantity(product.getProductId(), -product.getQuantity());
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
                stockClient.updateQuantity(product.getProductId(), product.getQuantity());
            }
            return changeStatus(sale, SaleStatus.CANCELADA);
        } else {
            throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR VENDA");
        }
    }

}
