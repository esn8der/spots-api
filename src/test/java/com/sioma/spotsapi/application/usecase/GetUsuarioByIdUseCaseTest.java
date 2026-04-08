package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUsuarioByIdUseCaseTest {
    @Mock
    UsuarioRepository repository;

    @InjectMocks
    GetUsuarioByIdUseCase useCase;

    @Test
    void shouldThrowExceptionWhenUsuarioDoesNotExist() {
        // GIVEN
        when(repository.findById(UsuarioFixtures.ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(UsuarioNotFoundException.class,
                () -> useCase.execute(UsuarioFixtures.ID)
        );
    }

    @Test
    void shouldReturnUsuarioWhenExists() {
        // GIVEN
        Usuario usuario = UsuarioFixtures.anyUsuario();
        when(repository.findById(UsuarioFixtures.ID))
                .thenReturn(Optional.of(usuario));

        // WHEN
        Usuario result = useCase.execute(UsuarioFixtures.ID);

        // THEN
        assertSame(usuario, result);
    }
}
