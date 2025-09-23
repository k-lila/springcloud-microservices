package backend.ClientMicroservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "client")
@Getter
@Setter
public class Client {

    @Id
    private String id;

    @NotNull
    @Size(min=1, max=50)
    @Schema(description = "Name", minLength = 1, maxLength = 50, nullable = false)
    private String name;

    @NotNull
    @Indexed(unique = true, background = true)
    @Schema(description = "CPF", nullable = false)
    @Pattern(regexp = "\\d{11}", message = "CPF inválido")
    private String cpf;

	@NotNull
	@Schema(description="Phone", nullable = false) 
    private Long phone;
	
	@NotNull
	@Size(min = 1, max = 50)
	@Indexed(unique = true, background = true)
	@Schema(description="Email", minLength = 1, maxLength=50, nullable = false)
	@Pattern(regexp = ".+@.+\\..+", message = "Email inválido")
	private String email;
    
	@NotNull
	@Size(min = 1, max = 50)
	@Schema(description="Address", minLength = 1, maxLength=50, nullable = false)
    private String address;
    
	@NotNull
	@Schema(description="Residential number", nullable = false) 
    private Integer residentialNumber;
    
	@NotNull
	@Size(min = 1, max = 50)
	@Schema(description="City", minLength = 1, maxLength=50, nullable = false)
    private String city;
    
	@NotNull
	@Size(min = 1, max = 50)
	@Schema(description="State", minLength = 1, maxLength=50, nullable = false)
    private String state;

}
