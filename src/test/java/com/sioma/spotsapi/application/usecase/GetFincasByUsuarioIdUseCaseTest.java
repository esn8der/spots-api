package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.FincaFixtures;
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
@DisplayName("GetFincasByUsuarioIdUseCase - Pruebas de aplicación")
class GetFincasByUsuarioIdUseCaseTest {

    @Mock
    private FincaRepository fincaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private GetFincasByUsuarioIdUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza UsuarioNotFoundException cuando el usuario no existe")
        void shouldThrowExceptionWhenUsuarioDoesNotExist() {
            // GIVEN: El usuario NO existe
            givenUsuarioExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO consultar fincas
            assertThrows(
                    UsuarioNotFoundException.class,
                    () -> useCase.execute(FincaFixtures.USUARIO_ID, 0, 10),
                    "Debe lanzar excepción cuando el usuario no existe"
            );

            verify(fincaRepository, never()).findAllByUsuarioId(anyLong(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Consulta exitosa con paginación")
    class HappyPath {

        @Test
        @DisplayName("retorna PageResult vacío cuando el usuario no tiene fincas")
        void shouldReturnEmptyPageResultWhenUsuarioHasNoFincas() {
            // GIVEN: El usuario existe pero no tiene fincas
            givenUsuarioExists(true);
            PageResult<Finca> emptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
            when(fincaRepository.findAllByUsuarioId(FincaFixtures.USUARIO_ID, 0, 10))
                    .thenReturn(emptyPage);

            // WHEN: Ejecutamos el caso de uso
            PageResult<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID, 0, 10);

            // THEN: Verifica que se consultó el repositorio y el resultado es un PageResult vacío
            verify(fincaRepository).findAllByUsuarioId(FincaFixtures.USUARIO_ID, 0, 10);
            verifyNoMoreInteractions(fincaRepository);
            assertTrue(result.content().isEmpty(), "Debe retornar contenido vacío cuando no hay fincas");
            assertEquals(0, result.totalElements(), "Total de elementos debe ser 0");
            assertEquals(0, result.totalPages(), "Total de páginas debe ser 0");
        }

        @Test
        @DisplayName("retorna PageResult con las fincas cuando el usuario tiene registros")
        void shouldReturnPageResultWithFincasWhenUsuarioHasFincas() {
            // GIVEN: El usuario existe y tiene fincas
            givenUsuarioExists(true);
            List<Finca> expectedFincas = List.of(
                    new Finca(1L, FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID)
            );
            PageResult<Finca> expectedPage = new PageResult<>(expectedFincas, 0, 10, 1L, 1);
            when(fincaRepository.findAllByUsuarioId(FincaFixtures.USUARIO_ID, 0, 10))
                    .thenReturn(expectedPage);

            // WHEN: Ejecutamos el caso de uso
            PageResult<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID, 0, 10);

            // THEN: Verifica que se consultó el repositorio y se retornó el PageResult esperado
            verify(fincaRepository).findAllByUsuarioId(FincaFixtures.USUARIO_ID, 0, 10);
            assertEquals(expectedFincas, result.content(), "Debe retornar exactamente las fincas del usuario");
            assertEquals(1, result.totalElements(), "Total de elementos debe ser 1");
            assertEquals(1, result.totalPages(), "Total de páginas debe ser 1");
            assertEquals(0, result.page(), "Debe ser la página 0");
            assertEquals(10, result.size(), "El tamaño de página debe ser 10");
        }

        @Test
        @DisplayName("respeta los parámetros de paginación al consultar el repositorio")
        void shouldPassPaginationParamsToRepository() {
            // GIVEN: El usuario existe
            givenUsuarioExists(true);
            PageResult<Finca> anyPage = new PageResult<>(List.of(), 2, 5, 0L, 0);
            when(fincaRepository.findAllByUsuarioId(FincaFixtures.USUARIO_ID, 2, 5))
                    .thenReturn(anyPage);

            // WHEN: Ejecutamos con página=2, tamaño=5
            useCase.execute(FincaFixtures.USUARIO_ID, 2, 5);

            // THEN: Verifica que los parámetros se propagaron correctamente al repositorio
            verify(fincaRepository).findAllByUsuarioId(FincaFixtures.USUARIO_ID, 2, 5);
        }
    }

    // ===== Helpers BDD =====

    private void givenUsuarioExists(boolean exists) {
        Optional<Usuario> usuario = exists
                ? Optional.of(mock(Usuario.class))
                : Optional.empty();
        when(usuarioRepository.findById(FincaFixtures.USUARIO_ID)).thenReturn(usuario);
    }
}