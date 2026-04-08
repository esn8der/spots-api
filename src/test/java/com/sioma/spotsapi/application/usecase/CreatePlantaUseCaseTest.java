package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePlantaUseCase - Pruebas de aplicación")
class CreatePlantaUseCaseTest {

    @Mock
    private PlantaRepository repository;

    @InjectMocks
    private CreatePlantaUseCase useCase;

    @Nested
    @DisplayName("Validación de precondiciones")
    class PreconditionValidation {

        @Test
        @DisplayName("lanza PlantaAlreadyExistsException cuando el nombre ya está registrado")
        void shouldThrowPlantaAlreadyExistsExceptionWhenNombreAlreadyExists() {
            // GIVEN: Ya existe una planta con ese nombre
            givenPlantaExists(true);

            // WHEN + THEN: Debe lanzar excepción y NO guardar nada
            assertThrows(
                    PlantaAlreadyExistsException.class,
                    () -> useCase.execute(PlantaFixtures.NOMBRE),
                    "Debe lanzar excepción cuando el nombre de la planta ya está registrado"
            );

            // AND: Verifica que no se hicieron operaciones innecesarias
            verify(repository, never()).save(any(Planta.class));
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class HappyPath {

        @Test
        @DisplayName("crea la planta con el nombre correcto")
        void shouldCreatePlantaSuccessfullyWithCorrectNombre() {
            // GIVEN: El nombre no existe aún
            givenPlantaExists(false);

            // WHEN: Ejecutamos el caso de uso
            useCase.execute(PlantaFixtures.NOMBRE);

            // THEN: Verifica que se llamó a save con los datos correctos
            ArgumentCaptor<Planta> plantaCaptor = ArgumentCaptor.forClass(Planta.class);
            verify(repository).save(plantaCaptor.capture());

            Planta savedPlanta = plantaCaptor.getValue();
            assertEquals(PlantaFixtures.NOMBRE, savedPlanta.getNombre(), "El nombre de la planta debe ser el esperado");
        }
    }

    // ===== Helpers BDD =====
    private void givenPlantaExists(boolean exists) {
        when(repository.existsByNombreIgnoreCase(PlantaFixtures.NOMBRE)).thenReturn(exists);
    }
}