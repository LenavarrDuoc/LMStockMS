package cl.duoc.lmstockms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import cl.duoc.lmstockms.clients.ToAPICatalogFeign;
import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.ProductoDTO;
import cl.duoc.lmstockms.exceptions.IdExisteException;
import cl.duoc.lmstockms.exceptions.IdNoExisteException;
import cl.duoc.lmstockms.mappers.InventarioInputMapper;
import cl.duoc.lmstockms.models.Inventario;
import cl.duoc.lmstockms.repositories.InventarioRepository;
import cl.duoc.lmstockms.services.InventarioService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BORRAR_InventarioServiceTest {

    @Autowired
    private InventarioService inventarioService;

    @MockitoBean
    private InventarioRepository inventarioRepository;

    @MockitoBean
    private ToAPICatalogFeign toAPICatalogFeign;

    @Autowired
    private InventarioInputMapper inputMapper;
    
    //@DisplayName("")
    @Test
    public void testFindAll() {
        // Arrange: dos entidades Inventario, cada una con su propio productoId
        Inventario inv1 = new Inventario(1L, 3L, 10, true);
        Inventario inv2 = new Inventario(2L, 5L, 20, true);

        when(inventarioRepository.findAll()).thenReturn(List.of(inv1, inv2));

        // El mapper llama a Feign por CADA elemento, así que hay que stubear ambos productoId
        when(toAPICatalogFeign.obtener(3L)).thenReturn(new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-1119861645", "desc", 1L, "Distribuidora Libros"
        ));
        when(toAPICatalogFeign.obtener(5L)).thenReturn(new ProductoDTO(
            5L, "Clean Code", "Robert C. Martin", "Prentice Hall", "Programación", 2008, 30000.0, 3,
            "978-0132350884", "desc", 1L, "Distribuidora Libros"
        ));

        // Act
        List<InventarioResponseDTO> resultado = inventarioService.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Java for Dummies", resultado.get(0).getProductoNombre());
        assertEquals("Clean Code", resultado.get(1).getProductoNombre());

        verify(inventarioRepository).findAll();
        verify(toAPICatalogFeign).obtener(3L);
        verify(toAPICatalogFeign).obtener(5L);
    }

    @Test
    void save_productoYaExisteEnInventario() {
        InventarioInputDTO inputDTO = new InventarioInputDTO(3L, 10, true);

        when(inventarioRepository.existsByProductoId(3L)).thenReturn(true);

        assertThrows(IdExisteException.class, () -> inventarioService.save(inputDTO));

        // Si ya existe, no debe ni consultar el catálogo ni guardar nada
        verify(toAPICatalogFeign, never()).obtener(any());
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void save_productoNoExisteEnCatalogo() {
        InventarioInputDTO inputDTO = new InventarioInputDTO(3L, 10, true);

        when(inventarioRepository.existsByProductoId(3L)).thenReturn(false);
        when(toAPICatalogFeign.obtener(3L)).thenReturn(null);

        assertThrows(IdNoExisteException.class, () -> inventarioService.save(inputDTO));

        verify(inventarioRepository, never()).save(any());
    }

    @Test
    public void save_datosValidos(){
        InventarioInputDTO inputDTO = new InventarioInputDTO(3L, 10, true);
        Inventario inventarioEntity = inputMapper.toEntity(inputDTO);

        when(toAPICatalogFeign.obtener(3L)).thenReturn(new ProductoDTO(
            3L, "Java for Dummies","Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5, "978-111-98-6164-5",
            "Learn to write practical, reusable code with the straight forward tutorials and tips in the newest edition of this For Dummies bestseller",
            1L,
            "Distribuidora Libros"
        ));

        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioEntity);
        InventarioResponseDTO saved = assertDoesNotThrow(() -> inventarioService.save(inputDTO));

        assertNotNull(saved);
        verify(inventarioRepository).save(any(Inventario.class));
    }
}
