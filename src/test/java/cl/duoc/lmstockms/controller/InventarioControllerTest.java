package cl.duoc.lmstockms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import cl.duoc.lmstockms.controllers.InventarioRESTController;
import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.exceptions.IdExisteException;
import cl.duoc.lmstockms.exceptions.IdNoExisteException;
import cl.duoc.lmstockms.services.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = InventarioRESTController.class, excludeAutoConfiguration = ServletWebSecurityAutoConfiguration.class)
public class InventarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    private InventarioResponseDTO dtoResponse1;
    private InventarioResponseDTO dtoResponse2;
    private InventarioInputDTO dtoInput;
    private InventarioUpdateDTO dtoUpdate;

    private final IdNoExisteException idNoExisteException = new IdNoExisteException("ID de registro no existe.");
    private final IdExisteException idExisteException = new IdExisteException("Producto ya registrado.");

    @BeforeEach
    void setUp(){
        dtoResponse1 = new InventarioResponseDTO(1L, 20, 3L, "Java for Dummies",
                "Terry A. Burd", 2022, "978-111-98-6164-5",
                "Programación", "Wiley", true);
        dtoResponse2 = new InventarioResponseDTO(2L, 10, 5L, "Clean Code",
                "Robert C. Martin", 2008, "978-013-23-5088-4",
                "Programación", "Prentice Hall", true);

        dtoInput = new InventarioInputDTO(3L, 20, true);
        dtoUpdate = new InventarioUpdateDTO(1L, 3L, 79, false);

    }

    @Test
    void findAllTest() throws Exception{
        when(inventarioService.findAll()).thenReturn(List.of(dtoResponse1, dtoResponse2));
        mockMvc.perform(get("/api/v1/inventarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productoNombre").value("Java for Dummies"))
                .andExpect(jsonPath("$[1].productoNombre").value("Clean Code"));
        verify(inventarioService).findAll();
    }

    @Test
    void findAllEmptyTest() throws Exception{
        when(inventarioService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/inventarios"))
                .andExpect(status().isNoContent());
        verify(inventarioService).findAll();
    }

    @Test
    void findByIdTest() throws Exception{
        when(inventarioService.findById(dtoResponse1.getId())).thenReturn(dtoResponse1);
        mockMvc.perform(get("/api/v1/inventarios/{id}", dtoResponse1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoNombre").value("Java for Dummies"));
        verify(inventarioService).findById(dtoResponse1.getId());
    }

    @Test
    void findByIdNotFound() throws Exception{
        when(inventarioService.findById(dtoResponse1.getId())).thenThrow(idNoExisteException);
        mockMvc.perform(get("/api/v1/inventarios/{id}", dtoResponse1.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(idNoExisteException.getMessage()));
        verify(inventarioService).findById(dtoResponse1.getId());
    }

    @Test
    void createTest() throws Exception{
        when(inventarioService.save(any(InventarioInputDTO.class))).thenReturn(dtoResponse1);
        mockMvc.perform(post("/api/v1/inventarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(dtoInput.getProductoId()));
        verify(inventarioService).save(dtoInput);
    }

    @Test
    void createRegistroYaExisteTests() throws Exception{
        when(inventarioService.save(any(InventarioInputDTO.class))).thenThrow(idExisteException);
        mockMvc.perform(post("/api/v1/inventarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(idExisteException.getMessage()));
        verify(inventarioService).save(any(InventarioInputDTO.class));
    }

    @Test
    void createProductoNoExiste() throws Exception{
        when(inventarioService.save(any(InventarioInputDTO.class))).thenThrow(idNoExisteException);
        mockMvc.perform(post("/api/v1/inventarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(idNoExisteException.getMessage()));
        verify(inventarioService).save(any(InventarioInputDTO.class));
    }

    @Test
    void findByProductoIdTest() throws Exception{
        when(inventarioService.findByProductoId(dtoInput.getProductoId())).thenReturn(dtoResponse1);
        mockMvc.perform(get("/api/v1/inventarios/by-id-producto/{id}", dtoInput.getProductoId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(dtoInput.getProductoId()));
        verify(inventarioService).findByProductoId(dtoInput.getProductoId());
    }

    @Test
    void findByIdProductoNoEncontrado() throws Exception{
        when(inventarioService.findByProductoId(dtoInput.getProductoId())).thenThrow(idNoExisteException);
        mockMvc.perform(get("/api/v1/inventarios/by-id-producto/{id}", dtoInput.getProductoId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(idNoExisteException.getMessage()));
        verify(inventarioService).findByProductoId(dtoInput.getProductoId());
    }

    @Test
    void updateTest() throws Exception {
        when(inventarioService.update(dtoUpdate)).thenReturn(dtoResponse1);
        mockMvc.perform(put("/api/v1/inventarios/{id}", dtoUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(dtoUpdate.getProductoId()));
        verify(inventarioService).update(dtoUpdate);
    }

    @Test
    void updateNoEncontradoTest() throws Exception{
        when(inventarioService.update(dtoUpdate)).thenThrow(idNoExisteException);
        mockMvc.perform(put("/api/v1/inventarios/{id}", dtoUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(idNoExisteException.getMessage()));
        verify(inventarioService).update(dtoUpdate);
    }

    @Test
    void deleteByIdTest() throws Exception{
        Long id = 1L;
        when(inventarioService.deleteById(id)).thenReturn(true);
        mockMvc.perform(delete("/api/v1/inventarios/{id}", id))
                .andExpect(status().isNoContent());
        verify(inventarioService).deleteById(id);
    }

    @Test
    void deleteByIdNoEncontradoTest() throws Exception{
        Long id = 1L;
        when(inventarioService.deleteById(id)).thenThrow(idNoExisteException);
        mockMvc.perform(delete("/api/v1/inventarios/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(idNoExisteException.getMessage()));
        verify(inventarioService).deleteById(id);
    }
}