package cl.duoc.lmstockms.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.lmstockms.assemblers.InventarioModelAssembler;
import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.services.InventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/inventarios")
@RequiredArgsConstructor
@Tag(name = "Stock v2", description = "Gestion de inventario")
public class InventarioRESTControllerV2 {
    
    //TO DO: ADAPTAR LOGS A CONTEXTO HATEOAS
    private static final Logger logger = LoggerFactory.getLogger(InventarioRESTController.class.getName());
    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;

    //CREATE:
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<InventarioResponseDTO>> save(@Valid @RequestBody InventarioInputDTO dto){
        String logMsgRequest = "Recibiendo solicitud para crear/guardar inventario.";
        String logMsg = "Solicitud para crear/guardar inventario.";
        logger.info(logMsgRequest);

        InventarioResponseDTO created = inventarioService.save(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        
        logger.info(logMsg + "=> creado con ID Inventario: {}, ID Producto: {}, Cantidad: {}, Estado: {}.", created.getId(),
                                                                                                            created.getProductoId(), 
                                                                                                            created.getCantidad(), 
                                                                                                            created.getEstado());
                                                                                                            
        return ResponseEntity.created(linkTo(methodOn(InventarioRESTControllerV2.class)
                                      .findById(created.getId()))
                                      .toUri())
                                      .body(assembler.toModel(created));
    }

    //READ:
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<InventarioResponseDTO>> findAll(){
        String logMsgRequest = "HATEOAS Recibiendo solicitud para buscar listado de inventarios.";
        String logMsg = "Solicitud para buscar listado de inventarios.";
        logger.info(logMsgRequest);
        List<EntityModel<InventarioResponseDTO>> listadoDTO = inventarioService.findAll().stream()
                                                                                        .map(assembler::toModel)
                                                                                        .collect(Collectors.toList());;

        return CollectionModel.of(listadoDTO,
                linkTo(methodOn(InventarioRESTControllerV2.class).findAll()).withSelfRel());
    }
  
    
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<InventarioResponseDTO>> findById(@PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para buscar inventario por ID: " + id + ".";
        String logMsg = "Solicitud para buscar inventario por ID: " + id + ".";
        logger.info(logMsgRequest);
        InventarioResponseDTO dto = inventarioService.findById(id);
        if (dto != null){
            logger.info(logMsg + "=> encontrado.");
            return ResponseEntity.ok(assembler.toModel(dto));
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/by-id-producto/{productoId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<InventarioResponseDTO>> findByProductoId(@PathVariable Long productoId){
        String logMsgRequest = "Recibiendo solicitud para buscar inventario por ID: " + productoId + ".";
        String logMsg = "Solicitud para buscar inventario por ID: " + productoId + ".";
        logger.info(logMsgRequest);
        InventarioResponseDTO dto = inventarioService.findByProductoId(productoId);
        if (dto != null){
            logger.info(logMsg + "=> encontrado con ID:{}", dto.getId() + ".");
            return ResponseEntity.ok(assembler.toModel(dto));
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    //UPDATE:
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<InventarioResponseDTO>> update(@Valid @RequestBody InventarioUpdateDTO ent, @PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para actualizar inventario con ID: " + id + ".";
        String logMsg = "Solicitud para actualizar inventario con ID: " + id + ".";
        logger.info(logMsgRequest);
        ent.setId(id);
        InventarioResponseDTO updated = inventarioService.update(ent);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(updated.getId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + " => actualizado.");
        return ResponseEntity.status(200).location(location).body(assembler.toModel(updated));
        //devuelve el estado y la locación //devuelve el objeto creado
    }

    //DELETE:
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para borrar inventario con ID: " + id + ".";
        String logMsg = "Solicitud para borrar inventario con ID: " + id + ".";
        logger.info(logMsgRequest);
        if(inventarioService.deleteById(id)){
            logger.info(logMsg + " => encontrado y borrado.");
            return ResponseEntity.noContent().build();
        }
        logger.info(logMsg + " => no encontrado.");
        return ResponseEntity.notFound().build();
    }
}
