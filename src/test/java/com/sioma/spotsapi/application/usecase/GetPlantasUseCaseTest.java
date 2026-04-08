package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPlantasUseCase - Pruebas de aplicación")
class GetPlantasUseCaseTest {

    @Mock
    private PlantaRepository repository;

    @InjectMocks
    private GetPlantasUseCase useCase;

    @Nested
    @DisplayName("Consulta exitosa")
    class HappyPath {

        @Test
        @DisplayName("retorna lista vacía cuando no hay plantas registradas")
        void shouldReturnEmptyListWhenNoPlantasFound() {
            // GIVEN: El repositorio retorna lista vacía
            when(repository.findAll()).thenReturn(List.of());

            // WHEN: Ejecutamos el caso de uso
            List<Planta> result = useCase.execute();

            // THEN: Verifica que se consultó el repositorio y el resultado es vacío
            verify(repository).findAll();
            assertTrue(result.isEmpty(), "Debe retornar lista vacía cuando no hay plantas");
        }

        @Test
        @DisplayName("retorna las plantas cuando existen registros")
        void shouldReturnPlantasWhenPlantasFound() {
            // GIVEN: El repositorio retorna una lista con plantas
            List<Planta> expectedPlantas = List.of(new Planta(PlantaFixtures.NOMBRE), new Planta(PlantaFixtures.uniqueName()));
            when(repository.findAll()).thenReturn(expectedPlantas);

            // WHEN: Ejecutamos el caso de uso
            List<Planta> result = useCase.execute();

            // THEN: Verifica que se consultó el repositorio y se retornaron las plantas esperadas
            verify(repository).findAll();
            assertEquals(expectedPlantas, result, "Debe retornar exactamente las plantas registradas");
        }
    }
}