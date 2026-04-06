package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.ports.out.PasswordHasher;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
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
class CreateUsuarioUseCaseTest {

    @Mock
    UsuarioRepository repository;

    @Mock
    PasswordHasher passwordHasher;

    @InjectMocks
    CreateUsuarioUseCase useCase;

    @Test
    void shouldThrowExceptionWhenEmailExists() {

        // GIVEN
        givenUsuarioExists(true);

        // WHEN + THEN
        assertThrows(
                UsuarioAlreadyExistsException.class,
                () -> useCase.execute(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        // THEN
        verify(passwordHasher, never()).hash(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCreateUsuarioSuccessfully() {

        // GIVEN
        givenUsuarioExists(false);
        when(passwordHasher.hash(UsuarioFixtures.PASSWORD))
                .thenReturn(UsuarioFixtures.PASSWORD_HASHED);

        // WHEN
        useCase.execute(
                UsuarioFixtures.NOMBRE,
                UsuarioFixtures.EMAIL,
                UsuarioFixtures.PASSWORD
        );

        // THEN
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(usuarioCaptor.capture());
        verify(passwordHasher).hash(UsuarioFixtures.PASSWORD);

        Usuario savedUsuario = usuarioCaptor.getValue();

        assertEquals(UsuarioFixtures.NOMBRE, savedUsuario.getNombre());
        assertEquals(UsuarioFixtures.EMAIL, savedUsuario.getEmail());
        assertEquals(UsuarioFixtures.PASSWORD_HASHED, savedUsuario.getPassword());
    }

    private void givenUsuarioExists(boolean exists) {
        when(repository.existsByEmailIgnoreCase(
                        UsuarioFixtures.EMAIL))
                .thenReturn(exists);
    }
}