package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.ports.out.GeospatialConverter;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreateLoteUseCaseTest {

    @Mock
    LoteRepository repository;

    @Mock
    FincaRepository fincaRepository;

    @Mock
    PlantaRepository plantaRepository;

    @Mock
    GeospatialConverter geospatialConverter;

    @InjectMocks
    CreateLoteUseCase useCase;

    private static final List<List<Double>> COORDINATES = List.of(
            List.of(-73.0, 4.0),
            List.of(-73.0, 4.1),
            List.of(-73.1, 4.1),
            List.of(-73.1, 4.0),
            List.of(-73.0, 4.0)
    );

    private void givenConverterReturnsPolygon() {
        when(geospatialConverter.toPolygon(COORDINATES))
                .thenReturn(LoteFixtures.anyGeocerca());
    }

    @Test
    void shouldThrowExceptionWhenFincaDoesNotExist() {
        // GIVEN
        givenConverterReturnsPolygon();
        givenFincaExists(false);

        // WHEN + THEN
        assertThrows(FincaNotFoundException.class,
                () -> useCase.execute(
                        LoteFixtures.NOMBRE,
                        COORDINATES,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID
                ));

        thenLoteIsNotSaved();
    }

    @Test
    void shouldThrowExceptionWhenTipoCultivoDoesNotExist() {
        // GIVEN
        givenConverterReturnsPolygon();
        givenFincaExists(true);
        givenPlantaExists(false);

        // WHEN + THEN
        assertThrows(PlantaNotFoundException.class,
                () -> useCase.execute(
                        LoteFixtures.NOMBRE,
                        COORDINATES,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID
                ));

        thenLoteIsNotSaved();
    }

    @Test
    void shouldThrowExceptionWhenLoteAlreadyExistsForFinca() {
        // GIVEN
        givenConverterReturnsPolygon();
        givenFincaExists(true);
        givenPlantaExists(true);
        givenLoteExists(true);

        // WHEN + THEN
        assertThrows(LoteAlreadyExistsException.class,
                () -> useCase.execute(
                        LoteFixtures.NOMBRE,
                        COORDINATES,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID
                ));

        thenLoteIsNotSaved();
    }

    @Test
    void shouldCreateLoteSuccessfully() {
        // GIVEN
        givenConverterReturnsPolygon();
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

        // THEN
        ArgumentCaptor<Lote> captor = ArgumentCaptor.forClass(Lote.class);
        verify(repository).save(captor.capture());

        Lote saved = captor.getValue();
        assertEquals(LoteFixtures.NOMBRE, saved.getNombre());
        assertEquals(LoteFixtures.FINCA_ID, saved.getFincaId());
        assertEquals(LoteFixtures.TIPO_CULTIVO_ID, saved.getTipoCultivoId());
    }

    private void givenFincaExists(boolean exists) {
        when(fincaRepository.findById(LoteFixtures.FINCA_ID))
                .thenReturn(exists ? Optional.of(mock(Finca.class)) : Optional.empty());
    }

    private void givenPlantaExists(boolean exists) {
        when(plantaRepository.findById(LoteFixtures.TIPO_CULTIVO_ID))
                .thenReturn(exists ? Optional.of(mock(Planta.class)) : Optional.empty());
    }

    private void givenLoteExists(boolean exists) {
        when(repository.existsByNombreIgnoreCaseAndFincaId(
                LoteFixtures.NOMBRE, LoteFixtures.FINCA_ID))
                .thenReturn(exists);
    }

    private void thenLoteIsNotSaved() {
        verify(repository, never()).save(any(Lote.class));
    }
}