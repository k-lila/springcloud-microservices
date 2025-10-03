package backend.StockMicroservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "stock")
public class Stock {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true, background = true)
    private String productId;

    @NotNull
    private Integer quantity;

    public Stock(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

}
