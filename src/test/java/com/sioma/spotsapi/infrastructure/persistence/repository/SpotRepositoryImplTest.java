package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.model.SpotPosition;
import com.sioma.spotsapi.fixtures.*;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.*;
import com.sioma.spotsapi.infrastructure.persistence.mapper.SpotEntityMapper;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("SpotRepositoryImpl - Pruebas de infraestructura")
class SpotRepositoryImplTest {

    @Autowired
    private SpotJpaRepository jpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    @Autowired
    private LoteJpaRepository loteJpaRepository;

    private final SpotEntityMapper mapper = new SpotEntityMapper();
    private SpotRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new SpotRepositoryImpl(jpaRepository, mapper);
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

    // Helper para crear un lote válido para tests de Spot
    private @NonNull LoteEntity createLoteForSpotTest(TestData data, Point point) {
        return loteJpaRepository.save(
                new LoteEntity(
                        "Lote para Spot",
                        LoteFixtures.loteContainingPoint(point).getGeocerca(),
                        data.fincaId(),
                        data.plantaId()
                )
        );
    }

    // Record inmutable para datos de test (evita variables de instancia compartidas)
    private record TestData(Long usuarioId, Long fincaId, Long plantaId) {}

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("crea un nuevo spot y devuelve el dominio con ID generado")
        void shouldSaveNewSpotAndReturnDomainWithGeneratedId() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);
            SpotPosition posicion = new SpotPosition(SpotFixtures.LINEA, SpotFixtures.POSICION);

            Spot spotDomain = new Spot(point, lote.getId(), posicion);

            // WHEN
            Spot resultado = repository.save(spotDomain);

            // THEN
            assertNotNull(resultado.getId(), "El spot guardado debe tener ID generado");
            assertEquals(point.getX(), resultado.getCoordenada().getX(), 0.0000001, "Debe conservar la longitud");
            assertEquals(point.getY(), resultado.getCoordenada().getY(), 0.0000001, "Debe conservar la latitud");
            assertEquals(4326, resultado.getCoordenada().getSRID(), "Debe conservar SRID 4326");
            assertEquals(lote.getId(), resultado.getLoteId());
            assertEquals(posicion.linea(), resultado.getLinea());
            assertEquals(posicion.linea(), resultado.getPosicion());

            // AND: Verifica persistencia real en DB
            Optional<SpotEntity> spotDB = jpaRepository.findById(resultado.getId());
            assertTrue(spotDB.isPresent());
            assertEquals(point.getX(), spotDB.get().getCoordenada().getX(), 0.0000001);
            assertEquals(4326, spotDB.get().getCoordenada().getSRID());
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("devuelve Optional con el spot cuando existe")
        void shouldReturnOptionalWithSpotWhenExists() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            SpotEntity entityGuardado = jpaRepository.save(
                    new SpotEntity(point, lote.getId(), SpotFixtures.LINEA, SpotFixtures.POSICION)
            );

            // WHEN
            Optional<Spot> resultado = repository.findById(entityGuardado.getId());

            // THEN
            assertTrue(resultado.isPresent());
            Spot spot = resultado.get();
            assertEquals(entityGuardado.getId(), spot.getId());
            assertEquals(point.getX(), spot.getCoordenada().getX(), 0.0000001);
            assertEquals(point.getY(), spot.getCoordenada().getY(), 0.0000001);
            assertEquals(4326, spot.getCoordenada().getSRID());
            assertEquals(lote.getId(), spot.getLoteId());
            assertEquals(SpotFixtures.LINEA, spot.getLinea());
            assertEquals(SpotFixtures.POSICION, spot.getPosicion());
        }

        @Test
        @DisplayName("devuelve Optional vacío cuando el spot no existe")
        void shouldReturnEmptyOptionalWhenSpotDoesNotExist() {
            // WHEN
            Optional<Spot> resultado = repository.findById(99999L);

            // THEN
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteById {

        @Test
        @DisplayName("elimina el spot de la base de datos")
        void shouldDeleteSpotFromDatabase() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            SpotEntity entityGuardado = jpaRepository.save(
                    new SpotEntity(point, lote.getId(), SpotFixtures.LINEA, SpotFixtures.POSICION)
            );

            // WHEN
            repository.deleteById(entityGuardado.getId());

            // THEN
            Optional<SpotEntity> resultado = jpaRepository.findById(entityGuardado.getId());
            assertTrue(resultado.isEmpty(), "El spot debe haber sido eliminado de la base de datos");
        }

        @Test
        @DisplayName("no lanza excepción al eliminar un ID que no existe")
        void shouldNotThrowExceptionWhenDeletingNonExistentId() {
            // WHEN & THEN: JPA deleteById es idempotente
            assertDoesNotThrow(() -> repository.deleteById(99999L));
        }
    }

    @Nested
    @DisplayName("existsByLoteIdAndLineaAndPosicion()")
    class ExistsByLoteIdAndLineaAndPosicion {

        @Test
        @DisplayName("devuelve true cuando el spot existe con lote, línea y posición exactos")
        void shouldReturnTrueWhenSpotExistsWithExactLoteLineaAndPosicion() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            jpaRepository.save(new SpotEntity(
                    point,
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            ));

            // WHEN
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            );

            // THEN
            assertTrue(exists, "Debe encontrar el spot con coordenadas exactas");
        }

        @Test
        @DisplayName("devuelve false cuando la posición es diferente")
        void shouldReturnFalseWhenPosicionIsDifferent() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            jpaRepository.save(new SpotEntity(
                    point,
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            ));

            // WHEN: Buscamos con POSICION diferente
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION + 1
            );

            // THEN
            assertFalse(exists, "No debe encontrar spot con posición diferente");
        }

        @Test
        @DisplayName("devuelve false cuando la línea es diferente")
        void shouldReturnFalseWhenLineaIsDifferent() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            jpaRepository.save(new SpotEntity(
                    point,
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            ));

            // WHEN: Buscamos con LÍNEA diferente
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    lote.getId(),
                    SpotFixtures.LINEA + 1,
                    SpotFixtures.POSICION
            );

            // THEN
            assertFalse(exists, "No debe encontrar spot con línea diferente");
        }

        @Test
        @DisplayName("devuelve false cuando el lote es diferente")
        void shouldReturnFalseWhenLoteIsDifferent() {
            // GIVEN: Dos lotes diferentes
            TestData data1 = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote1 = createLoteForSpotTest(data1, point);

            // Crear segundo lote con datos únicos para evitar conflictos
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
            LoteEntity lote2 = createLoteForSpotTest(data2, point);

            // Guardar spot en lote1
            jpaRepository.save(new SpotEntity(
                    point,
                    lote1.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            ));

            // WHEN: Buscamos en lote2 con misma línea y posición
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    lote2.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            );

            // THEN
            assertFalse(exists, "No debe encontrar spot en un lote diferente");
        }

        @Test
        @DisplayName("devuelve false cuando no hay spots para ese lote")
        void shouldReturnFalseWhenNoSpotsForLote() {
            // GIVEN
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            // WHEN: Buscamos en un lote vacío
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            );

            // THEN
            assertFalse(exists, "Debe retornar false cuando no hay spots");
        }

        @Test
        @DisplayName("devuelve false cuando el lote no existe")
        void shouldReturnFalseWhenLoteDoesNotExist() {
            // WHEN: Buscamos con ID de lote inexistente
            boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                    99999L,
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            );

            // THEN
            assertFalse(exists, "Debe retornar false para lote inexistente");
        }
    }
}