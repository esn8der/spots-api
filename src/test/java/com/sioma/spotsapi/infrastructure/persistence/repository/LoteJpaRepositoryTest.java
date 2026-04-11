package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.*;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
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
@DisplayName("LoteJpaRepository - Pruebas de infraestructura")
class LoteJpaRepositoryTest {

    @Autowired
    private LoteJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    // Helper para crear datos base (sin estado compartido entre tests)
    private @NonNull TestData createBaseData() {
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );
        FincaEntity finca = fincaRepository.save(
                new FincaEntity(FincaFixtures.NOMBRE, usuario.getId())
        );
        PlantaEntity planta = plantaRepository.save(
                new PlantaEntity(PlantaFixtures.NOMBRE)
        );
        return new TestData(usuario.getId(), finca.getId(), planta.getId());
    }

    // Record inmutable para datos de test (evita variables de instancia compartidas)
    private record TestData(Long usuarioId, Long fincaId, Long plantaId) {
    }

    @Nested
    @DisplayName("existsByNombreIgnoreCaseAndFincaId()")
    class ExistsByNombreIgnoreCaseAndFincaId {

        @Test
        @DisplayName("devuelve true cuando el lote existe ignorando mayúsculas/minúsculas")
        void shouldReturnTrueWhenLoteExistsIgnoringCase() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();

            LoteEntity loteGuardado = repository.save(
                    new LoteEntity(LoteFixtures.NOMBRE, geocerca, data.fincaId(), data.plantaId())
            );

            // WHEN: Buscamos con nombre en mayúsculas
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                    loteGuardado.getNombre().toUpperCase(),
                    loteGuardado.getFincaId()
            );

            // THEN
            assertTrue(exists, "Debe encontrar el lote aunque el caso del nombre sea diferente");
        }

        @Test
        @DisplayName("devuelve true cuando el nombre existe con caso mixto")
        void shouldReturnTrueWhenNameExistsWithMixedCase() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            String nombreOriginal = "Lote Test";

            repository.save(new LoteEntity(nombreOriginal, geocerca, data.fincaId(), data.plantaId()));

            // WHEN: Buscamos con caso mixto
            String nombreBuscado = nombreOriginal.substring(0, 1).toLowerCase() +
                    nombreOriginal.substring(1).toUpperCase();
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(nombreBuscado, data.fincaId());

            // THEN
            assertTrue(exists, "Debe encontrar el lote con cualquier combinación de mayúsculas/minúsculas");
        }

        @Test
        @DisplayName("devuelve false cuando el nombre no existe para esa finca")
        void shouldReturnFalseWhenLoteDoesNotExistForFinca() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN: Buscamos un nombre que nunca fue guardado
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                    LoteFixtures.uniqueName(),
                    data.fincaId()
            );

            // THEN
            assertFalse(exists, "Debe retornar false para nombres inexistentes");
        }

        @Test
        @DisplayName("devuelve false cuando el nombre existe pero en otra finca")
        void shouldReturnFalseWhenLoteExistsInDifferentFinca() {
            // GIVEN: Dos fincas con lotes del mismo nombre
            TestData data1 = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();

            repository.save(new LoteEntity("Lote Compartido", geocerca, data1.fincaId(), data1.plantaId()));

            // Crear segunda finca con datos únicos para evitar conflictos
            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.uniqueEmail(), UsuarioFixtures.PASSWORD)
            );
            FincaEntity finca2 = fincaRepository.save(
                    new FincaEntity(FincaFixtures.uniqueName(), usuario2.getId())
            );
            PlantaEntity planta2 = plantaRepository.save(
                    new PlantaEntity(PlantaFixtures.uniqueName())
            );
            TestData data2 = new TestData(usuario2.getId(), finca2.getId(), planta2.getId());

            // WHEN: Buscamos el mismo nombre pero en finca2
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId("Lote Compartido", data2.fincaId());

            // THEN
            assertFalse(exists, "No debe encontrar el lote en una finca diferente");
        }

        @Test
        @DisplayName("devuelve false para string vacío")
        void shouldReturnFalseForEmptyString() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN: Buscamos con string vacío
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId("", data.fincaId());

            // THEN
            assertFalse(exists, "Debe retornar false para string vacío");
        }

        @Test
        @DisplayName("maneja nombres con espacios y caracteres especiales")
        void shouldHandleNamesWithSpacesAndSpecialChars() {
            // GIVEN: Guardamos un nombre con espacios y caracteres especiales
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            String nombreEspecial = "Lote Andino - Región 2024";

            repository.save(new LoteEntity(nombreEspecial, geocerca, data.fincaId(), data.plantaId()));

            // WHEN: Buscamos ignorando caso
            boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                    "LOTE ANDINO - REGIÓN 2024",
                    data.fincaId()
            );

            // THEN
            assertTrue(exists, "Debe encontrar nombres con caracteres especiales ignorando el caso");
        }
    }

    @Nested
    @DisplayName("findAllByFincaId(Long, Pageable) - Paginación")
    class FindAllByFincaIdWithPagination {

        @Test
        @DisplayName("devuelve página con contenido correcto y metadatos")
        void shouldReturnPageWithCorrectContentAndMetadata() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            repository.save(new LoteEntity("Lote A", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Lote B", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Lote C", geocerca, data.fincaId(), data.plantaId()));

            Pageable pageable = PageRequest.of(0, 2, Sort.by("nombre").ascending());

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

            // THEN
            assertEquals(2, result.getContent().size(), "Debe retornar 2 elementos por página");
            assertEquals(0, result.getNumber(), "Debe ser la página 0");
            assertEquals(2, result.getSize(), "El tamaño de página debe ser 2");
            assertEquals(3, result.getTotalElements(), "Debe haber 3 elementos en total");
            assertEquals(2, result.getTotalPages(), "Debe haber 2 páginas en total");
            assertTrue(result.hasContent(), "Debe tener contenido");
            assertFalse(result.isEmpty(), "No debe estar vacío");

            // Verificar ordenamiento por nombre (asc)
            assertEquals("Lote A", result.getContent().get(0).getNombre(),
                    "Primer elemento debe ser 'Lote A'");
            assertEquals("Lote B", result.getContent().get(1).getNombre(),
                    "Segundo elemento debe ser 'Lote B'");
        }

        @Test
        @DisplayName("devuelve página vacía cuando no hay resultados")
        void shouldReturnEmptyPageWhenNoResults() {
            // GIVEN
            TestData data = createBaseData();
            Pageable pageable = PageRequest.of(0, 10);

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe estar vacío cuando no hay lotes");
            assertEquals(0, result.getTotalElements(), "Total de elementos debe ser 0");
            assertEquals(0, result.getTotalPages(), "Total de páginas debe ser 0");
        }

        @Test
        @DisplayName("devuelve página vacía cuando el número de página está fuera de rango")
        void shouldReturnEmptyPageWhenPageNumberIsOutOfRange() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            repository.save(new LoteEntity("Lote Único", geocerca, data.fincaId(), data.plantaId()));
            Pageable pageable = PageRequest.of(999, 10); // Página inexistente

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe retornar contenido vacío para página fuera de rango");
            assertEquals(1, result.getTotalElements(), "Pero los metadatos totales deben ser correctos");
        }

        @Test
        @DisplayName("aplica ordenamiento ascendente por nombre por defecto")
        void shouldApplyAscendingSortByNameByDefault() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            repository.save(new LoteEntity("Zebra", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Alpha", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Mango", geocerca, data.fincaId(), data.plantaId()));

            Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

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
            Polygon geocerca = LoteFixtures.anyGeocerca();
            repository.save(new LoteEntity("Alpha", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Mango", geocerca, data.fincaId(), data.plantaId()));
            repository.save(new LoteEntity("Zebra", geocerca, data.fincaId(), data.plantaId()));

            Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").descending());

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

            // THEN
            assertEquals("Zebra", result.getContent().getFirst().getNombre(),
                    "Primer elemento debe ser 'Zebra' en orden descendente");
            assertEquals("Alpha", result.getContent().get(2).getNombre(),
                    "Último elemento debe ser 'Alpha'");
        }

        @Test
        @DisplayName("maneja correctamente tamaño de página personalizado")
        void shouldHandleCustomPageSize() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            for (int i = 1; i <= 25; i++) {
                repository.save(new LoteEntity("Lote-" + String.format("%02d", i), geocerca, data.fincaId(), data.plantaId()));
            }

            Pageable pageable = PageRequest.of(1, 10, Sort.by("nombre").ascending()); // Página 1 = elementos 10-19

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(data.fincaId(), pageable);

            // THEN
            assertEquals(10, result.getContent().size(), "Debe retornar 10 elementos por página");
            assertEquals(1, result.getNumber(), "Debe ser la página 1 (base 0)");
            assertEquals(25, result.getTotalElements(), "Total debe ser 25");
            assertEquals(3, result.getTotalPages(), "Debe haber 3 páginas en total");
            assertEquals("Lote-11", result.getContent().getFirst().getNombre(),
                    "Primer elemento de página 1 debe ser 'Lote-11'");
        }

        @Test
        @DisplayName("maneja finca inexistente retornando página vacía")
        void shouldHandleNonExistentFincaReturningEmptyPage() {
            // GIVEN
            Pageable pageable = PageRequest.of(0, 10);

            // WHEN
            Page<LoteEntity> result = repository.findAllByFincaId(99999L, pageable);

            // THEN
            assertTrue(result.isEmpty(), "Debe retornar página vacía para finca inexistente");
            assertEquals(0, result.getTotalElements(), "Total de elementos debe ser 0");
        }
    }
}