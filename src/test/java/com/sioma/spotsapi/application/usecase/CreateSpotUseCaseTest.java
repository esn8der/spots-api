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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSpotUseCaseTest {

    @Mock
    SpotRepository repository;

    @Mock
    LoteRepository loteRepository;

    @Mock
    GeospatialConverter geospatialConverter;

    @InjectMocks
    CreateSpotUseCase useCase;

    private static final List<Double> VALID_COORDINATES = SpotFixtures.validCoordinates();

    @Test
    void shouldThrowExceptionWhenLoteDoesNotExist() {
        // GIVEN
        givenConverterReturnsValidPoint();
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(LoteNotFoundException.class,
                () -> useCase.execute(
                        VALID_COORDINATES,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                ));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSpotAlreadyExistsByLoteAndLineaAndPosicion() {
        // GIVEN
        givenConverterReturnsValidPoint();
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(
                        LoteFixtures.loteContainingPoint(SpotFixtures.validPoint())));
        when(repository.existsByLoteIdAndLineaAndPosicion(
                SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION))
                .thenReturn(true);

        // WHEN + THEN
        assertThrows(SpotAlreadyExistsException.class,
                () -> useCase.execute(
                        VALID_COORDINATES,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                ));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPointIsOutsideLote() {
        // GIVEN
        givenConverterReturnsValidPoint();
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(
                        LoteFixtures.loteNOTContainingPoint(SpotFixtures.validPoint())));
        when(repository.existsByLoteIdAndLineaAndPosicion(
                SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION))
                .thenReturn(false);

        // WHEN + THEN
        assertThrows(PointOutsideLoteException.class,
                () -> useCase.execute(
                        VALID_COORDINATES,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                ));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCreateSpotSuccessfully() {
        // GIVEN
        givenConverterReturnsValidPoint();
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(
                        LoteFixtures.loteContainingPoint(SpotFixtures.validPoint())));
        when(repository.existsByLoteIdAndLineaAndPosicion(
                SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION))
                .thenReturn(false);

        // WHEN
        useCase.execute(
                SpotFixtures.validCoordinates(),
                SpotFixtures.LOTE_ID,
                SpotFixtures.LINEA,
                SpotFixtures.POSICION
        );

        // THEN
        ArgumentCaptor<Spot> captor = ArgumentCaptor.forClass(Spot.class);
        verify(repository).save(captor.capture());

        Spot saved = captor.getValue();
        assertEquals(SpotFixtures.LOTE_ID, saved.getLoteId());
        assertEquals(SpotFixtures.LINEA, saved.getLinea());
        assertEquals(SpotFixtures.POSICION, saved.getPosicion());
    }
    // stub compartido: el converter siempre devuelve el punto válido

    private void givenConverterReturnsValidPoint() {
        when(geospatialConverter.toPoint(SpotFixtures.validCoordinates()))
                .thenReturn(SpotFixtures.validPoint());
    }
}