package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLotesByFincaIdUseCaseTests {

    @Mock
    LoteRepository repository;

    @Mock
    FincaRepository fincaRepository;

    @InjectMocks
    GetLotesByFincaIdUseCase useCase;

    @Test
    void shouldThrowExceptionWhenFincaDoesNotExist() {
        // GIVEN
        givenFincaExists(false);

        // WHEN + THEN
        assertThrows(FincaNotFoundException.class,
                () -> useCase.execute(LoteFixtures.FINCA_ID)
        );

        verify(repository, never()).findAllByFincaId(anyLong());
    }

    @Test
    void shouldReturnEmptyListWhenNoLotesFound(){
        // GIVEN
        givenFincaExists(true);
        when(repository.findAllByFincaId(LoteFixtures.FINCA_ID)).thenReturn(List.of());

        // WHEN
        List<Lote> result = useCase.execute(LoteFixtures.FINCA_ID);

        // THEN
        verify(repository).findAllByFincaId(LoteFixtures.FINCA_ID);
        verifyNoMoreInteractions(repository);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnLotesWhenLotesFoundForFinca(){
        // GIVEN
        givenFincaExists(true);

        List<Lote> expectedLotes = List.of(
                LoteFixtures.anyLote()
        );
        when(repository.findAllByFincaId(LoteFixtures.FINCA_ID)).thenReturn(expectedLotes);

        // WHEN
        List<Lote> result = useCase.execute(LoteFixtures.FINCA_ID);

        // THEN
        verify(repository).findAllByFincaId(LoteFixtures.FINCA_ID);
        assertEquals(expectedLotes, result);
    }

    private void givenFincaExists(boolean exists){
        Optional<Finca> finca = exists ? Optional.of(mock(Finca.class)) : Optional.empty();
        when(fincaRepository.findById(LoteFixtures.FINCA_ID)).thenReturn(finca);

    }
}
