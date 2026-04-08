package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.*;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("FincaJpaRepository - Pruebas de infraestructura")
class FincaJpaRepositoryTest {

    @Autowired
    private FincaJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    // Helper para crear datos base (sin estado compartido entre tests)
    private @NonNull TestData createBaseData() {
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.EMAIL, UsuarioFixtures.PASSWORD)
        );
        return new TestData(usuario.getId());
    }

    // Record inmutable para datos de test (evita variables de instancia compartidas)
    private record TestData(Long usuarioId) {}

    @Nested
    @DisplayName("existsByNombreIgnoreCaseAndUsuarioId()")
    class ExistsByNombreIgnoreCaseAndUsuarioId {

        @Test
        @DisplayName("devuelve true cuando la finca existe ignorando mayúsculas/minúsculas")
        void shouldReturnTrueWhenFincaExistsIgnoringCase() {
            // GIVEN
            TestData data = createBaseData();
            String nombreOriginal = FincaFixtures.NOMBRE;

            FincaEntity fincaGuardada = repository.save(
                    new FincaEntity(nombreOriginal, data.usuarioId())
            );

            // WHEN: Buscamos con nombre en mayúsculas
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                    fincaGuardada.getNombre().toUpperCase(),
                    fincaGuardada.getUsuarioId()
            );

            // THEN
            assertTrue(exists, "Debe encontrar la finca aunque el caso del nombre sea diferente");
        }

        @Test
        @DisplayName("devuelve true cuando el nombre existe con caso mixto")
        void shouldReturnTrueWhenNameExistsWithMixedCase() {
            // GIVEN
            TestData data = createBaseData();
            String nombreOriginal = "Finca Test";

            repository.save(new FincaEntity(nombreOriginal, data.usuarioId()));

            // WHEN: Buscamos con caso mixto
            String nombreBuscado = nombreOriginal.substring(0, 1).toLowerCase() +
                    nombreOriginal.substring(1).toUpperCase();
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(nombreBuscado, data.usuarioId());

            // THEN
            assertTrue(exists, "Debe encontrar la finca con cualquier combinación de mayúsculas/minúsculas");
        }

        @Test
        @DisplayName("devuelve false cuando el nombre no existe para ese usuario")
        void shouldReturnFalseWhenFincaDoesNotExistForUsuario() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN: Buscamos un nombre que nunca fue guardado
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                    FincaFixtures.uniqueName(),
                    data.usuarioId()
            );

            // THEN
            assertFalse(exists, "Debe retornar false para nombres inexistentes");
        }

        @Test
        @DisplayName("devuelve false cuando el nombre existe pero para otro usuario")
        void shouldReturnFalseWhenFincaExistsForDifferentUsuario() {
            // GIVEN: Dos usuarios con fincas del mismo nombre
            TestData data1 = createBaseData();
            String nombreCompartido = "Finca Compartida";

            repository.save(new FincaEntity(nombreCompartido, data1.usuarioId()));

            // Crear segundo usuario con datos únicos
            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.uniqueEmail(), UsuarioFixtures.PASSWORD)
            );
            TestData data2 = new TestData(usuario2.getId());

            // WHEN: Buscamos el mismo nombre pero para usuario2
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(nombreCompartido, data2.usuarioId());

            // THEN
            assertFalse(exists, "No debe encontrar la finca en un usuario diferente");
        }

        @Test
        @DisplayName("devuelve false para string vacío")
        void shouldReturnFalseForEmptyString() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN: Buscamos con string vacío
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId("", data.usuarioId());

            // THEN
            assertFalse(exists, "Debe retornar false para string vacío");
        }

        @Test
        @DisplayName("maneja nombres con espacios y caracteres especiales")
        void shouldHandleNamesWithSpacesAndSpecialChars() {
            // GIVEN: Guardamos un nombre con espacios y caracteres especiales
            TestData data = createBaseData();
            String nombreEspecial = "Finca Andina - Región 2024";

            repository.save(new FincaEntity(nombreEspecial, data.usuarioId()));

            // WHEN: Buscamos ignorando caso
            boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                    "FINCA ANDINA - REGIÓN 2024",
                    data.usuarioId()
            );

            // THEN
            assertTrue(exists, "Debe encontrar nombres con caracteres especiales ignorando el caso");
        }
    }

    @Nested
    @DisplayName("findAllByUsuarioId()")
    class FindAllByUsuarioId {

        @Test
        @DisplayName("devuelve lista vacía cuando no hay fincas para ese usuario")
        void shouldReturnEmptyListWhenNoFincasForUsuario() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN
            List<FincaEntity> fincas = repository.findAllByUsuarioId(data.usuarioId());

            // THEN
            assertTrue(fincas.isEmpty(), "Debe retornar lista vacía cuando no hay fincas");
        }

        @Test
        @DisplayName("devuelve solo las fincas del usuario solicitado")
        void shouldReturnOnlyFincasFromRequestedUsuario() {
            // GIVEN: Dos usuarios con fincas diferentes
            TestData data1 = createBaseData();

            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.uniqueEmail(), UsuarioFixtures.PASSWORD)
            );
            TestData data2 = new TestData(usuario2.getId());

            // Fincas para usuario1
            repository.save(new FincaEntity("Finca A", data1.usuarioId()));
            repository.save(new FincaEntity("Finca B", data1.usuarioId()));

            // Finca para usuario2
            repository.save(new FincaEntity("Finca C", data2.usuarioId()));

            // WHEN
            List<FincaEntity> fincas = repository.findAllByUsuarioId(data1.usuarioId());

            // THEN
            assertEquals(2, fincas.size(), "Debe retornar exactamente 2 fincas para usuario1");
            assertTrue(
                    fincas.stream().allMatch(f -> f.getUsuarioId().equals(data1.usuarioId())),
                    "Todas las fincas deben pertenecer al usuario solicitado"
            );
            assertEquals(
                    Set.of("Finca A", "Finca B"),
                    fincas.stream().map(FincaEntity::getNombre).collect(Collectors.toSet()),
                    "Debe retornar los nombres correctos de las fincas"
            );
        }

        @Test
        @DisplayName("maneja correctamente cuando el usuario no existe")
        void shouldHandleNonExistentUsuarioId() {
            // WHEN: Consultamos fincas de un usuario que no existe
            List<FincaEntity> fincas = repository.findAllByUsuarioId(99999L);

            // THEN
            assertTrue(fincas.isEmpty(), "Debe retornar lista vacía para usuario inexistente");
        }
    }
}