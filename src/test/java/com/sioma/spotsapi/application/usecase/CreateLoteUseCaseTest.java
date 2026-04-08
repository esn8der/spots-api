package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.ports.out.GeospatialConverter;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLoteUseCase - Pruebas de aplicación")
class CreateLoteUseCaseTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FincaRepository fincaRepository;

    @Mock
    private PlantaRepository plantaRepository;

    @Mock
    private GeospatialConverter geospatialConverter;

    @InjectMocks
    private CreateLoteUseCase useCase;

    // Constante para coordenadas de prueba (reutilizable en todos los tests)
    private static final List<List<Double>> COORDINATES = List.of(
            List.of(-73.0, 4.0),
            List.of(-73.0, 4.1),
            List.of(-73.1, 4.1),
            List.of(-73.1, 4.0),
            List.of(-73.0, 4.0)  // cerrado
    );

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza FincaNotFoundException cuando la finca no existe")
        void shouldThrowFincaNotFoundExceptionWhenFincaDoesNotExist() {
            // GIVEN: La finca NO existe
            givenGeospatialConverterReturnsPolygon();
            givenFincaExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    FincaNotFoundException.class,
                    () -> useCase.execute(
                            LoteFixtures.NOMBRE,
                            COORDINATES,
                            LoteFixtures.FINCA_ID,
                            LoteFixtures.TIPO_CULTIVO_ID
                    ),
                    "Debe lanzar excepción cuando la finca no existe"
            );

            thenLoteIsNotSaved();
        }

        @Test
        @DisplayName("lanza PlantaNotFoundException cuando el tipo de cultivo no existe")
        void shouldThrowPlantaNotFoundExceptionWhenPlantaDoesNotExist() {
            // GIVEN: La finca existe, pero el tipo de cultivo NO
            givenGeospatialConverterReturnsPolygon();
            givenFincaExists(true);
            givenPlantaExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    PlantaNotFoundException.class,
                    () -> useCase.execute(
                            LoteFixtures.NOMBRE,
                            COORDINATES,
                            LoteFixtures.FINCA_ID,
                            LoteFixtures.TIPO_CULTIVO_ID
                    ),
                    "Debe lanzar excepción cuando el tipo de cultivo no existe"
            );

            thenLoteIsNotSaved();
        }

        @Test
        @DisplayName("lanza LoteAlreadyExistsException cuando ya existe un lote con ese nombre para la finca")
        void shouldThrowLoteAlreadyExistsExceptionWhenLoteAlreadyExistsForFinca() {
            // GIVEN: Finca y planta existen, pero ya hay un lote con ese nombre
            givenGeospatialConverterReturnsPolygon();
            givenFincaExists(true);
            givenPlantaExists(true);
            givenLoteExists(true);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    LoteAlreadyExistsException.class,
                    () -> useCase.execute(
                            LoteFixtures.NOMBRE,
                            COORDINATES,
                            LoteFixtures.FINCA_ID,
                            LoteFixtures.TIPO_CULTIVO_ID
                    ),
                    "Debe lanzar excepción cuando el lote ya existe para esa finca"
            );

            thenLoteIsNotSaved();
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class HappyPath {

        @Test
        @DisplayName("convierte las coordenadas a Polygon usando GeospatialConverter")
        void shouldConvertCoordinatesToPolygonUsingGeospatialConverter() {
            // GIVEN: Todas las precondiciones se cumplen
            givenGeospatialConverterReturnsPolygon();
            givenFincaExists(true);
            givenPlantaExists(true);
            givenLoteExists(false);

            // WHEN
            useCase.execute(
                    LoteFixtures.NOMBRE,
                    COORDINATES,
                    LoteFixtures.FINCA_ID,
                    LoteFixtures.TIPO_CULTIVO_ID
            );

            // THEN: Verifica que el converter fue llamado con las coordenadas correctas
            verify(geospatialConverter).toPolygon(COORDINATES);
        }

        @Test
        @DisplayName("crea el lote con los datos correctos y la geocerca convertida")
        void shouldCreateLoteSuccessfullyWithConvertedGeocerca() {
            // GIVEN: Todas las precondiciones se cumplen
            givenGeospatialConverterReturnsPolygon();
            givenFincaExists(true);
            givenPlantaExists(true);
            givenLoteExists(false);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(
                    LoteFixtures.NOMBRE,
                    COORDINATES,
                    LoteFixtures.FINCA_ID,
                    LoteFixtures.TIPO_CULTIVO_ID
            );

            // THEN: Verifica que se llamó a save con los datos correctos
            ArgumentCaptor<Lote> loteCaptor = ArgumentCaptor.forClass(Lote.class);
            verify(loteRepository).save(loteCaptor.capture());

            Lote saved = loteCaptor.getValue();
            assertEquals(LoteFixtures.NOMBRE, saved.getNombre(), "El nombre del lote debe ser el esperado");
            assertEquals(LoteFixtures.FINCA_ID, saved.getFincaId(), "El fincaId del lote debe ser el esperado");
            assertEquals(LoteFixtures.TIPO_CULTIVO_ID, saved.getTipoCultivoId(), "El tipoCultivoId del lote debe ser el esperado");
            assertNotNull(saved.getGeocerca(), "La geocerca del lote no debe ser null");
            assertEquals(4326, saved.getGeocerca().getSRID(), "La geocerca debe tener SRID 4326 (WGS84)");
        }
    }

    // ===== Helpers BDD =====

    private void givenGeospatialConverterReturnsPolygon() {
        when(geospatialConverter.toPolygon(COORDINATES))
                .thenReturn(LoteFixtures.anyGeocerca());
    }

    private void givenFincaExists(boolean exists) {
        Optional<Finca> finca = exists ? Optional.of(mock(Finca.class)) : Optional.empty();
        when(fincaRepository.findById(LoteFixtures.FINCA_ID)).thenReturn(finca);
    }

    private void givenPlantaExists(boolean exists) {
        Optional<Planta> planta = exists ? Optional.of(mock(Planta.class)) : Optional.empty();
        when(plantaRepository.findById(LoteFixtures.TIPO_CULTIVO_ID)).thenReturn(planta);
    }

    private void givenLoteExists(boolean exists) {
        when(loteRepository.existsByNombreIgnoreCaseAndFincaId(
                LoteFixtures.NOMBRE, LoteFixtures.FINCA_ID))
                .thenReturn(exists);
    }

    private void thenLoteIsNotSaved() {
        verify(loteRepository, never()).save(any(Lote.class));
    }
}