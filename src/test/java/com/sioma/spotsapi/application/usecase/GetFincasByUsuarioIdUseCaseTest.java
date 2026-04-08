package com.sioma.spotsapi.application.usecase;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
                    () -> useCase.execute(FincaFixtures.USUARIO_ID),
                    "Debe lanzar excepción cuando el usuario no existe"
            );

            verify(fincaRepository, never()).findAllByUsuarioId(anyLong());
        }
    }

    @Nested
    @DisplayName("Consulta exitosa")
    class HappyPath {

        @Test
        @DisplayName("retorna lista vacía cuando el usuario no tiene fincas")
        void shouldReturnEmptyListWhenUsuarioHasNoFincas() {
            // GIVEN: El usuario existe pero no tiene fincas
            givenUsuarioExists(true);
            when(fincaRepository.findAllByUsuarioId(FincaFixtures.USUARIO_ID))
                    .thenReturn(List.of());

            // WHEN: Ejecutamos el caso de uso
            List<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID);

            // THEN: Verifica que se consultó el repositorio y el resultado es vacío
            verify(fincaRepository).findAllByUsuarioId(FincaFixtures.USUARIO_ID);
            verifyNoMoreInteractions(fincaRepository);
            assertTrue(result.isEmpty(), "Debe retornar lista vacía cuando no hay fincas");
        }

        @Test
        @DisplayName("retorna las fincas cuando el usuario tiene fincas registradas")
        void shouldReturnFincasWhenUsuarioHasFincas() {
            // GIVEN: El usuario existe y tiene fincas
            givenUsuarioExists(true);
            List<Finca> expectedFincas = List.of(
                    new Finca(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID)
            );
            when(fincaRepository.findAllByUsuarioId(FincaFixtures.USUARIO_ID))
                    .thenReturn(expectedFincas);

            // WHEN: Ejecutamos el caso de uso
            List<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID);

            // THEN: Verifica que se consultó el repositorio y se retornaron las fincas esperadas
            verify(fincaRepository).findAllByUsuarioId(FincaFixtures.USUARIO_ID);
            assertEquals(expectedFincas, result, "Debe retornar exactamente las fincas del usuario");
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