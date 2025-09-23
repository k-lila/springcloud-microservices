package backend.ClientMicroservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import backend.ClientMicroservice.controller.ClientController;
import backend.ClientMicroservice.domain.Client;
import backend.ClientMicroservice.exception.DomainEntityNotFound;
import backend.ClientMicroservice.repository.IClientRepository;

@SpringBootTest
@ActiveProfiles("test")
class ClientMicroserviceApplicationTests {
    
    @Autowired
	private MongoTemplate mongoTemplate;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private ClientController clientController;

    private Client generateClient(String cpf) {
        Client client = new Client();
        client.setCpf(cpf);
        client.setName("Nome teste");
        client.setEmail(cpf + "emailteste@teste.com");
        client.setPhone(98765L);
        client.setState("Estado teste");
        client.setCity("Cidade teste");
        client.setResidentialNumber(123);
        client.setAddress("Endereço teste");
        return client;
    }

    @AfterEach
    void clean() {
        clientRepository.deleteAll();
    }

	@Test
	void contextLoads() {
	}

	@Test
    void testMongoConnection() {
        String string_test = "string test";
        if (!mongoTemplate.collectionExists(string_test)) {
            mongoTemplate.createCollection(string_test);
        }
        Document doc = new Document();
        doc.put("message", "Teste Mongo");
        mongoTemplate.insert(doc, string_test);
		List<Document> found = mongoTemplate.findAll(Document.class, string_test);
        assertFalse(found.isEmpty());
        assertEquals("Teste Mongo", found.get(0).get("message"));
    }

    @Test
    void testRegisterClient() {
        Client client = generateClient("98765432100");
        ResponseEntity<Client> response = clientController.registerClient(client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client found = mongoTemplate.findById(response.getBody().getId(), Client.class);
        assertNotNull(found);
    }

    @Test
    void testSearchAll() {
        for (int i = 0; i < 5; i++) {
            Client c = generateClient(i + "9876543210");
            ResponseEntity<Client> response = clientController.registerClient(c);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        PageRequest pageable = PageRequest.of(1, 10);
        ResponseEntity<Page<Client>> responsePage = clientController.search(pageable);
        assertEquals(HttpStatus.OK, responsePage.getStatusCode());
        assertEquals(5, responsePage.getBody().getTotalElements());
    }

    @Test
    void testIfClientIsRegistered() {
        Client client = generateClient("98765432100");
        ResponseEntity<Client> response = clientController.registerClient(client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean isRegistered = clientController.isRegistered(response.getBody().getId()).getBody();
        assertTrue(isRegistered);
        Boolean notRegistered = clientController.isRegistered("teste").getBody();
        assertFalse(notRegistered);
    }

    @Test
    void testSearchClientByIdAndCpf() {
        Client client = generateClient("00123456789");
        ResponseEntity<Client> response = clientController.registerClient(client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client foundById = clientController.searchById(response.getBody().getId()).getBody();
        Client foundByCpf = clientController.searchByCPF("00123456789").getBody();
        assertEquals(foundById.getId(), foundByCpf.getId());
        assertEquals(foundById.getEmail(), foundByCpf.getEmail());
        assertEquals(client.getAddress(), foundById.getAddress());
        assertEquals(client.getAddress(), foundByCpf.getAddress());
    }

    @Test
    void testUpdateClient() {
        Client client = generateClient("98765432100");
        ResponseEntity<Client> response = clientController.registerClient(client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client updatedClient = response.getBody();
        updatedClient.setAddress("update");
        updatedClient.setCity("update");
        updatedClient.setState("update");
        ResponseEntity<Client> updateResponse = clientController.updateClient(updatedClient);
        assertEquals("update", updateResponse.getBody().getAddress());
        assertEquals("update", updateResponse.getBody().getCity());
        assertEquals("update", updateResponse.getBody().getState());
    }


    @Test
    void testDeleteClient() {
        Client client = generateClient("98765432100");
        ResponseEntity<Client> response = clientController.registerClient(client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<Void> deleteResponse = clientController.removeClient(response.getBody().getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertThrows(DomainEntityNotFound.class, () -> {
            clientController.searchById(response.getBody().getId());
        });
    }

}
