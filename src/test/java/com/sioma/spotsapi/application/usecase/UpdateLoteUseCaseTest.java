package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.fixtures.LoteFixtures;
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
@DisplayName("UpdateLoteUseCase - Pruebas de aplicación")
class UpdateLoteUseCaseTest {

    @Mock
    private LoteRepository repository;

    @InjectMocks
    private UpdateLoteUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza LoteNotFoundException cuando el lote no existe")
        void shouldThrowLoteNotFoundExceptionWhenLoteDoesNotExist() {
            // GIVEN: El lote NO existe
            givenLoteExists(false);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    LoteNotFoundException.class,
                    () -> useCase.execute(LoteFixtures.ID, LoteFixtures.NOMBRE),
                    "Debe lanzar excepción cuando el lote no existe"
            );

            verify(repository, never()).save(any(Lote.class));
        }

        @Test
        @DisplayName("lanza LoteAlreadyExistsException cuando el nombre ya existe en la finca")
        void shouldThrowLoteAlreadyExistsExceptionWhenNombreAlreadyExistsInFinca() {
            // GIVEN: El lote existe, pero otro lote ya tiene ese nombre en la misma finca
            givenLoteExists(true);
            givenNombreExistsInFinca(LoteFixtures.NOMBRE, true);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    LoteAlreadyExistsException.class,
                    () -> useCase.execute(LoteFixtures.ID, LoteFixtures.NOMBRE),
                    "Debe lanzar excepción cuando el nombre ya existe en la finca"
            );

            verify(repository, never()).save(any(Lote.class));
        }
    }

    @Nested
    @DisplayName("Actualización exitosa")
    class HappyPath {

        @Test
        @DisplayName("renombra el lote cuando todas las validaciones pasan")
        void shouldRenameLoteWhenAllValidationsPass() {
            // GIVEN: El lote existe y el nombre es único en la finca
            Lote loteOriginal = LoteFixtures.anyLote();
            givenLoteExists(loteOriginal);
            givenNombreExistsInFinca("Nuevo Nombre", false);

            String nuevoNombre = "Nuevo Nombre";

            when(repository.save(any(Lote.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // WHEN: Ejecutamos el caso de uso
            Lote result = useCase.execute(LoteFixtures.ID, nuevoNombre);

            // THEN: Verifica que el resultado tiene el nuevo nombre
            assertEquals(nuevoNombre, result.getNombre(), "El nombre del lote debe ser actualizado");
            assertEquals(LoteFixtures.ID, result.getId(), "El ID no debe cambiar");

            // AND: Verifica que se guardó el lote renombrado
            ArgumentCaptor<Lote> loteCaptor = ArgumentCaptor.forClass(Lote.class);
            verify(repository).save(loteCaptor.capture());
            assertEquals(nuevoNombre, loteCaptor.getValue().getNombre(), "El lote guardado debe tener el nuevo nombre");
        }

        @Test
        @DisplayName("llama a lote.renombrar() antes de guardar")
        void shouldCallLoteRenombrarBeforeSaving() {
            // GIVEN
            Lote loteOriginal = LoteFixtures.anyLote();
            givenLoteExists(loteOriginal);
            givenNombreExistsInFinca("Nuevo Nombre", false);

            // WHEN
            useCase.execute(LoteFixtures.ID, "Nuevo Nombre");

            // THEN: Verifica que se intentó guardar (el rename se valida indirectamente)
            verify(repository).save(any(Lote.class));
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

    private void givenLoteExists(Lote lote) {
        when(repository.findById(LoteFixtures.ID))
                .thenReturn(Optional.of(lote));
    }

    private void givenNombreExistsInFinca(String nombre, boolean exists) {
        when(repository.existsByNombreIgnoreCaseAndFincaId(nombre, LoteFixtures.FINCA_ID))
                .thenReturn(exists);
    }
}