package backend.SaleMicroservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import backend.SaleMicroservice.domain.Sale;

public interface ISaleRepository extends MongoRepository<Sale, String> {
    Optional<Sale> findByCode(String code);
    Page<Sale> findByClientId(String clientId, Pageable pageable);
}
