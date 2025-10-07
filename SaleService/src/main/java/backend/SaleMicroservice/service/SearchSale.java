package backend.SaleMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.client.IClientClient;
import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
import backend.SaleMicroservice.exception.ExternalServiceException;
import backend.SaleMicroservice.repository.ISaleRepository;
import feign.FeignException;

@Service
public class SearchSale {

    private final ISaleRepository saleRepository;
    private final IClientClient clientClient;

    @Autowired
    public SearchSale(ISaleRepository saleRepository, IClientClient clientClient) {
        this.saleRepository = saleRepository;
        this.clientClient = clientClient;
    }

    public Sale searchById(String saleId) {
        return saleRepository.findById(saleId).orElseThrow(
            () -> new DomainEntityNotFound(Sale.class, "id", saleId)
        );
    }

    public Sale searchByCode(String saleCode) {
        return saleRepository.findByCode(saleCode).orElseThrow(
            () -> new DomainEntityNotFound(Sale.class, "code", saleCode)
        );
    }

    public Page<Sale> searchByClient(String clientId, Pageable pageable) {
        try {
            clientClient.getClientById(clientId);
        } catch (FeignException e) {
            throw new ExternalServiceException("client-service", e);
        }
        return saleRepository.findByClientId(clientId, pageable);
    }

    public Page<Sale> searchAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

}
