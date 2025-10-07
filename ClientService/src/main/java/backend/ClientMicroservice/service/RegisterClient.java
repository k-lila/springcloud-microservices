package backend.ClientMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.ClientMicroservice.domain.Client;
import backend.ClientMicroservice.exception.DomainEntityNotFound;
import backend.ClientMicroservice.repository.IClientRepository;
import jakarta.validation.Valid;

@Service
public class RegisterClient {

    private IClientRepository clientRepository;
    @Autowired
    public RegisterClient(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client registerClient(@Valid Client client) {
        return this.clientRepository.insert(client);
    }

    public Client updateClient(@Valid Client client) {
        if (!clientRepository.existsById(client.getId())) {
            throw new DomainEntityNotFound(Client.class, "id", client.getId());
        }
        return this.clientRepository.save(client);
    }

    public void deleteClient(String id) {
        if (!clientRepository.existsById(id)) {
            throw new DomainEntityNotFound(Client.class, "id", id);
        }
        this.clientRepository.deleteById(id);
    }
}
