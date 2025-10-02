package backend.SaleMicroservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.domain.Sale.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponseDTO {
    private String id;
    private String code;
    private String clientId;
    private List<ProductQuantityDTO> productList;
    private Instant dataSale;
    private BigDecimal totalPrice;
    private SaleStatus status;

    public static SaleResponseDTO fromEntity(Sale sale) {
        List<ProductQuantityDTO> productDTOs = sale.getProductList().stream()
            .map(pq -> new ProductQuantityDTO(pq.getProductId(), pq.getQuantity(), pq.getPrice()))
            .toList();
        return new SaleResponseDTO(
            sale.getId(),
            sale.getCode(),
            sale.getClientId(),
            productDTOs,
            sale.getDataSale(),
            sale.getTotalPrice(),
            sale.getStatus()
        );
    }
}
