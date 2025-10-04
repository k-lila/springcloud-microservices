package backend.SaleMicroservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.SaleMicroservice.domain.Sale;
import backend.SaleMicroservice.dto.SaleRequestDTO;
import backend.SaleMicroservice.dto.SaleResponseDTO;
import backend.SaleMicroservice.service.RegisterSale;
import backend.SaleMicroservice.service.SearchSale;
import backend.SaleMicroservice.service.UpdateSaleStatus;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final RegisterSale registerSale;
    private final SearchSale searchSale;
    private final UpdateSaleStatus updateSaleStatus;

    @Autowired
    public SaleController(RegisterSale registerSale, SearchSale searchSale, UpdateSaleStatus updateSaleStatus) {
        this.registerSale = registerSale;
        this.searchSale = searchSale;
        this.updateSaleStatus = updateSaleStatus;
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> registerSale(@RequestBody SaleRequestDTO request) {
        Sale sale = registerSale.registerSale(request);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @PutMapping("/{id}/addProducts")
    public ResponseEntity<SaleResponseDTO> addProduct(
            @PathVariable String id,
            @RequestParam String productCode,
            @RequestParam Integer quantity) {
        
        Sale sale = registerSale.addProduct(id, productCode, quantity);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @PutMapping("/{id}/removeProducts")
    public ResponseEntity<SaleResponseDTO> removeProduct(
            @PathVariable String id,
            @RequestParam String productCode,
            @RequestParam Integer quantity) {
        Sale sale = registerSale.removeProduct(id, productCode, quantity);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @PutMapping("/{id}/removeAll")
    public ResponseEntity<SaleResponseDTO> removeAll(@PathVariable String id) {
        Sale sale = registerSale.removeAll(id);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> searchById(@PathVariable String id) {
        Sale sale = searchSale.searchById(id);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SaleResponseDTO> searchByCode(@PathVariable String code) {
        Sale sale = searchSale.searchByCode(code);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<Page<SaleResponseDTO>> searchByClient(@PathVariable String id, Pageable pageable) {
        Page<Sale> clientSales = searchSale.searchByClient(id, pageable);
        return ResponseEntity.ok(clientSales.map(SaleResponseDTO::fromEntity));
    }

    @GetMapping
    public ResponseEntity<Page<SaleResponseDTO>> searchAllSales(Pageable pageable) {
        Page<Sale> sales = searchSale.searchAll(pageable);
        return ResponseEntity.ok(sales.map(SaleResponseDTO::fromEntity)); 
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<SaleResponseDTO> closeSale(@PathVariable String id) {
        Sale sale = updateSaleStatus.closeSale(id);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable String id) {
        Sale sale = updateSaleStatus.cancelSale(id);
        return ResponseEntity.ok(SaleResponseDTO.fromEntity(sale));
    }


}
