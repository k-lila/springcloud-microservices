package backend.SaleMicroservice.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQuantity {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
