package cl.duoc.lmstockms.dtos;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioInputDTO {

    @PositiveOrZero
    private Long productoId;

    @PositiveOrZero
    private Integer cantidad;

    @NotNull
    private Boolean estado;

}
