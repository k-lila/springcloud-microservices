package backend.SaleMicroservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleRequestDTO {
    private String clientId;
    private String code;
    private List<ProductQuantityDTO> productList;
}
