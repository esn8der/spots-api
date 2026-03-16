package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.domain.exception.FincaNotExistsException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotExistsException;
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

    @InjectMocks
    CreateLoteUseCase useCase;

    @Test
    void shouldThrowExceptionWhenFincaDoesNotExist() {
        // GIVEN
        givenFincaExists(false);

        // WHEN + THEN
        assertThrows(FincaNotExistsException.class,
                () -> useCase.execute(LoteFixtures.NOMBRE,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID)
        );

        // THEN
        thenLoteIsNotSaved();
    }

    @Test
    void shouldThrowExceptionWhenTipoCultivoDoesNotExist() {
        // GIVEN
        givenFincaExists(true);
        givenPlantaExists(false);

        // WHEN + THEN
        assertThrows(
                PlantaNotExistsException.class,
                () -> useCase.execute(
                        LoteFixtures.NOMBRE,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID
                )
        );

        // THEN
        thenLoteIsNotSaved();
    }

    @Test
    void shouldThrowExceptionWhenLoteAlreadyExistsForFinca() {
        // GIVEN
        givenFincaExists(true);
        givenPlantaExists(true);
        givenLoteExists(true);

        // WHEN + THEN
        assertThrows(LoteAlreadyExistsException.class,
                () -> useCase.execute(LoteFixtures.NOMBRE,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID)
                );

        // THEN
        thenLoteIsNotSaved();
    }

    @Test
    void shouldCreateLoteSuccessfully(){
        // GIVEN
        givenFincaExists(true);
        givenPlantaExists(true);
        givenLoteExists(false);

        //WHEN
        useCase.execute(
                LoteFixtures.NOMBRE,
                LoteFixtures.FINCA_ID,
                LoteFixtures.TIPO_CULTIVO_ID
        );

        //THEN
        ArgumentCaptor<Lote> loteCaptor = ArgumentCaptor.forClass(Lote.class);
        verify(repository).save(loteCaptor.capture());

        Lote loteSaved = loteCaptor.getValue();

        assertEquals(LoteFixtures.NOMBRE, loteSaved.getNombre());
        assertEquals(LoteFixtures.FINCA_ID, loteSaved.getFincaId());
        assertEquals(LoteFixtures.TIPO_CULTIVO_ID, loteSaved.getTipoCultivoId());
    }

    private void givenFincaExists(boolean exists) {
        when(fincaRepository.existsById(LoteFixtures.FINCA_ID))
                .thenReturn(exists);
    }

    private void givenPlantaExists(boolean exists) {
        when(plantaRepository.existsById(LoteFixtures.TIPO_CULTIVO_ID))
                .thenReturn(exists);
    }

    private void givenLoteExists(boolean exists) {
        when(repository.existsByNombreIgnoreCaseAndFincaId(
                LoteFixtures.NOMBRE,
                LoteFixtures.FINCA_ID))
                .thenReturn(exists);
    }

    private void thenLoteIsNotSaved() {
        verify(repository, never())
                .save(any(Lote.class));
    }
}
