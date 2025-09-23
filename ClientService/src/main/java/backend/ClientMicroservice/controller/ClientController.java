package backend.ClientMicroservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.ClientMicroservice.domain.Client;
import backend.ClientMicroservice.service.RegisterClient;
import backend.ClientMicroservice.service.SearchClient;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "clients")
public class ClientController {
    private RegisterClient registerClient;
    private SearchClient searchClient;

    @Autowired
    public ClientController(RegisterClient registerClient, SearchClient searchClient) {
        this.registerClient = registerClient;
        this.searchClient = searchClient;
    }

    @GetMapping
    public ResponseEntity<Page<Client>> search(Pageable pageable) {
        return ResponseEntity.ok(searchClient.search(pageable));
    }

    @GetMapping(value = "isRegistered/{id}")
    public ResponseEntity<Boolean> isRegistered(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(searchClient.isRegistered(id));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Search client by id")
    public ResponseEntity<Client> searchById(@PathVariable(value = "id", required = true) String id) {
        return ResponseEntity.ok(searchClient.searchById(id));
    }

    @GetMapping(value = "/cpf/{cpf}")
    @Operation(summary = "Search client by CPF")
    public ResponseEntity<Client> searchByCPF(@PathVariable(value = "cpf", required = true) String cpf) {
        return ResponseEntity.ok(searchClient.searchByCPF(cpf));
    }

	@PostMapping
	public ResponseEntity<Client> registerClient(@RequestBody @Valid Client cliente) {
		return ResponseEntity.ok(registerClient.registerClient(cliente));
	}

    @PutMapping
    @Operation(summary = "Update a client")
    public ResponseEntity<Client> updateClient(@RequestBody @Valid Client client) {
        return ResponseEntity.ok(registerClient.updateClient(client));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "removes a client by its id")
    public ResponseEntity<Void> removeClient(@PathVariable(value = "id") String id) {
        registerClient.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

}
