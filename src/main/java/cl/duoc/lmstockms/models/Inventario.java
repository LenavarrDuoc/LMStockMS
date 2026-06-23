package cl.duoc.lmstockms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fk_producto_id", nullable = false, unique = true)
    private Long productoId;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    //TODO: Falta especificar un @Pattern que permita ciertos estados. Estado debería ser entidad aparte con más de dos opciones y que colabore.
    @Column(name = "estado", nullable = false)
    private Boolean estado;
}
