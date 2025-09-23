package backend.ClientMicroservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.ClientMicroservice.domain.Client;

@Repository
public interface IClientRepository extends MongoRepository<Client, String> {
    Optional<Client> findByCpf(String cpf);
}
