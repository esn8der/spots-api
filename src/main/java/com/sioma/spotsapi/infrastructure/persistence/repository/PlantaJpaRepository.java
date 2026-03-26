package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantaJpaRepository extends JpaRepository<PlantaEntity, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}