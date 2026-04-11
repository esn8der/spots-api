package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.LoteEntityMapper;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("LoteRepositoryImpl - Pruebas de infraestructura")
class LoteRepositoryImplTest {

    @Autowired
    private LoteJpaRepository jpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    private final LoteEntityMapper mapper = new LoteEntityMapper();
    private LoteRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new LoteRepositoryImpl(jpaRepository, mapper);
    }

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("crea un nuevo lote y devuelve el dominio con ID generado")
        void shouldSaveNewLoteAndReturnDomainWithGeneratedId() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            Lote loteDomain = new Lote(LoteFixtures.NOMBRE, geocerca, data.fincaId(), data.plantaId());

            // WHEN
            Lote resultado = repository.save(loteDomain);

            // THEN
            assertNotNull(resultado.getId(), "El lote guardado debe tener ID generado");
            assertEquals(LoteFixtures.NOMBRE, resultado.getNombre());
            assertEquals(data.fincaId(), resultado.getFincaId());
            assertEquals(data.plantaId(), resultado.getTipoCultivoId());
            assertEquals(4326, resultado.getGeocerca().getSRID(), "Debe conservar SRID 4326");

            // AND: Verifica persistencia real en DB
            Optional<LoteEntity> enDb = jpaRepository.findById(resultado.getId());
            assertTrue(enDb.isPresent());
            assertEquals(LoteFixtures.NOMBRE, enDb.get().getNombre());
        }

    }
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("devuelve Optional con el lote cuando existe")
        void shouldReturnOptionalWithLoteWhenExists() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();

            LoteEntity entityGuardado = jpaRepository.save(
                    new LoteEntity(LoteFixtures.NOMBRE, geocerca, data.fincaId(), data.plantaId())
            );

            // WHEN
            Optional<Lote> resultado = repository.findById(entityGuardado.getId());

            // THEN
            assertTrue(resultado.isPresent());
            Lote lote = resultado.get();
            assertEquals(entityGuardado.getId(), lote.getId());
            assertEquals(LoteFixtures.NOMBRE, lote.getNombre());
            assertEquals(data.fincaId(), lote.getFincaId());
            assertEquals(data.plantaId(), lote.getTipoCultivoId());
            assertEquals(4326, lote.getGeocerca().getSRID());
        }

        @Test
        @DisplayName("devuelve Optional vacío cuando el lote no existe")
        void shouldReturnEmptyOptionalWhenLoteDoesNotExist() {
            // WHEN
            Optional<Lote> resultado = repository.findById(99999L);

            // THEN
            assertTrue(resultado.isEmpty());
        }

    }

    @Nested
    @DisplayName("findAllByFincaId(Long, int, int) - Paginación")
    class FindAllByFincaIdWithPagination {

        @Test
        @DisplayName("devuelve PageResult con contenido correcto y metadatos")
        void shouldReturnPageResultWithCorrectContentAndMetadata() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            jpaRepository.save(new LoteEntity("Lote A", geocerca, data.fincaId(), data.plantaId()));
            jpaRepository.save(new LoteEntity("Lote B", geocerca, data.fincaId(), data.plantaId()));
            jpaRepository.save(new LoteEntity("Lote C", geocerca, data.fincaId(), data.plantaId()));

            // WHEN
            PageResult<Lote> resultado = repository.findAllByFincaId(data.fincaId(), 0, 2);

            // THEN
            assertEquals(2, resultado.content().size(), "Debe retornar 2 elementos por página");
            assertEquals(0, resultado.page(), "Debe ser la página 0");
            assertEquals(2, resultado.size(), "El tamaño de página debe ser 2");
            assertEquals(3, resultado.totalElements(), "Debe haber 3 elementos en total");
            assertEquals(2, resultado.totalPages(), "Debe haber 2 páginas en total");
            assertFalse(resultado.content().isEmpty(), "No debe estar vacío");

            // Verificar ordenamiento por nombre (asc por defecto en JPA)
            assertEquals("Lote A", resultado.content().get(0).getNombre(),
                    "Primer elemento debe ser 'Lote A'");
            assertEquals("Lote B", resultado.content().get(1).getNombre(),
                    "Segundo elemento debe ser 'Lote B'");
        }

        @Test
        @DisplayName("devuelve PageResult vacío cuando no hay lotes para la finca")
        void shouldReturnEmptyPageResultWhenNoLotesForFinca() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN
            PageResult<Lote> resultado = repository.findAllByFincaId(data.fincaId(), 0, 10);

            // THEN
            assertTrue(resultado.content().isEmpty(), "Debe retornar contenido vacío");
            assertEquals(0, resultado.totalElements(), "Total de elementos debe ser 0");
            assertEquals(0, resultado.totalPages(), "Total de páginas debe ser 0");
        }

        @Test
        @DisplayName("devuelve contenido vacío cuando el número de página está fuera de rango")
        void shouldReturnEmptyContentWhenPageNumberIsOutOfRange() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            jpaRepository.save(new LoteEntity("Lote Único", geocerca, data.fincaId(), data.plantaId()));

            // WHEN
            PageResult<Lote> resultado = repository.findAllByFincaId(data.fincaId(), 999, 10);

            // THEN
            assertTrue(resultado.content().isEmpty(), "Debe retornar contenido vacío para página fuera de rango");
            assertEquals(1, resultado.totalElements(), "Pero los metadatos totales deben ser correctos");
            assertEquals(1, resultado.totalPages(), "Total de páginas debe ser 1");
        }

        @Test
        @DisplayName("maneja finca inexistente retornando PageResult vacío")
        void shouldHandleNonExistentFincaReturningEmptyPageResult() {
            // WHEN
            PageResult<Lote> resultado = repository.findAllByFincaId(99999L, 0, 10);

            // THEN
            assertTrue(resultado.content().isEmpty(), "Debe retornar contenido vacío para finca inexistente");
            assertEquals(0, resultado.totalElements(), "Total de elementos debe ser 0");
        }

        @Test
        @DisplayName("maneja correctamente tamaño de página personalizado")
        void shouldHandleCustomPageSize() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();
            for (int i = 1; i <= 25; i++) {
                jpaRepository.save(new LoteEntity("Lote-" + String.format("%02d", i), geocerca, data.fincaId(), data.plantaId()));
            }

            // WHEN: Página 1 (base 0) con tamaño 10 → elementos 10-19
            PageResult<Lote> resultado = repository.findAllByFincaId(data.fincaId(), 1, 10);

            // THEN
            assertEquals(10, resultado.content().size(), "Debe retornar 10 elementos por página");
            assertEquals(1, resultado.page(), "Debe ser la página 1 (base 0)");
            assertEquals(25, resultado.totalElements(), "Total debe ser 25");
            assertEquals(3, resultado.totalPages(), "Debe haber 3 páginas en total");
            assertEquals("Lote-11", resultado.content().getFirst().getNombre(),
                    "Primer elemento de página 1 debe ser 'Lote-11'");
        }

        @Test
        @DisplayName("devuelve solo los lotes de la finca solicitada (aislamiento por fincaId)")
        void shouldReturnOnlyLotesFromRequestedFinca() {
            // GIVEN: Dos fincas con lotes diferentes
            TestData data1 = createBaseData();

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

            Polygon geocerca = LoteFixtures.anyGeocerca();

            // Lotes para finca1
            jpaRepository.save(new LoteEntity("Lote A", geocerca, data1.fincaId(), data1.plantaId()));
            jpaRepository.save(new LoteEntity("Lote B", geocerca, data1.fincaId(), data1.plantaId()));

            // Lote para finca2
            jpaRepository.save(new LoteEntity("Lote C", geocerca, data2.fincaId(), data2.plantaId()));

            // WHEN
            PageResult<Lote> resultado = repository.findAllByFincaId(data1.fincaId(), 0, 10);

            // THEN
            assertEquals(2, resultado.content().size(), "Debe retornar exactamente 2 lotes para finca1");
            assertTrue(
                    resultado.content().stream().allMatch(l -> l.getFincaId().equals(data1.fincaId())),
                    "Todos los lotes deben pertenecer a la finca solicitada"
            );
            assertEquals(
                    Set.of("Lote A", "Lote B"),
                    resultado.content().stream().map(Lote::getNombre).collect(Collectors.toSet()),
                    "Debe retornar los nombres correctos de los lotes"
            );
        }
    }

    @Nested
    @DisplayName("existsByNombreIgnoreCaseAndFincaId()")
    class ExistsByNombreIgnoreCaseAndFincaId {

        @Test
        @DisplayName("devuelve true cuando existe con mismo nombre ignorando mayúsculas")
        void shouldReturnTrueWhenLoteExistsIgnoringCase() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();

            jpaRepository.save(new LoteEntity("Mi Lote", geocerca, data.fincaId(), data.plantaId()));

            // WHEN
            boolean existe = repository.existsByNombreIgnoreCaseAndFincaId("MI LOTE", data.fincaId());

            // THEN
            assertTrue(existe);
        }

        @Test
        @DisplayName("devuelve false cuando el nombre no existe para esa finca")
        void shouldReturnFalseWhenLoteDoesNotExistForFinca() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN
            boolean existe = repository.existsByNombreIgnoreCaseAndFincaId("Lote Inexistente", data.fincaId());

            // THEN
            assertFalse(existe);
        }

        @Test
        @DisplayName("devuelve false cuando el nombre existe pero en otra finca")
        void shouldReturnFalseWhenLoteExistsInDifferentFinca() {
            // GIVEN: Dos fincas con lotes del mismo nombre
            TestData data1 = createBaseData();

            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.uniqueEmail(), UsuarioFixtures.PASSWORD)
            );
            FincaEntity finca2 = fincaRepository.save(
                    new FincaEntity("Finca 2", usuario2.getId())
            );
            PlantaEntity planta2 = plantaRepository.save(
                    new PlantaEntity(PlantaFixtures.uniqueName())
            );
            TestData data2 = new TestData(usuario2.getId(), finca2.getId(), planta2.getId());

            Polygon geocerca = LoteFixtures.anyGeocerca();
            jpaRepository.save(new LoteEntity("Lote Compartido", geocerca, data1.fincaId(), data1.plantaId()));

            // WHEN: Buscamos el mismo nombre pero en finca2
            boolean existe = repository.existsByNombreIgnoreCaseAndFincaId("Lote Compartido", data2.fincaId());

            // THEN
            assertFalse(existe);
        }

    }
    @Nested
    @DisplayName("deleteById()")
    class DeleteById {

        @Test
        @DisplayName("elimina el lote de la base de datos")
        void shouldDeleteLoteFromDatabase() {
            // GIVEN
            TestData data = createBaseData();
            Polygon geocerca = LoteFixtures.anyGeocerca();

            LoteEntity entityGuardado = jpaRepository.save(
                    new LoteEntity(LoteFixtures.NOMBRE, geocerca, data.fincaId(), data.plantaId())
            );

            // WHEN
            repository.deleteById(entityGuardado.getId());

            // THEN
            Optional<LoteEntity> resultado = jpaRepository.findById(entityGuardado.getId());
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("no lanza excepción al eliminar un ID que no existe")
        void shouldNotThrowExceptionWhenDeletingNonExistentId() {
            // WHEN & THEN: JPA deleteById es idempotente
            assertDoesNotThrow(() -> repository.deleteById(99999L));
        }

    }
    // Helper para crear datos base (sin estado compartido entre tests)
    private @NonNull TestData createBaseData() {
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(UsuarioFixtures.NOMBRE, UsuarioFixtures.EMAIL, UsuarioFixtures.PASSWORD)
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
    private record TestData(Long usuarioId, Long fincaId, Long plantaId) {}
}