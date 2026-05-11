package cl.duoc.lmstockms.mappers;

import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.models.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioUpdateMapper {
    public Inventario toEntity(Inventario ent, InventarioUpdateDTO dto) {

        if (dto!= null){

            ent.setId(dto.getId());
            ent.setCantidad(dto.getCantidad());
            ent.setCantidad(dto.getCantidad());
            ent.setEstado(dto.getEstado());

            return ent;
        }
        return null;
    }
}
