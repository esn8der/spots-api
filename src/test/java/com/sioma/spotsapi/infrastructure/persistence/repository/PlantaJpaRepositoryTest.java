package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PlantaJpaRepositoryTest extends PostgresContainerConfig {

    @Autowired
    private PlantaJpaRepository repository;

    @Test
    void shouldReturnTrueWhenNameExistsIgnoreCase() {
        // GIVEN
        repository.save(new PlantaEntity(PlantaFixtures.NOMBRE));

        // WHEN
        boolean exists = repository.existsByNombreIgnoreCase(PlantaFixtures.NOMBRE.toUpperCase());

        // THEN
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenNameDoesNotExistsIgnoreCase(){
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCase("Banano");

        // THEN
        assertFalse(exists);
    }
}
