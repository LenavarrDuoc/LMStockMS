package cl.duoc.lmstockms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
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

    @Test
    void findAllTest(){
        //lista de registros de inventario
        Inventario esperado1 = new Inventario(1L, 3L, 20, true);
        Inventario esperado2 = new Inventario(2L, 5L, 10, true);
        when(inventarioRepository.findAll()).thenReturn(List.of(esperado1, esperado2));
        
        //supuestos datos obtenidos desde LMCatalogoMS al efectuar .findAll() desde el repositorio
        when(toAPICatalogFeign.obtener(3L)).thenReturn(new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        ));
        when(toAPICatalogFeign.obtener(5L)).thenReturn(new ProductoDTO(
            5L, "Clean Code", "Robert C. Martin", "Prentice Hall", "Programación", 2008, 30000.0, 3,
            "978-013-23-5088-4", "desc", 1L, "Distribuidora Libros"
        ));

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
        when(toAPICatalogFeign.obtener(3L)).thenReturn(new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        ));

        InventarioResponseDTO resultado = inventarioService.findById(id);

        assertEquals(esperado.get().getId(), resultado.getId());
        assertNotNull(resultado);
    }

    @Test
    void existsByIdTest(){
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        boolean resultado = inventarioService.existsById(1L);
        assertTrue(resultado);
    }

    @Test
    void findByProductoIdTest(){
        Long productoId = 3L;
        Inventario esperado = new Inventario(1L, productoId, 20, true);
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(esperado);
        
        //supuestos datos obtenidos desde LMCatalogoMS al efectuar .findById() desde el repositorio
        when(toAPICatalogFeign.obtener(3L)).thenReturn(new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        ));

        InventarioResponseDTO resultado = inventarioService.findByProductoId(productoId);

        assertEquals(esperado.getProductoId(), resultado.getProductoId());
        assertNotNull(resultado);
    }

    @Test
    void updateTest(){
        Inventario inventario = new Inventario(1L, 3L, 20, true);
        InventarioUpdateDTO invUpdate = new InventarioUpdateDTO(1L, 3L, 15, false);

        when(inventarioRepository.findById(inventario.getId())).thenReturn(Optional.of(inventario));

        //se corrobora lo que llegó al .save() sea el updateDTO, invocando su argumento 
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ProductoDTO productoDTO = new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        );

        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO);

        InventarioResponseDTO resultado = inventarioService.update(invUpdate);

        assertNotNull(resultado);
        assertEquals(15, resultado.getCantidad());
        assertEquals(false, resultado.getEstado());
        assertEquals(3L, resultado.getProductoId());
    }

    @Test
    void saveTest(){
        ProductoDTO productoDTO = new ProductoDTO(
            3L, "Java for Dummies", "Terry A. Burd", "Wiley", "Programación", 2022, 26725.0, 5,
            "978-111-98-6164-5", "desc", 1L, "Distribuidora Libros"
        );

        when(toAPICatalogFeign.obtener(3L)).thenReturn(productoDTO);

        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        InventarioInputDTO invInput = new InventarioInputDTO(3L, 20, true);

        InventarioResponseDTO resultado = inventarioService.save(invInput);

        assertNotNull(resultado);
        assertEquals(invInput.getProductoId(), resultado.getProductoId());
    }
}
