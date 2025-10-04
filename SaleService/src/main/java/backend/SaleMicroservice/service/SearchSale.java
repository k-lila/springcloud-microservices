package backend.SaleMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.exception.DomainEntityNotFound;
import backend.SaleMicroservice.repository.ISaleRepository;

@Service
public class SearchSale {

    private final ISaleRepository saleRepository;

    @Autowired
    public SearchSale(ISaleRepository saleRepository) {
        this.saleRepository = saleRepository;
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

    public Page<Sale> searchByClient(String id, Pageable pageable) {
        return saleRepository.findByClientId(id, pageable);
    }

    public Page<Sale> searchAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

}
