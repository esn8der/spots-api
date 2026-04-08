package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUsuarioByIdUseCaseTest {
    @Mock
    UsuarioRepository repository;

    @InjectMocks
    DeleteUsuarioByIdUseCase useCase;

    @Test
    void shouldTrowExceptionWhenLoteDoesNotExist() {
        // GIVEN
        when(repository.findById(LoteFixtures.ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(UsuarioNotFoundException.class,
                () -> useCase.execute(UsuarioFixtures.ID)
        );

        // THEN
        verify(repository, never()).deleteById(UsuarioFixtures.ID);
    }

    @Test
    void shouldDeleteLoteSuccessfully() {
        // GIVEN
        when(repository.findById(UsuarioFixtures.ID)).thenReturn(Optional.of(mock(Usuario.class)));

        // WHEN
        useCase.execute(UsuarioFixtures.ID);

        // THEN
        verify(repository).deleteById(UsuarioFixtures.ID);
    }
}
