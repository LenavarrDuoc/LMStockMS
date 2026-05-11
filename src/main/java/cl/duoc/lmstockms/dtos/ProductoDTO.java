package cl.duoc.lmstockms.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {

    private Long id;


    private String titulo;


    private String autor;


    private String editorial;


    private String categoria;


    private Integer anioPublicacion;

    @NotNull @Positive
    private Double precio;

    @NotNull @Min(0)
    private Integer stock;

    @NotBlank
    private String isbn;

    private String descripcion;

    @NotNull(message = "Proveedor requerido")
    private Long proveedorId;

    private String proveedorNombre;
}