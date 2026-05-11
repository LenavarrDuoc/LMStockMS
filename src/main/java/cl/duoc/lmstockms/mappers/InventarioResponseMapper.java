package cl.duoc.lmstockms.mappers;

import cl.duoc.lmstockms.clients.ToAPICatalogFeign;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.ProductoDTO;
import cl.duoc.lmstockms.models.Inventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventarioResponseMapper {
    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

public InventarioResponseDTO toDto (Inventario ent) {

    if (ent != null) {

        InventarioResponseDTO dto = new InventarioResponseDTO();
        ProductoDTO productoDTO = toAPICatalogFeign.obtener(ent.getId());


        dto.setId(ent.getId());
        dto.setProductoId(ent.getProductoId());
        dto.setCantidad(ent.getCantidad());
        //TODO: Llamar a entidad producto via FEIGN y dar detalles del producto en esta sección
        ToAPICatalogFeign toAPICatalogFeign = null;
        dto.setProductoNombre(productoDTO.getTitulo());
        dto.setProductoAutor(productoDTO.getAutor());
        dto.setProductoCategoria(productoDTO.getCategoria());
        dto.setProductoEditorial(productoDTO.getEditorial());
        dto.setProductoAnioPublicacion(productoDTO.getAnioPublicacion());
        dto.setProductoIsbn(productoDTO.getIsbn());
        dto.setEstado(ent.getEstado());

        return dto;
    }
    return null;
}
}
