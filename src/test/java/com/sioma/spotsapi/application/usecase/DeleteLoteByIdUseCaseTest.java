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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteLoteByIdUseCaseTest {
    @Mock
    LoteRepository repository;

    @InjectMocks
    DeleteLoteByIdUseCase useCase;

    @Test
    void shouldTrowExceptionWhenLoteDoesNotExist() {
        // GIVEN
        when(repository.findById(LoteFixtures.ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(LoteNotFoundException.class,
                () -> useCase.execute(LoteFixtures.ID)
        );

        // THEN
        verify(repository, never()).deleteById(LoteFixtures.ID);
    }

    @Test
    void shouldDeleteLoteSuccessfully() {
        // GIVEN
        when(repository.findById(LoteFixtures.ID)).thenReturn(Optional.of(mock(Lote.class)));

        // WHEN
        useCase.execute(LoteFixtures.ID);

        // THEN
        verify(repository).deleteById(LoteFixtures.ID);
    }
}
