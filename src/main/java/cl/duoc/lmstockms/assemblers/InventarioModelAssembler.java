package cl.duoc.lmstockms.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import cl.duoc.lmstockms.controllers.InventarioRESTControllerV2;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<InventarioResponseDTO, EntityModel<InventarioResponseDTO>>{

    @Override
    public EntityModel<InventarioResponseDTO> toModel(InventarioResponseDTO inventario){
        return EntityModel.of(inventario,
             // Link "self": Enlace directo a este género específico basado en su ID
                linkTo(methodOn(InventarioRESTControllerV2.class).findById(inventario.getId())).withSelfRel(),
                // Link "inventarios": Enlace de retorno hacia la colección completa v2
                linkTo(methodOn(InventarioRESTControllerV2.class).findAll()).withRel("inventarios")
        );
    }

}
