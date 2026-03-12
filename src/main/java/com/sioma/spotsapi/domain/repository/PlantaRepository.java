package  com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Planta;

public interface PlantaRepository {

    Planta save(Planta planta);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsById(Long id);

}