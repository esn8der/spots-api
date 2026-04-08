package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.FincaFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFincaUseCase - Pruebas de aplicación")
class CreateFincaUseCaseTest {

    @Mock
    private FincaRepository fincaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CreateFincaUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza UsuarioNotFoundException cuando el usuario no existe")
        void shouldThrowUsuarioNotFoundExceptionWhenUsuarioDoesNotExist() {
            // GIVEN: El usuario NO existe en el repositorio
            givenUsuarioExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    UsuarioNotFoundException.class,
                    () -> useCase.execute(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID),
                    "Debe lanzar excepción cuando el usuario no existe"
            );

            thenFincaIsNotSaved();
        }

        @Test
        @DisplayName("lanza FincaAlreadyExistsException cuando ya existe una finca con ese nombre para el usuario")
        void shouldThrowFincaAlreadyExistsExceptionWhenFincaAlreadyExistsForUser() {
            // GIVEN: El usuario existe, pero ya tiene una finca con ese nombre
            givenUsuarioExists(true);
            givenFincaExists(true);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    FincaAlreadyExistsException.class,
                    () -> useCase.execute(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID),
                    "Debe lanzar excepción cuando la finca ya existe para ese usuario"
            );

            thenFincaIsNotSaved();
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class HappyPath {

        @Test
        @DisplayName("crea la finca cuando el usuario existe y el nombre es único")
        void shouldCreateFincaSuccessfullyWhenUsuarioExistsAndNameIsUnique() {
            // GIVEN: El usuario existe y no hay finca con ese nombre
            givenUsuarioExists(true);
            givenFincaExists(false);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID);

            // THEN: Verifica que se llamó a save con los datos correctos
            ArgumentCaptor<Finca> fincaCaptor = ArgumentCaptor.forClass(Finca.class);
            verify(fincaRepository).save(fincaCaptor.capture());

            Finca fincaSaved = fincaCaptor.getValue();
            assertEquals(FincaFixtures.NOMBRE, fincaSaved.getNombre(), "El nombre de la finca debe ser el esperado");
            assertEquals(FincaFixtures.USUARIO_ID, fincaSaved.getUsuarioId(), "El usuarioId de la finca debe ser el esperado");
        }
    }

    // ===== Helpers =====
    private void givenUsuarioExists(boolean exists) {
        Optional<Usuario> usuario = exists ? Optional.of(mock(Usuario.class)) : Optional.empty();
        when(usuarioRepository.findById(FincaFixtures.USUARIO_ID)).thenReturn(usuario);
    }

    private void givenFincaExists(boolean exists) {
        when(fincaRepository.existsByNombreIgnoreCaseAndUsuarioId(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID))
                .thenReturn(exists);
    }

    private void thenFincaIsNotSaved() {
        verify(fincaRepository, never()).save(any(Finca.class));
    }
}