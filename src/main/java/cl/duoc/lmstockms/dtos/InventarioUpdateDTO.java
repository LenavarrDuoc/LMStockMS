package cl.duoc.lmstockms.dtos;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioUpdateDTO {
    @PositiveOrZero
    private Long id;

    @PositiveOrZero
    private Long productoId;

    @PositiveOrZero
    private Integer cantidad;

    @NotNull
    private Boolean estado;
}
