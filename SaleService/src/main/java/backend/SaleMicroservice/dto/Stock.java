package backend.SaleMicroservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock {
    private String productId;
    private Integer quantity;
}
