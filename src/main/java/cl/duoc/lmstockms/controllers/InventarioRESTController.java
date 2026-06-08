package cl.duoc.lmstockms.controllers;

import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.services.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventarios")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestion de inventario")
public class InventarioRESTController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioRESTController.class.getName());

    private final InventarioService inventarioService;

    //CREATE:
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Se ha creado registro",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto al hacer solicitud (ej: productoId ya existe)",
                    content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "Crea inventario", description = "Guarda un registro nuevo de inventario")
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> save(@Valid @RequestBody InventarioInputDTO dto){
        String logMsgRequest = "Recibiendo solicitud para crear/guardar inventario.";
        String logMsg = "Solicitud para crear/guardar inventario.";
        logger.info(logMsgRequest);
        InventarioResponseDTO created = inventarioService.save(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + "=> creado con ID Inventario: {}, ID Producto: {}, Cantidad: {}, Estado: {}.", created.getId(), created.getProductoId(), created.getCantidad(), created.getEstado());
        return ResponseEntity.created(location).body(created);
        //devuelve el estado y la locación //devuelve el objeto creado
    }

    //READ:
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se han encontrado registros",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InventarioResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se han encontrado registros",
                    content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @GetMapping
    @Operation(summary = "Lista todo", description = "Muestra todos los registros de inventarios")
    public ResponseEntity<List<InventarioResponseDTO>> findAll(){
        String logMsgRequest = "Recibiendo solicitud para buscar listado de inventarios.";
        String logMsg = "Solicitud para buscar listado de inventarios.";
        logger.info(logMsgRequest);
        List<InventarioResponseDTO> listadoDTO = inventarioService.findAll();

        if (!listadoDTO.isEmpty()){
            logger.info(logMsg + "=> encontrado(s) y enlistado(s).");
            return ResponseEntity.ok(listadoDTO);
        }
        logger.info(logMsg + "=> sin coincidencias (vacío).");
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se ha encontrado el registro por ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se ha encontrado el registro",
                    content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @GetMapping("/{id}")
    @Operation(summary = "Encuentra por ID", description = "Trae el registro perteneciente al ID ingresado")
    public ResponseEntity<InventarioResponseDTO> findById(@PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para buscar inventario por ID: " + id + ".";
        String logMsg = "Solicitud para buscar inventario por ID: " + id + ".";
        logger.info(logMsgRequest);
        InventarioResponseDTO dto = inventarioService.findById(id);
        if (dto != null){
            logger.info(logMsg + "=> encontrado.");
            return ResponseEntity.ok(dto);
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se ha encontrado registro por ID de producto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se ha encontrado registro",
                    content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @GetMapping("/by-id-producto/{productoId}")
    @Operation(summary = "Encuentra por ID", description = "Trae el registro perteneciente a Inventarios según ID de producto")
    public ResponseEntity<InventarioResponseDTO> findByProductoId(@PathVariable Long productoId){
        String logMsgRequest = "Recibiendo solicitud para buscar inventario por ID: " + productoId + ".";
        String logMsg = "Solicitud para buscar inventario por ID: " + productoId + ".";
        logger.info(logMsgRequest);
        InventarioResponseDTO dto = inventarioService.findByProductoId(productoId);
        if (dto != null){
            logger.info(logMsg + "=> encontrado con ID:{}", dto.getId() + ".");
            return ResponseEntity.ok(dto);
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se ha actualizado el registro",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se han encontrado registros",
                    content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    //UPDATE:
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar por ID", description = "Actualiza información de registro perteneciente al ID ingresado")
    public ResponseEntity<InventarioResponseDTO> update(@Valid @RequestBody InventarioUpdateDTO ent, @PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para actualizar inventario con ID: " + id + ".";
        String logMsg = "Solicitud para actualizar inventario con ID: " + id + ".";
        logger.info(logMsgRequest);
        ent.setId(id);
        InventarioResponseDTO updated = inventarioService.update(ent);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(updated.getId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + " => actualizado.");
        return ResponseEntity.status(200).location(location).body(updated);
        //devuelve el estado y la locación //devuelve el objeto creado
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Se ha eliminado el registro",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se ha encontrado el registro",
                    content = @Content(schema = @Schema(hidden = true))
            )
            
        }
    )
    //DELETE:
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar por ID", description = "Elimina el registro perteneciente al ID")
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
