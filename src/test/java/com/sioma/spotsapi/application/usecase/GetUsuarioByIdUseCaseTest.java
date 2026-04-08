package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUsuarioByIdUseCase - Pruebas de aplicación")
class GetUsuarioByIdUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private GetUsuarioByIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza UsuarioNotFoundException cuando el usuario no existe")
        void shouldThrowExceptionWhenUsuarioDoesNotExist() {
            // GIVEN: El usuario NO existe en el repositorio
            givenUsuarioExists(false);

            // WHEN + THEN: Debe lanzar excepción
            assertThrows(
                    UsuarioNotFoundException.class,
                    () -> useCase.execute(UsuarioFixtures.ID),
                    "Debe lanzar excepción cuando el usuario no existe"
            );
        }
    }

    @Nested
    @DisplayName("Consulta exitosa")
    class HappyPath {

        @Test
        @DisplayName("retorna el usuario cuando existe")
        void shouldReturnUsuarioWhenExists() {
            // GIVEN: El usuario existe
            givenUsuarioExists(true);

            // WHEN: Ejecutamos el caso de uso
            Usuario result = useCase.execute(UsuarioFixtures.ID);

            // THEN: Verifica que se retornó el usuario esperado
            assertNotNull(result, "El resultado no debe ser null");
            assertEquals(UsuarioFixtures.ID, result.getId(), "El ID del usuario debe coincidir");
            assertEquals(UsuarioFixtures.NOMBRE, result.getNombre(), "El nombre del usuario debe coincidir");
            assertEquals(UsuarioFixtures.EMAIL, result.getEmail(), "El email del usuario debe coincidir");
        }
    }

    // ===== Helpers BDD =====

    private void givenUsuarioExists(boolean exists) {
        if (exists) {
            when(repository.findById(UsuarioFixtures.ID))
                    .thenReturn(Optional.of(UsuarioFixtures.anyUsuario()));
        } else {
            when(repository.findById(UsuarioFixtures.ID))
                    .thenReturn(Optional.empty());
        }
    }
}