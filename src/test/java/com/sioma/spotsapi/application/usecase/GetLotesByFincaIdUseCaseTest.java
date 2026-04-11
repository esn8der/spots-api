package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
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
import static org.mockito.ArgumentMatchers.anyInt;
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
                    () -> useCase.execute(LoteFixtures.FINCA_ID, 0, 10),
                    "Debe lanzar excepción cuando la finca no existe"
            );

            verify(loteRepository, never()).findAllByFincaId(anyLong(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Consulta exitosa con paginación")
    class HappyPath {

        @Test
        @DisplayName("retorna PageResult vacío cuando la finca no tiene lotes")
        void shouldReturnEmptyPageResultWhenFincaHasNoLotes() {
            // GIVEN: La finca existe pero no tiene lotes
            givenFincaExists(true);
            PageResult<Lote> emptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
            when(loteRepository.findAllByFincaId(LoteFixtures.FINCA_ID, 0, 10))
                    .thenReturn(emptyPage);

            // WHEN: Ejecutamos el caso de uso
            PageResult<Lote> result = useCase.execute(LoteFixtures.FINCA_ID, 0, 10);

            // THEN: Verifica que se consultó el repositorio y el resultado es un PageResult vacío
            verify(loteRepository).findAllByFincaId(LoteFixtures.FINCA_ID, 0, 10);
            verifyNoMoreInteractions(loteRepository);
            assertTrue(result.content().isEmpty(), "Debe retornar contenido vacío cuando no hay lotes");
            assertEquals(0, result.totalElements(), "Total de elementos debe ser 0");
            assertEquals(0, result.totalPages(), "Total de páginas debe ser 0");
        }

        @Test
        @DisplayName("retorna PageResult con los lotes cuando la finca tiene registros")
        void shouldReturnPageResultWithLotesWhenFincaHasLotes() {
            // GIVEN: La finca existe y tiene lotes
            givenFincaExists(true);
            List<Lote> expectedLotes = List.of(LoteFixtures.anyLote());
            PageResult<Lote> expectedPage = new PageResult<>(expectedLotes, 0, 10, 1L, 1);
            when(loteRepository.findAllByFincaId(LoteFixtures.FINCA_ID, 0, 10))
                    .thenReturn(expectedPage);

            // WHEN: Ejecutamos el caso de uso
            PageResult<Lote> result = useCase.execute(LoteFixtures.FINCA_ID, 0, 10);

            // THEN: Verifica que se consultó el repositorio y se retornó el PageResult esperado
            verify(loteRepository).findAllByFincaId(LoteFixtures.FINCA_ID, 0, 10);
            assertEquals(expectedLotes, result.content(), "Debe retornar exactamente los lotes de la finca");
            assertEquals(1, result.totalElements(), "Total de elementos debe ser 1");
            assertEquals(1, result.totalPages(), "Total de páginas debe ser 1");
            assertEquals(0, result.page(), "Debe ser la página 0");
            assertEquals(10, result.size(), "El tamaño de página debe ser 10");
        }

        @Test
        @DisplayName("respeta los parámetros de paginación al consultar el repositorio")
        void shouldPassPaginationParamsToRepository() {
            // GIVEN: La finca existe
            givenFincaExists(true);
            PageResult<Lote> anyPage = new PageResult<>(List.of(), 2, 5, 0L, 0);
            when(loteRepository.findAllByFincaId(LoteFixtures.FINCA_ID, 2, 5))
                    .thenReturn(anyPage);

            // WHEN: Ejecutamos con página=2, tamaño=5
            useCase.execute(LoteFixtures.FINCA_ID, 2, 5);

            // THEN: Verifica que los parámetros se propagaron correctamente al repositorio
            verify(loteRepository).findAllByFincaId(LoteFixtures.FINCA_ID, 2, 5);
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