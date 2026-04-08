package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("PlantaJpaRepository - Pruebas de infraestructura")
class PlantaJpaRepositoryTest {

    @Autowired
    private PlantaJpaRepository repository;

    @Nested
    @DisplayName("existsByNombreIgnoreCase()")
    class ExistsByNombreIgnoreCase {

        @Test
        @DisplayName("devuelve true cuando el nombre existe ignorando mayúsculas/minúsculas")
        void shouldReturnTrueWhenNameExistsIgnoringCase() {
            // GIVEN
            String nombreOriginal = PlantaFixtures.NOMBRE;
            repository.save(new PlantaEntity(nombreOriginal));

            // WHEN: Buscamos el mismo nombre en mayúsculas
            boolean exists = repository.existsByNombreIgnoreCase(nombreOriginal.toUpperCase());

            // THEN
            assertTrue(exists, "Debe encontrar la planta aunque el caso sea diferente");
        }

        @Test
        @DisplayName("devuelve true cuando el nombre existe con caso mixto")
        void shouldReturnTrueWhenNameExistsWithMixedCase() {
            // GIVEN
            String nombreOriginal = PlantaFixtures.NOMBRE;
            repository.save(new PlantaEntity(nombreOriginal));

            // WHEN: Buscamos con caso mixto (primera mayúscula, resto minúscula)
            String nombreBuscado = nombreOriginal.substring(0, 1).toUpperCase() +
                    nombreOriginal.substring(1).toLowerCase();
            boolean exists = repository.existsByNombreIgnoreCase(nombreBuscado);

            // THEN
            assertTrue(exists, "Debe encontrar la planta con cualquier combinación de mayúsculas/minúsculas");
        }

        @Test
        @DisplayName("devuelve false cuando el nombre no existe")
        void shouldReturnFalseWhenNameDoesNotExist() {
            // WHEN: Buscamos un nombre que nunca fue guardado
            boolean exists = repository.existsByNombreIgnoreCase(PlantaFixtures.uniqueName());

            // THEN
            assertFalse(exists, "Debe retornar false para nombres inexistentes");
        }

        @Test
        @DisplayName("devuelve false para string vacío")
        void shouldReturnFalseForEmptyString() {
            // WHEN: Buscamos con string vacío
            boolean exists = repository.existsByNombreIgnoreCase("");

            // THEN
            assertFalse(exists, "Debe retornar false para string vacío");
        }

        @Test
        @DisplayName("maneja correctamente nombres con espacios y caracteres especiales")
        void shouldHandleNamesWithSpacesAndSpecialChars() {
            // GIVEN: Guardamos un nombre con espacios y caracteres especiales
            String nombreEspecial = "Café Arábica - Región Andina";
            repository.save(new PlantaEntity(nombreEspecial));

            // WHEN: Buscamos ignorando caso
            boolean exists = repository.existsByNombreIgnoreCase("CAFÉ ARÁBICA - REGIÓN ANDINA");

            // THEN
            assertTrue(exists, "Debe encontrar nombres con caracteres especiales ignorando el caso");
        }
    }
}