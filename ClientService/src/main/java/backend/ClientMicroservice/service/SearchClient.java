package backend.ClientMicroservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.ClientMicroservice.domain.Client;
import backend.ClientMicroservice.exception.DomainEntityNotFound;
import backend.ClientMicroservice.repository.IClientRepository;

@Service
public class SearchClient {

    private IClientRepository clientRepository;
    @Autowired
    public SearchClient(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<Client> search(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client searchById(String id) {
        return clientRepository.findById(id).orElseThrow(() -> new DomainEntityNotFound(Client.class, "id", id));
    }

    public Boolean isRegistered(String id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.isPresent() ? true : false;
    }

    public Client searchByCPF(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(() -> new DomainEntityNotFound(Client.class, "cpf", cpf));
    }
}
