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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @DisplayName("findAllByFincaId()")
    class FindAllByFincaId {

        @Test
        @DisplayName("devuelve lista vacía cuando no hay lotes para esa finca")
        void shouldReturnEmptyListWhenNoLotesForFinca() {
            // GIVEN
            TestData data = createBaseData();

            // WHEN
            List<LoteEntity> lotes = repository.findAllByFincaId(data.fincaId());

            // THEN
            assertTrue(lotes.isEmpty(), "Debe retornar lista vacía cuando no hay lotes");
        }

        @Test
        @DisplayName("devuelve solo los lotes de la finca solicitada")
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
            repository.save(new LoteEntity("Lote A", geocerca, data1.fincaId(), data1.plantaId()));
            repository.save(new LoteEntity("Lote B", geocerca, data1.fincaId(), data1.plantaId()));

            // Lote para finca2
            repository.save(new LoteEntity("Lote C", geocerca, data2.fincaId(), data2.plantaId()));

            // WHEN
            List<LoteEntity> lotes = repository.findAllByFincaId(data1.fincaId());

            // THEN
            assertEquals(2, lotes.size(), "Debe retornar exactamente 2 lotes para finca1");
            assertTrue(
                    lotes.stream().allMatch(l -> l.getFincaId().equals(data1.fincaId())),
                    "Todos los lotes deben pertenecer a la finca solicitada"
            );
            assertEquals(
                    Set.of("Lote A", "Lote B"),
                    lotes.stream().map(LoteEntity::getNombre).collect(Collectors.toSet()),
                    "Debe retornar los nombres correctos de los lotes"
            );
        }

        @Test
        @DisplayName("maneja correctamente cuando la finca no existe")
        void shouldHandleNonExistentFincaId() {
            // WHEN: Consultamos lotes de una finca que no existe
            List<LoteEntity> lotes = repository.findAllByFincaId(99999L);

            // THEN
            assertTrue(lotes.isEmpty(), "Debe retornar lista vacía para finca inexistente");
        }
    }
}