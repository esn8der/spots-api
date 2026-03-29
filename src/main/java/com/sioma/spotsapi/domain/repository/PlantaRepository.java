package  com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Planta;

import java.util.List;
import java.util.Optional;

public interface PlantaRepository {
    Planta save(Planta planta);
    Optional<Planta> findById(Long id);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsById(Long id);
    List<Planta> findAll();
}