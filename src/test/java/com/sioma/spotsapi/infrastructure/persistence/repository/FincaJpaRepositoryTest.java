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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    private record TestData(Long usuarioId) {
    }

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
    @DisplayName("findAllByUsuarioId(Long, Pageable) - Paginación")
    class FindAllByUsuarioIdWithPagination {

        @Test
        @DisplayName("devuelve página con contenido correcto y metadatos")
        void shouldReturnPageWithCorrectContentAndMetadata() {
            // GIVEN
            TestData data = createBaseData();
            repository.save(new FincaEntity("Finca A", data.usuarioId()));
            repository.save(new FincaEntity("Finca B", data.usuarioId()));
            repository.save(new FincaEntity("Finca C", data.usuarioId()));

            Pageable pageable = PageRequest.of(0, 2, Sort.by("nombre").ascending());

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertEquals(2, result.getContent().size(), "Debe retornar 2 elementos por página");
            assertEquals(0, result.getNumber(), "Debe ser la página 0");
            assertEquals(2, result.getSize(), "El tamaño de página debe ser 2");
            assertEquals(3, result.getTotalElements(), "Debe haber 3 elementos en total");
            assertEquals(2, result.getTotalPages(), "Debe haber 2 páginas en total");
            assertTrue(result.hasContent(), "Debe tener contenido");
            assertFalse(result.isEmpty(), "No debe estar vacío");

            // Verificar ordenamiento por nombre (asc)
            assertEquals("Finca A", result.getContent().get(0).getNombre(),
                    "Primer elemento debe ser 'Finca A'");
            assertEquals("Finca B", result.getContent().get(1).getNombre(),
                    "Segundo elemento debe ser 'Finca B'");
        }

        @Test
        @DisplayName("devuelve página vacía cuando no hay resultados")
        void shouldReturnEmptyPageWhenNoResults() {
            // GIVEN
            TestData data = createBaseData();
            Pageable pageable = PageRequest.of(0, 10);

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe estar vacío cuando no hay fincas");
            assertEquals(0, result.getTotalElements(), "Total de elementos debe ser 0");
            assertEquals(0, result.getTotalPages(), "Total de páginas debe ser 0");
        }

        @Test
        @DisplayName("devuelve página vacía cuando el número de página está fuera de rango")
        void shouldReturnEmptyPageWhenPageNumberIsOutOfRange() {
            // GIVEN
            TestData data = createBaseData();
            repository.save(new FincaEntity("Finca Única", data.usuarioId()));
            Pageable pageable = PageRequest.of(999, 10); // Página inexistente

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe retornar contenido vacío para página fuera de rango");
            assertEquals(1, result.getTotalElements(), "Pero los metadatos totales deben ser correctos");
        }

        @Test
        @DisplayName("aplica ordenamiento ascendente por nombre por defecto")
        void shouldApplyAscendingSortByNameByDefault() {
            // GIVEN
            TestData data = createBaseData();
            repository.save(new FincaEntity("Zebra", data.usuarioId()));
            repository.save(new FincaEntity("Alpha", data.usuarioId()));
            repository.save(new FincaEntity("Mango", data.usuarioId()));

            Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertEquals(3, result.getContent().size(), "Debe retornar los 3 elementos");
            assertEquals("Alpha", result.getContent().get(0).getNombre(),
                    "Primer elemento debe ser 'Alpha'");
            assertEquals("Mango", result.getContent().get(1).getNombre(),
                    "Segundo elemento debe ser 'Mango'");
            assertEquals("Zebra", result.getContent().get(2).getNombre(),
                    "Tercer elemento debe ser 'Zebra'");
        }

        @Test
        @DisplayName("aplica ordenamiento descendente cuando se especifica")
        void shouldApplyDescendingSortWhenSpecified() {
            // GIVEN
            TestData data = createBaseData();
            repository.save(new FincaEntity("Alpha", data.usuarioId()));
            repository.save(new FincaEntity("Mango", data.usuarioId()));
            repository.save(new FincaEntity("Zebra", data.usuarioId()));

            Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").descending());

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertEquals("Zebra", result.getContent().get(0).getNombre(),
                    "Primer elemento debe ser 'Zebra' en orden descendente");
            assertEquals("Alpha", result.getContent().get(2).getNombre(),
                    "Último elemento debe ser 'Alpha'");
        }

        @Test
        @DisplayName("maneja correctamente tamaño de página personalizado")
        void shouldHandleCustomPageSize() {
            // GIVEN
            TestData data = createBaseData();
            for (int i = 1; i <= 25; i++) {
                repository.save(new FincaEntity("Finca-" + String.format("%02d", i), data.usuarioId()));
            }

            Pageable pageable = PageRequest.of(1, 10, Sort.by("nombre").ascending()); // Página 1 = elementos 10-19

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(data.usuarioId(), pageable);

            // THEN
            assertEquals(10, result.getContent().size(), "Debe retornar 10 elementos por página");
            assertEquals(1, result.getNumber(), "Debe ser la página 1 (base 0)");
            assertEquals(25, result.getTotalElements(), "Total debe ser 25");
            assertEquals(3, result.getTotalPages(), "Debe haber 3 páginas en total");
            assertEquals("Finca-11", result.getContent().getFirst().getNombre(),
                    "Primer elemento de página 1 debe ser 'Finca-11'");
        }

        @Test
        @DisplayName("maneja usuario inexistente retornando página vacía")
        void shouldHandleNonExistentUsuarioReturningEmptyPage() {
            // GIVEN
            Pageable pageable = PageRequest.of(0, 10);

            // WHEN
            Page<FincaEntity> result = repository.findAllByUsuarioId(99999L, pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe retornar página vacía para usuario inexistente");
            assertEquals(0, result.getTotalElements(), "Total de elementos debe ser 0");
        }
    }
}