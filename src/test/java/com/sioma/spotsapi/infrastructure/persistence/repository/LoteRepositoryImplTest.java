package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
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
    @DisplayName("findAllByFincaId()")
    class FindAllByFincaId {

        @Test
        @DisplayName("devuelve lista vacía cuando no hay lotes para esa finca")
        void shouldReturnEmptyListWhenNoLotesForFinca() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN
            List<Lote> resultado = repository.findAllByFincaId(data.fincaId());

            // THEN
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("devuelve solo los lotes de la finca solicitada")
        void shouldReturnOnlyLotesFromRequestedFinca() {
            // GIVEN: Dos fincas con lotes diferentes
            TestData data1 = createBaseData();

            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE,
                            UsuarioFixtures.uniqueEmail(),
                            UsuarioFixtures.PASSWORD)
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
            List<Lote> resultado = repository.findAllByFincaId(data1.fincaId());

            // THEN
            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().allMatch(l -> l.getFincaId().equals(data1.fincaId())));
            assertEquals(
                    Set.of("Lote A", "Lote B"),
                    resultado.stream().map(Lote::getNombre).collect(Collectors.toSet())
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