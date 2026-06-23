package cl.duoc.lmstockms;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import cl.duoc.lmstockms.exceptions.IdExisteException;
import cl.duoc.lmstockms.exceptions.IdNoExisteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import cl.duoc.lmstockms.clients.ToAPICatalogFeign;
import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.dtos.ProductoDTO;
import cl.duoc.lmstockms.models.Inventario;
import cl.duoc.lmstockms.repositories.InventarioRepository;
import cl.duoc.lmstockms.services.InventarioService;

@SpringBootTest
public class InventarioServiceTest {

    @MockitoBean
    private InventarioRepository inventarioRepository;

    @Autowired
    private InventarioService inventarioService;

    @MockitoBean
    private ToAPICatalogFeign toAPICatalogFeign;

    private ProductoDTO productoDTO_ej1;
    private ProductoDTO productoDTO_ej2;

    @BeforeEach
    void setUp(){
        productoDTO_ej1 = new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        );

        productoDTO_ej2 = new ProductoDTO(
            5L, "Clean Code", "Robert C. Martin", "Prentice Hall", "Programación", 2008, 30000.0, 3,
            "978-013-23-5088-4", "desc", 1L, "Distribuidora Libros"
        );
    }

    @Test
    void findAllTest(){
        //lista de registros de inventario
        Inventario esperado1 = new Inventario(1L, 3L, 20, true);
        Inventario esperado2 = new Inventario(2L, 5L, 10, true);
        when(inventarioRepository.findAll()).thenReturn(List.of(esperado1, esperado2));
        
        //supuestos datos obtenidos desde LMCatalogoMS al efectuar .findAll() desde el repositorio
        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO_ej1);
        when(toAPICatalogFeign.obtener(5L)).thenReturn(productoDTO_ej2);

        List<InventarioResponseDTO> resultados = inventarioService.findAll();

        assertEquals(2, resultados.size());
        assertEquals("Java for Dummies", resultados.get(0).getProductoNombre());
        assertEquals("Clean Code", resultados.get(1).getProductoNombre());

        verify(inventarioRepository).findAll();
        verify(toAPICatalogFeign).obtener(3L);
        verify(toAPICatalogFeign).obtener(5L);
    }

    @Test
    void findByIdTest(){
        Long id = 1L;
        Optional<Inventario> esperado = Optional.of(new Inventario(id, 3L, 20, true));
        when(inventarioRepository.findById(id)).thenReturn(esperado);
        
        //supuestos datos obtenidos desde LMCatalogoMS al efectuar .findById() desde el repositorio
        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO_ej1);

        InventarioResponseDTO resultado = inventarioService.findById(id);

        assertEquals(esperado.get().getId(), resultado.getId());
        assertNotNull(resultado);

        verify(inventarioRepository).findById(id);
        verify(toAPICatalogFeign).obtener(3L);
    }

    @Test
    void findByIdExceptionIdNoExisteTest(){
        when(inventarioRepository.findById(3L)).thenReturn(Optional.empty());

        IdNoExisteException exception = assertThrows(IdNoExisteException.class, () -> inventarioService.findById(3L));

        String expectedMessage = "ID de registro no existe.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository).findById(3L);
    }

    @Test
    void existsByIdTest(){
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        boolean resultado = inventarioService.existsById(1L);
        assertTrue(resultado);

        verify(inventarioRepository).existsById(1L);
    }

    @Test
    void findByProductoIdTest(){
        Long productoId = 3L;
        Inventario esperado = new Inventario(1L, productoId, 20, true);
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(esperado);
        
        //supuestos datos obtenidos desde LMCatalogoMS al efectuar .findById() desde el repositorio
        when(toAPICatalogFeign.obtener(productoId)).thenReturn(productoDTO_ej1);

        InventarioResponseDTO resultado = inventarioService.findByProductoId(productoId);

        assertEquals(esperado.getProductoId(), resultado.getProductoId());
        assertNotNull(resultado);

        verify(inventarioRepository).findByProductoId(productoId);
        verify(toAPICatalogFeign).obtener(productoId);
    }

    @Test
    void findByProductoIdExceptionIdNoExisteTest(){
        when(inventarioRepository.findByProductoId(3L)).thenReturn(null);
        IdNoExisteException exception = assertThrows(IdNoExisteException.class, () -> inventarioService.findByProductoId(3L));

        String expectedMessage = "ID de producto no existe.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository).findByProductoId(3L);
    }

    @Test
    void updateTest(){
        Inventario inventario = new Inventario(1L, 3L, 20, true);
        InventarioUpdateDTO invUpdate = new InventarioUpdateDTO(1L, 3L, 15, false);

        when(inventarioRepository.findById(inventario.getId())).thenReturn(Optional.of(inventario));

        //se corrobora lo que llegó al .save() sea el updateDTO, invocando su argumento 
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO_ej1);

        InventarioResponseDTO resultado = inventarioService.update(invUpdate);

        assertNotNull(resultado);
        assertEquals(15, resultado.getCantidad());
        assertEquals(false, resultado.getEstado());
        assertEquals(3L, resultado.getProductoId());

        verify(inventarioRepository).findById(inventario.getId());
        verify(inventarioRepository).save(any(Inventario.class));
        verify(toAPICatalogFeign).obtener(3L);
    }

    @Test
    void updateExceptionIdNoExisteTest(){
        when(inventarioRepository.findById(1L)).thenReturn(Optional.empty());
        InventarioUpdateDTO invUpdate = new InventarioUpdateDTO(1L, 3L, 15, false);

        IdNoExisteException exception = assertThrows(IdNoExisteException.class, () -> inventarioService.update(invUpdate));

        String expectedMessage = "ID de registro no existe.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void saveTest(){
        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO_ej1);
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(inventarioRepository.existsByProductoId(3L)).thenReturn(false);

        InventarioInputDTO invInput = new InventarioInputDTO(3L, 20, true);

        InventarioResponseDTO resultado = inventarioService.save(invInput);

        assertNotNull(resultado);
        assertEquals(invInput.getProductoId(), resultado.getProductoId());

        verify(toAPICatalogFeign, times(2)).obtener(3L);
        verify(inventarioRepository).save(any(Inventario.class));
        verify(inventarioRepository).existsByProductoId(3L);
    }

    @Test
    void saveExceptionProductoYaRegistradoTest(){
        Inventario registro = new Inventario(1L, 3L, 20, true);
        when(inventarioRepository.existsByProductoId(registro.getProductoId())).thenReturn(true);

        InventarioInputDTO invInput = new InventarioInputDTO(3L, 20, true);
        IdExisteException exception = assertThrows(IdExisteException.class, () -> inventarioService.save(invInput));

        String expectedMessage = "Producto ya registrado.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository).existsByProductoId(registro.getProductoId());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void saveExceptionProductoNoExisteTest(){
        when(toAPICatalogFeign.obtener(3L)).thenReturn(null);

        InventarioInputDTO invInput = new InventarioInputDTO(3L, 20, true);
        IdNoExisteException exception = assertThrows(IdNoExisteException.class, () -> inventarioService.save(invInput));

        String expectedMessage = "ID de producto no existe.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository).existsByProductoId(3L);
        verify(toAPICatalogFeign).obtener(3L);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void deleteTest(){
        when(inventarioRepository.existsById(3L)).thenReturn(true);
        boolean resultado = inventarioService.deleteById(3L);
        assertTrue(resultado);
        verify(inventarioRepository).existsById(3L);
        verify(inventarioRepository).deleteById(3L);
    }

    @Test
    void deleteExceptionIdNoExisteTest(){
        when(inventarioRepository.existsById(3L)).thenReturn(false);
        IdNoExisteException exception = assertThrows(IdNoExisteException.class, () -> inventarioService.deleteById(3L));

        String expectedMessage = "ID de registro no existe.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(inventarioRepository).existsById(3L);
        verify(inventarioRepository, never()).deleteById(3L);
    }

}
