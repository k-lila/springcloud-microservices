package backend.SaleMicroservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductQuantityDTO {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
