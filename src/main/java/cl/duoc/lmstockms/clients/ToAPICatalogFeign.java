package cl.duoc.lmstockms.clients;

import cl.duoc.lmstockms.dtos.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//APIName = libreriaMarketCatalogService
//APIURL original sin Eureka y Gateway: url = "http://localhost:8088/api/v1/productos"
@FeignClient(name = "LMCatalogMS", path = "/api/v1/productos")
public interface ToAPICatalogFeign {

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id);
}
