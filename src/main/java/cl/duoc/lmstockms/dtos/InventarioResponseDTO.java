package cl.duoc.lmstockms.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {


    private Long id;

    private Integer cantidad;

    private Long productoId;

    private String productoNombre;

    private String productoAutor;

    private Integer productoAnioPublicacion;

    private String productoIsbn;

    private String productoCategoria;

    private String productoEditorial;

    @NotNull
    private Boolean estado;

}
