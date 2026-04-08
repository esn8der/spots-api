package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.ports.out.PasswordHasher;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUsuarioUseCase - Pruebas de aplicación")
class CreateUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateUsuarioUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza UsuarioAlreadyExistsException cuando el email ya está registrado")
        void shouldThrowUsuarioAlreadyExistsExceptionWhenEmailAlreadyExists() {
            // GIVEN: Ya existe un usuario con ese email
            givenUsuarioExists(true);

            // WHEN + THEN: Debe lanzar excepción y NO procesar nada
            assertThrows(
                    UsuarioAlreadyExistsException.class,
                    () -> useCase.execute(
                            UsuarioFixtures.NOMBRE,
                            UsuarioFixtures.EMAIL,
                            UsuarioFixtures.PASSWORD
                    ),
                    "Debe lanzar excepción cuando el email ya está registrado"
            );

            // AND: Verifica que no se hicieron operaciones innecesarias
            verify(passwordHasher, never()).hash(anyString());
            verify(repository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class HappyPath {

        @Test
        @DisplayName("hashea el password antes de guardar el usuario")
        void shouldHashPasswordBeforeSavingUsuario() {
            // GIVEN: El email no existe y el hasher está configurado
            givenUsuarioExists(false);
            when(passwordHasher.hash(UsuarioFixtures.PASSWORD))
                    .thenReturn(UsuarioFixtures.PASSWORD_HASHED);

            // WHEN
            useCase.execute(UsuarioFixtures.NOMBRE, UsuarioFixtures.EMAIL, UsuarioFixtures.PASSWORD);

            // THEN: Verifica que el hasher fue llamado con el password en claro
            verify(passwordHasher).hash(UsuarioFixtures.PASSWORD);
            verify(repository, never()).save(argThat(u -> UsuarioFixtures.PASSWORD.equals(u.getPassword())));
        }

        @Test
        @DisplayName("crea el usuario con los datos correctos y password hasheado")
        void shouldCreateUsuarioSuccessfullyWithHashedPassword() {
            // GIVEN: El email no existe y el hasher está configurado
            givenUsuarioExists(false);
            when(passwordHasher.hash(UsuarioFixtures.PASSWORD))
                    .thenReturn(UsuarioFixtures.PASSWORD_HASHED);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(UsuarioFixtures.NOMBRE, UsuarioFixtures.EMAIL, UsuarioFixtures.PASSWORD);

            // THEN: Verifica que se llamó a save con los datos correctos
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(repository).save(usuarioCaptor.capture());

            Usuario savedUsuario = usuarioCaptor.getValue();
            assertEquals(UsuarioFixtures.NOMBRE, savedUsuario.getNombre(), "El nombre debe ser el esperado");
            assertEquals(UsuarioFixtures.EMAIL, savedUsuario.getEmail(), "El email debe ser el esperado");
            assertEquals(UsuarioFixtures.PASSWORD_HASHED, savedUsuario.getPassword(), "El password debe estar hasheado");
        }
    }

    // ===== Helpers =====
    private void givenUsuarioExists(boolean exists) {
        when(repository.existsByEmailIgnoreCase(UsuarioFixtures.EMAIL)).thenReturn(exists);
    }
}