package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.application.fixtures.PlantaFixtures;
import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreatePlantaUseCaseTest {

    @Mock
    PlantaRepository repository;

    @InjectMocks
    CreatePlantaUseCase useCase;

    @Test
    void shouldThrowExceptionWhenNombreExists() {
        // GIVEN
        givenPlantaExists(true);

        // WHEN + THEN
        assertThrows(PlantaAlreadyExistsException.class,
                () -> useCase.execute(
                        PlantaFixtures.NOMBRE
                )
        );

        // THEN
        verify(repository, never()).save(any(Planta.class));
    }

    @Test
    void shouldCreatePlantaSuccessfully() {
        // GIVEN
        givenPlantaExists(false);

        // WHEN
        useCase.execute(PlantaFixtures.NOMBRE);

        // THEN
        ArgumentCaptor<Planta> plantaCaptor = ArgumentCaptor.forClass(Planta.class);
        verify(repository).save(plantaCaptor.capture());

        Planta savedPlanta = plantaCaptor.getValue();

        assertEquals(PlantaFixtures.NOMBRE, savedPlanta.getNombre());
    }

    private void givenPlantaExists(boolean exists) {
        when(repository.existsByNombreIgnoreCase(PlantaFixtures.NOMBRE))
                .thenReturn(exists);
    }
}
