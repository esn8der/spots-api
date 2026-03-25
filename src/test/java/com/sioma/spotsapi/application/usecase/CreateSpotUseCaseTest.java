package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.SpotFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSpotUseCaseTest {

    @Mock
    SpotRepository repository;

    @Mock
    LoteRepository loteRepository;

    @InjectMocks
    CreateSpotUseCase useCase;

    @Test
    void shouldThrowExceptionWhenLoteDoesNotExist() {
        // GIVEN
        when(loteRepository.findById(SpotFixtures.LOTE_ID)).thenReturn(Optional.empty());

        Point point = SpotFixtures.validPoint();

        // WHEN + THEN
        assertThrows(LoteNotFoundException.class,
                () -> useCase.execute(
                        point,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION

                )
        );

        // THEN
        verify(repository, never()).save(any(Spot.class));
    }

    @Test
    void shouldThrowExceptionWhenPointIsOutsideLote() {
        // GIVEN
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(LoteFixtures.loteNOTContainingPoint(SpotFixtures.validPoint())));

        Point point = SpotFixtures.validPoint();

        // WHEN + THEN
        assertThrows(PointOutsideLoteException.class,
                () -> useCase.execute(
                        point,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                )

        );

        // THEN
        verify(repository, never()).save(any(Spot.class));
    }

    @Test
    void shouldThrowExceptionWhenSpotAlreadyExistsByLoteAndLineaAndPosicion() {
        // GIVEN
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(LoteFixtures.loteContainingPoint(SpotFixtures.validPoint())));
        when(repository.existsByLoteIdAndLineaAndPosicion(SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION)).thenReturn(true);

        Point point = SpotFixtures.validPoint();

        // WHEN + THEN
        assertThrows(SpotAlreadyExistsException.class,
                () -> useCase.execute(
                        point,
                        SpotFixtures.LOTE_ID,
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                )

        );

        // THEN
        verify(repository, never()).save(any(Spot.class));
    }

    @Test
    void shouldCreateSpotSuccessfully() {
        // GIVEN
        when(loteRepository.findById(SpotFixtures.LOTE_ID))
                .thenReturn(Optional.of(LoteFixtures.loteContainingPoint(SpotFixtures.validPoint())));
        when(repository.existsByLoteIdAndLineaAndPosicion(SpotFixtures.LOTE_ID, SpotFixtures.LINEA, SpotFixtures.POSICION))
                .thenReturn(false);

        // WHEN
        useCase.execute(
                SpotFixtures.validPoint(),
                SpotFixtures.LOTE_ID,
                SpotFixtures.LINEA,
                SpotFixtures.POSICION
        );

        // THEN
        ArgumentCaptor<Spot> spotCaptor = ArgumentCaptor.forClass(Spot.class);
        verify(repository).save(spotCaptor.capture());

        Spot savedSpot = spotCaptor.getValue();

        assertEquals(SpotFixtures.LOTE_ID, savedSpot.getLoteId());
        assertEquals(SpotFixtures.LINEA, savedSpot.getLinea());
        assertEquals(SpotFixtures.POSICION, savedSpot.getPosicion());
    }

}

