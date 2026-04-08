package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetLotesByFincaIdUseCase - Pruebas de aplicación")
class GetLotesByFincaIdUseCaseTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private GetLotesByFincaIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza FincaNotFoundException cuando la finca no existe")
        void shouldThrowExceptionWhenFincaDoesNotExist() {
            // GIVEN: La finca NO existe
            givenFincaExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO consultar lotes
            assertThrows(
                    FincaNotFoundException.class,
                    () -> useCase.execute(LoteFixtures.FINCA_ID),
                    "Debe lanzar excepción cuando la finca no existe"
            );

            verify(loteRepository, never()).findAllByFincaId(anyLong());
        }
    }

    @Nested
    @DisplayName("Consulta exitosa")
    class HappyPath {

        @Test
        @DisplayName("retorna lista vacía cuando la finca no tiene lotes")
        void shouldReturnEmptyListWhenFincaHasNoLotes() {
            // GIVEN: La finca existe pero no tiene lotes
            givenFincaExists(true);
            when(loteRepository.findAllByFincaId(LoteFixtures.FINCA_ID))
                    .thenReturn(List.of());

            // WHEN: Ejecutamos el caso de uso
            List<Lote> result = useCase.execute(LoteFixtures.FINCA_ID);

            // THEN: Verifica que se consultó el repositorio y el resultado es vacío
            verify(loteRepository).findAllByFincaId(LoteFixtures.FINCA_ID);
            verifyNoMoreInteractions(loteRepository);
            assertTrue(result.isEmpty(), "Debe retornar lista vacía cuando no hay lotes");
        }

        @Test
        @DisplayName("retorna los lotes cuando la finca tiene lotes registrados")
        void shouldReturnLotesWhenFincaHasLotes() {
            // GIVEN: La finca existe y tiene lotes
            givenFincaExists(true);
            List<Lote> expectedLotes = List.of(LoteFixtures.anyLote());
            when(loteRepository.findAllByFincaId(LoteFixtures.FINCA_ID))
                    .thenReturn(expectedLotes);

            // WHEN: Ejecutamos el caso de uso
            List<Lote> result = useCase.execute(LoteFixtures.FINCA_ID);

            // THEN: Verifica que se consultó el repositorio y se retornaron los lotes esperados
            verify(loteRepository).findAllByFincaId(LoteFixtures.FINCA_ID);
            assertEquals(expectedLotes, result, "Debe retornar exactamente los lotes de la finca");
        }
    }

    // ===== Helpers BDD =====

    private void givenFincaExists(boolean exists) {
        Optional<Finca> finca = exists
                ? Optional.of(mock(Finca.class))
                : Optional.empty();
        when(fincaRepository.findById(LoteFixtures.FINCA_ID)).thenReturn(finca);
    }
}