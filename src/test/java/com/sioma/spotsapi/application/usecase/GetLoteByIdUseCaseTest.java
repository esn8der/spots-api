package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
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
@DisplayName("GetLoteByIdUseCase - Pruebas de aplicación")
class GetLoteByIdUseCaseTest {

    @Mock
    private LoteRepository repository;

    @InjectMocks
    private GetLoteByIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza LoteNotFoundException cuando el lote no existe")
        void shouldThrowExceptionWhenLoteDoesNotExist() {
            // GIVEN: El lote NO existe en el repositorio
            givenLoteExists(false);

            // WHEN + THEN: Debe lanzar excepción
            assertThrows(
                    LoteNotFoundException.class,
                    () -> useCase.execute(LoteFixtures.ID),
                    "Debe lanzar excepción cuando el lote no existe"
            );
        }
    }

    @Nested
    @DisplayName("Consulta exitosa")
    class HappyPath {

        @Test
        @DisplayName("retorna el lote cuando existe")
        void shouldReturnLoteWhenExists() {
            // GIVEN: El lote existe
            givenLoteExists(true);

            // WHEN: Ejecutamos el caso de uso
            Lote result = useCase.execute(LoteFixtures.ID);

            // THEN: Verifica que se retornó el lote esperado
            assertNotNull(result, "El resultado no debe ser null");
            assertEquals(LoteFixtures.ID, result.getId(), "El ID del lote debe coincidir");
            assertEquals(LoteFixtures.NOMBRE, result.getNombre(), "El nombre del lote debe coincidir");
        }
    }

    // ===== Helpers BDD =====

    private void givenLoteExists(boolean exists) {
        if (exists) {
            when(repository.findById(LoteFixtures.ID))
                    .thenReturn(Optional.of(LoteFixtures.anyLote()));
        } else {
            when(repository.findById(LoteFixtures.ID))
                    .thenReturn(Optional.empty());
        }
    }
}