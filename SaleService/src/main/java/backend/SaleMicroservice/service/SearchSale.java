package backend.SaleMicroservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.repository.ISaleRepository;

@Service
public class SearchSale {

    private final ISaleRepository saleRepository;

    @Autowired
    public SearchSale(ISaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Sale findById(String saleId) {
        return saleRepository.findById(saleId).orElseThrow(
            () -> new RuntimeException("VENDA NÃO ENCONTRADA, ID: " + saleId)
        );
    }

    public Sale findByCode(String saleCode) {
        return saleRepository.findByCode(saleCode).orElseThrow(
            () -> new RuntimeException("VENDA NÃO ENCONTRADA, CÓDIGO: " + saleCode)
        );
    }

    public Page<Sale> findByClient(String id, Pageable pageable) {
        return saleRepository.findByClientId(id, pageable);
    }

    public Page<Sale> searchAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

}
