package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
class PlantaJpaRepositoryTest {

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
    void shouldReturnFalseWhenNameDoesNotExistsIgnoreCase() {
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCase("Mala2");

        // THEN
        assertFalse(exists);
    }
}
