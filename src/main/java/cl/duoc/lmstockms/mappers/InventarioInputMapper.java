package cl.duoc.lmstockms.mappers;

import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.models.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioInputMapper {

    public Inventario toEntity(InventarioInputDTO dto){

        if (dto != null){
            Inventario ent = new Inventario();

            ent.setProductoId(dto.getProductoId());
            ent.setCantidad(dto.getCantidad());
            ent.setEstado(dto.getEstado());

            return ent;
        }
        return null;
    }
}
