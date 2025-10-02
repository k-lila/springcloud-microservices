package backend.SaleMicroservice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleRequestDTO {
    private String clientId;
    private String code;
    private List<ProductQuantityDTO> productList;
}
