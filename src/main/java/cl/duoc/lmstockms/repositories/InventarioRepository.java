package cl.duoc.lmstockms.repositories;

import cl.duoc.lmstockms.models.Inventario;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductoId(@PositiveOrZero Long productoId);

    Inventario findByProductoId(Long id);
}
