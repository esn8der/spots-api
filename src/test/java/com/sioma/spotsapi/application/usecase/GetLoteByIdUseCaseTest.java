package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLoteByIdUseCaseTest {
    @Mock
    LoteRepository repository;

    @InjectMocks
    GetLoteByIdUseCase useCase;

    @Test
    void shouldThrowExceptionWhenLoteDoesNotExist() {
        // GIVEN
        when(repository.findById(LoteFixtures.ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(LoteNotFoundException.class,
                () -> useCase.execute(LoteFixtures.ID)
        );
    }

    @Test
    void shouldReturnLoteWhenExists() {
        // GIVEN
        Lote lote = LoteFixtures.anyLote();
        when(repository.findById(LoteFixtures.ID)).thenReturn(Optional.of(lote));

        // WHEN
        Lote result = useCase.execute(LoteFixtures.ID);

        // THEN
        assertSame(lote, result);
    }

}
