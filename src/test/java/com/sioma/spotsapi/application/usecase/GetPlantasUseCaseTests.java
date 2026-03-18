package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPlantasUseCaseTests {

    @Mock
    PlantaRepository repository;

    @InjectMocks
    GetPlantasUseCase useCase;

    @Test
    void shouldReturnEmptyListWhenNoPlantasFound() {
        // GIVEN
        when(repository.findAll()).thenReturn(List.of());

        // WHEN
        List<Planta> result = useCase.execute();

        // THEN
        verify(repository).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnPlantasWhenPlantasFound() {
        // GIVEN
        List<Planta> expectedPlantas = List.of(
                new Planta(PlantaFixtures.NOMBRE)
        );
        when(repository.findAll()).thenReturn(expectedPlantas);

        // WHEN
        List<Planta> result = useCase.execute();

        // THEN
        verify(repository).findAll();
        assertEquals(expectedPlantas, result);
    }
}
