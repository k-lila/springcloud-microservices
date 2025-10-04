package backend.SaleMicroservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "sale")
@Getter
@Setter
public class Sale {

    public enum SaleStatus {
        INICIADA, CANCELADA, FINALIZADA;
        public static SaleStatus getByName(String saleStatus) {
            for (SaleStatus status : SaleStatus.values()) {
                if (status.name().equalsIgnoreCase(saleStatus)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Id
    private String id;

    @NotNull
    @Size(min = 2, max = 10)
    @Indexed(unique = true, background = true)
    private String code;

    @NotNull
    private String clientId;

    private List<ProductQuantity> productList;

    @NotNull
    private Instant dataSale;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private SaleStatus status;

    public Sale(String clientId) {
        this.clientId = clientId;
        this.status = SaleStatus.INICIADA;
        this.totalPrice = BigDecimal.ZERO;
        this.dataSale = Instant.now();
        this.productList = new ArrayList<>();
    }

	public void validateStatus() {
		if (this.status == SaleStatus.FINALIZADA || this.status == SaleStatus.CANCELADA) {
			throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR VENDA FINALIZADA OU CANCELADA");
		}
	}

    public void recalculateTotalPrice() {
        validateStatus();
        BigDecimal total = BigDecimal.ZERO;
        for (ProductQuantity productQuantity : productList) {
            BigDecimal _price = productQuantity.getPrice().multiply(BigDecimal.valueOf(productQuantity.getQuantity()));
            total = total.add(_price);
        }
        this.totalPrice = total;
    }

    public void addProduct(ProductQuantity productQuantity) {
        validateStatus();
        Optional<ProductQuantity> optional = productList.stream().filter((f) -> f.getProductId().equals(productQuantity.getProductId())).findAny();
        if (optional.isPresent()) {
            ProductQuantity _productQuantity = optional.get();
            Integer _quantity = _productQuantity.getQuantity() + productQuantity.getQuantity();
            _productQuantity.setQuantity(_quantity >= 0 ? _quantity : 0 );
        } else {
            productList.add(productQuantity);
        }
        recalculateTotalPrice();
    }

    public void removeProduct(ProductQuantity productQuantity) {
        validateStatus();
        Optional<ProductQuantity> optional = productList.stream().filter((f) -> f.getProductId().equals(productQuantity.getProductId())).findAny();
        if (optional.isPresent()) {
            ProductQuantity _productQuantity = optional.get();
            if (_productQuantity.getQuantity() <= productQuantity.getQuantity()) {
                productList.remove(_productQuantity);
            } else {
                _productQuantity.setQuantity(_productQuantity.getQuantity() - productQuantity.getQuantity());
            }
            recalculateTotalPrice();
        }
    }

    public void removeAll() {
        validateStatus();
        productList.clear();
        totalPrice = BigDecimal.ZERO;
    }

}
