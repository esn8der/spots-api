package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.ports.out.GeospatialConverter;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.SpotFixtures;
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
@DisplayName("CreateSpotUseCase - Pruebas de aplicación")
class CreateSpotUseCaseTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private GeospatialConverter geospatialConverter;

    @InjectMocks
    private CreateSpotUseCase useCase;

    private static final List<Double> VALID_COORDINATES = SpotFixtures.validCoordinates();

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza LoteNotFoundException cuando el lote no existe")
        void shouldThrowLoteNotFoundExceptionWhenLoteDoesNotExist() {
            // GIVEN: El lote NO existe
            givenConverterReturnsValidPoint();
            givenLoteExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    LoteNotFoundException.class,
                    () -> useCase.execute(VALID_COORDINATES, SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION),
                    "Debe lanzar excepción cuando el lote no existe"
            );

            verify(spotRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza SpotAlreadyExistsException cuando ya existe un spot con misma línea y posición en el lote")
        void shouldThrowSpotAlreadyExistsExceptionWhenSpotAlreadyExists() {
            // GIVEN: El lote existe, pero ya hay un spot con esa línea/posición
            givenConverterReturnsValidPoint();
            givenLoteExists(true);
            givenSpotExists(true);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    SpotAlreadyExistsException.class,
                    () -> useCase.execute(VALID_COORDINATES, SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION),
                    "Debe lanzar excepción cuando el spot ya existe"
            );

            verify(spotRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza PointOutsideLoteException cuando la coordenada está fuera de la geocerca del lote")
        void shouldThrowPointOutsideLoteExceptionWhenPointIsOutsideLote() {
            // GIVEN: El lote existe, pero la coordenada está fuera de su geocerca
            givenConverterReturnsValidPoint();
            givenLoteWithPointOutside();
            givenSpotExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    PointOutsideLoteException.class,
                    () -> useCase.execute(VALID_COORDINATES, SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION),
                    "Debe lanzar excepción cuando el punto está fuera del lote"
            );

            verify(spotRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class HappyPath {

        @Test
        @DisplayName("convierte las coordenadas a Point usando GeospatialConverter")
        void shouldConvertCoordinatesToPointUsingGeospatialConverter() {
            // GIVEN: Todas las precondiciones se cumplen
            givenConverterReturnsValidPoint();
            givenLoteExists(true);
            givenSpotExists(false);

            // WHEN
            useCase.execute(VALID_COORDINATES, SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION);

            // THEN: Verifica que el converter fue llamado con las coordenadas correctas
            verify(geospatialConverter).toPoint(VALID_COORDINATES);
        }

        @Test
        @DisplayName("crea el spot con los datos correctos")
        void shouldCreateSpotSuccessfullyWithCorrectData() {
            // GIVEN: Todas las precondiciones se cumplen
            givenConverterReturnsValidPoint();
            givenLoteExists(true);
            givenSpotExists(false);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(VALID_COORDINATES, SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION);

            // THEN: Verifica que se llamó a save con los datos correctos
            ArgumentCaptor<Spot> spotCaptor = ArgumentCaptor.forClass(Spot.class);
            verify(spotRepository).save(spotCaptor.capture());

            Spot saved = spotCaptor.getValue();
            assertEquals(SpotFixtures.LOTE_ID, saved.getLoteId(), "El loteId del spot debe ser el esperado");
            assertEquals(SpotFixtures.LINEA, saved.getLinea(), "La línea del spot debe ser la esperada");
            assertEquals(SpotFixtures.POSICION, saved.getPosicion(), "La posición del spot debe ser la esperada");
        }
    }

    // ===== Helpers BDD (mínimos, usan fixtures directamente) =====

    private void givenConverterReturnsValidPoint() {
        when(geospatialConverter.toPoint(SpotFixtures.validCoordinates()))
                .thenReturn(SpotFixtures.validPoint());
    }

    private void givenLoteExists(boolean exists) {
        if (exists) {
            when(loteRepository.findById(SpotFixtures.LOTE_ID))
                    .thenReturn(Optional.of(LoteFixtures.loteContainingPoint(SpotFixtures.validPoint())));
        } else {
            when(loteRepository.findById(SpotFixtures.LOTE_ID))
                    .thenReturn(Optional.empty());
        }
    }

    private void givenLoteWithPointOutside() {
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(LoteFixtures.loteNOTContainingPoint(SpotFixtures.validPoint())));
    }

    private void givenSpotExists(boolean exists) {
        when(spotRepository.existsByLoteIdAndLineaAndPosicion(
                SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION))
                .thenReturn(exists);
    }
}