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
                linkTo(methodOn(InventarioRESTControllerV2.class).findById(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioRESTControllerV2.class).findAll()).withRel("list-all"),
                linkTo(methodOn(InventarioRESTControllerV2.class).findByProductoId(inventario.getProductoId())).withRel("find-by-producto-id")
        );
    }

}
