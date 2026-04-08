package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUsuarioByIdUseCase - Pruebas de aplicación")
class DeleteUsuarioByIdUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private DeleteUsuarioByIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza UsuarioNotFoundException cuando el usuario no existe")
        void shouldThrowExceptionWhenUsuarioDoesNotExist() {
            // GIVEN: El usuario NO existe
            givenUsuarioExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO eliminar nada
            assertThrows(
                    UsuarioNotFoundException.class,
                    () -> useCase.execute(UsuarioFixtures.ID),
                    "Debe lanzar excepción cuando el usuario no existe"
            );

            verify(repository, never()).deleteById(UsuarioFixtures.ID);
        }
    }

    @Nested
    @DisplayName("Eliminación exitosa")
    class HappyPath {

        @Test
        @DisplayName("elimina el usuario cuando existe")
        void shouldDeleteUsuarioSuccessfullyWhenExists() {
            // GIVEN: El usuario existe
            givenUsuarioExists(true);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(UsuarioFixtures.ID);

            // THEN: Verifica que se llamó a deleteById con el ID correcto
            verify(repository).deleteById(UsuarioFixtures.ID);
        }
    }

    // ===== Helpers BDD =====

    private void givenUsuarioExists(boolean exists) {
        if (exists) {
            when(repository.findById(UsuarioFixtures.ID))
                    .thenReturn(java.util.Optional.of(mock(com.sioma.spotsapi.domain.model.Usuario.class)));
        } else {
            when(repository.findById(UsuarioFixtures.ID))
                    .thenReturn(java.util.Optional.empty());
        }
    }
}