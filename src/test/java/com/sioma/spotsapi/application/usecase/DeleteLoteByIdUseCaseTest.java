package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
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
@DisplayName("DeleteLoteByIdUseCase - Pruebas de aplicación")
class DeleteLoteByIdUseCaseTest {

    @Mock
    private LoteRepository repository;

    @InjectMocks
    private DeleteLoteByIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza LoteNotFoundException cuando el lote no existe")
        void shouldThrowExceptionWhenLoteDoesNotExist() {
            // GIVEN: El lote NO existe
            givenLoteExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO eliminar nada
            assertThrows(
                    LoteNotFoundException.class,
                    () -> useCase.execute(LoteFixtures.ID),
                    "Debe lanzar excepción cuando el lote no existe"
            );

            verify(repository, never()).deleteById(LoteFixtures.ID);
        }
    }

    @Nested
    @DisplayName("Eliminación exitosa")
    class HappyPath {

        @Test
        @DisplayName("elimina el lote cuando existe")
        void shouldDeleteLoteSuccessfullyWhenExists() {
            // GIVEN: El lote existe
            givenLoteExists(true);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(LoteFixtures.ID);

            // THEN: Verifica que se llamó a deleteById con el ID correcto
            verify(repository).deleteById(LoteFixtures.ID);
        }
    }

    // ===== Helpers BDD =====

    private void givenLoteExists(boolean exists) {
        if (exists) {
            when(repository.findById(LoteFixtures.ID))
                    .thenReturn(java.util.Optional.of(mock(com.sioma.spotsapi.domain.model.Lote.class)));
        } else {
            when(repository.findById(LoteFixtures.ID))
                    .thenReturn(java.util.Optional.empty());
        }
    }
}