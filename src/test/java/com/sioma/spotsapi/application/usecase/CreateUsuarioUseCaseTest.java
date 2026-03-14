package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.application.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUsuarioUseCaseTest {

    @Mock
    UsuarioRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

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
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCreateUsuarioSuccessfully() {

        // GIVEN
        givenUsuarioExists(false);
        when(passwordEncoder.encode(UsuarioFixtures.PASSWORD))
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
        verify(passwordEncoder).encode(UsuarioFixtures.PASSWORD);

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