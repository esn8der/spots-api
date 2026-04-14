package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.*;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("SpotJpaRepository - Pruebas de infraestructura")
class SpotJpaRepositoryTest {

    @Autowired
    private SpotJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    @Autowired
    private LoteJpaRepository loteJpaRepository;

    // Helper para crear datos base (sin estado compartido entre tests)
    private @NonNull TestData createBaseData() {
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD)
        );
        FincaEntity finca = fincaRepository.save(
                new FincaEntity(FincaFixtures.NOMBRE, usuario.getId())
        );
        PlantaEntity planta = plantaRepository.save(
                new PlantaEntity(PlantaFixtures.NOMBRE)
        );
        return new TestData(usuario.getId(), finca.getId(), planta.getId());
    }

    // Helper específico para crear un lote válido para tests de Spot
    private LoteEntity createLoteForSpotTest(@NonNull TestData data, Point point) {
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
    private record TestData(Long usuarioId, Long fincaId, Long plantaId) {
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

            repository.save(new SpotEntity(
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

            // Spot guardado con POSICION = 1
            repository.save(new SpotEntity(
                    point,
                    lote.getId(),
                    SpotFixtures.LINEA,
                    SpotFixtures.POSICION
            ));

            // WHEN: Buscamos con POSICION = 2
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

            repository.save(new SpotEntity(
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

            // Crear segundo lote con datos distintos para evitar conflictos
            UsuarioEntity usuario2 = usuarioRepository.save(
                    new UsuarioEntity(UsuarioFixtures.NOMBRE,
                            UsuarioFixtures.uniqueEmail(),
                            UsuarioFixtures.PASSWORD
                    )
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
            repository.save(new SpotEntity(
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

    @Nested
    @DisplayName("existsByLoteIdAndApproximateCoordinates() - Unicidad geo-espacial")
    class ExistsByLoteIdAndApproximateCoordinates {

        @Test
        @DisplayName("devuelve true cuando existe un spot en coordenadas que redondean igual (6 decimales)")
        void shouldReturnTrueWhenSpotExistsAtApproximateCoordinates() {
            // GIVEN: Guardamos un spot con coordenadas exactas
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint(); // [-73.647243, 3.896533]
            LoteEntity lote = createLoteForSpotTest(data, point);

            repository.save(new SpotEntity(point, lote.getId(), SpotFixtures.LINEA, SpotFixtures.POSICION));

            // WHEN: Consultamos con coordenadas que difieren en el 7.º decimal (~5 cm)
            // ROUND(-73.6472434, 6) = -73.647243 → MISMA CELDA
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    lote.getId(),
                    -73.6472434,  // Difiere en 7° decimal
                    3.8965334
            );

            // THEN: Debe encontrar el spot porque redondea a la misma celda de ~11 cm
            assertTrue(exists, "Debe encontrar spot en coordenadas que redondean igual a 6 decimales");
        }

        @Test
        @DisplayName("devuelve false cuando las coordenadas redondean a valores distintos")
        void shouldReturnFalseWhenCoordinatesRoundToDifferentValues() {
            // GIVEN: Guardamos un spot con coordenadas base
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint(); // [-73.647243, 3.896533]
            LoteEntity lote = createLoteForSpotTest(data, point);

            repository.save(new SpotEntity(point, lote.getId(), SpotFixtures.LINEA, SpotFixtures.POSICION));

            // WHEN: Consultamos con coordenadas que difieren en el 6.º decimal (~11 cm)
            // ROUND(-73.647244, 6) = -73.647244 → CELDA DIFERENTE
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    lote.getId(),
                    -73.647244,  // Difiere en 6° decimal
                    3.896533
            );

            // THEN: No debe encontrar el spot porque cae en celda distinta
            assertFalse(exists, "No debe encontrar spot en coordenadas que redondean distinto");
        }

        @Test
        @DisplayName("devuelve false cuando el lote es diferente aunque las coordenadas coincidan")
        void shouldReturnFalseWhenLoteIsDifferentEvenWithSameCoordinates() {
            // GIVEN: Dos lotes diferentes con spots en la misma coordenada aproximada
            TestData data1 = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote1 = createLoteForSpotTest(data1, point);

            repository.save(new SpotEntity(point, lote1.getId(), 1, 1));

            // Crear segundo lote con datos únicos
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

            // WHEN: Consultamos en lote2 con las mismas coordenadas del spot en lote1
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    lote2.getId(),  // Lote diferente
                    point.getX(),
                    point.getY()
            );

            // THEN: No debe encontrar porque la unicidad es por (lote_id, coordenadas)
            assertFalse(exists, "No debe encontrar spot en lote diferente aunque coordenadas coincidan");
        }

        @Test
        @DisplayName("devuelve false cuando no hay spots en el lote")
        void shouldReturnFalseWhenNoSpotsInLote() {
            // GIVEN: Lote vacío
            TestData data = createBaseData();
            Point point = SpotFixtures.validPoint();
            LoteEntity lote = createLoteForSpotTest(data, point);

            // WHEN: Consultamos coordenadas en lote sin spots
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    lote.getId(),
                    point.getX(),
                    point.getY()
            );

            // THEN
            assertFalse(exists, "Debe retornar false cuando el lote no tiene spots");
        }

        @Test
        @DisplayName("devuelve false cuando el lote no existe")
        void shouldReturnFalseWhenLoteDoesNotExist() {
            // WHEN: Consultamos con ID de lote inexistente
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    99999L,
                    -73.647243,
                    3.896533
            );

            // THEN
            assertFalse(exists, "Debe retornar false para lote inexistente");
        }

        @Test
        @DisplayName("maneja correctamente valores negativos de coordenadas")
        void shouldHandleNegativeCoordinatesCorrectly() {
            // GIVEN: Spot en coordenadas negativas (hemisferio sur/oeste)
            TestData data = createBaseData();
            Point point = new org.locationtech.jts.geom.GeometryFactory(
                    new org.locationtech.jts.geom.PrecisionModel(), 4326)
                    .createPoint(new org.locationtech.jts.geom.Coordinate(-74.1234567, -4.5678901));
            LoteEntity lote = createLoteForSpotTest(data, point);

            repository.save(new SpotEntity(point, lote.getId(), 1, 1));

            // WHEN: Consultamos con variación en 7° decimal
            boolean exists = repository.existsByLoteIdAndApproximateCoordinates(
                    lote.getId(),
                    -74.1234568,  // Difiere en 7° decimal
                    -4.5678902
            );

            // THEN: Debe encontrar porque redondea igual
            assertTrue(exists, "Debe manejar correctamente coordenadas negativas con redondeo");
        }
    }
}