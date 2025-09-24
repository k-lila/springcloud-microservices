package backend.ProductMicroservice.domain;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "product")
@Getter
@Setter
public class Product {
	
	public enum Status {
		ACTIVE, INACTIVE
	}

    @Id
    private String id;

    @NotNull
    @Size(min=1, max=50)
    @Schema(description = "Name", minLength = 1, maxLength = 50, nullable = false)
    private String name;

    @NotNull
    @Indexed(unique = true, background = true)
    @Schema(description = "Code", nullable = false)
    private String code;

	@NotNull
	@Size(min = 1, max = 140)
    private String description;
	
	@NotNull
	private BigDecimal price;

	private Status status;

}
