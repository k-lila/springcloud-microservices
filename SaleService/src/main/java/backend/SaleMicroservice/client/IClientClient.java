package backend.SaleMicroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import backend.SaleMicroservice.dto.ClientDTO;

@FeignClient(name = "client-service", url = "http://localhost:8081")
public interface IClientClient {
    @GetMapping("/clients/{id}")
    ClientDTO getClientById(@PathVariable(value = "id") String id);
}
