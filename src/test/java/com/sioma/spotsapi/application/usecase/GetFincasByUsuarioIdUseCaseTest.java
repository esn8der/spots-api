package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotExistsException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.fixtures.FincaFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFincasByUsuarioIdUseCaseTest {

    @Mock
    FincaRepository repository;

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    GetFincasByUsuarioIdUseCase useCase;

    @Test
    void shouldThrowExceptionWhenUsuarioDoesNotExist() {
        // GIVEN
        when(usuarioRepository.existsById(FincaFixtures.USUARIO_ID)).thenReturn(false);

        // WHEN + THEN
        assertThrows(UsuarioNotExistsException.class,
                () -> useCase.execute(FincaFixtures.USUARIO_ID
                )
        );

        verify(repository, never()).findAllByUsuarioId(anyLong());
    }

    @Test
    void shouldReturnEmptyListWhenNoFincasFound(){
        // GIVEN
        when(usuarioRepository.existsById(FincaFixtures.USUARIO_ID)).thenReturn(true);
        when(repository.findAllByUsuarioId(FincaFixtures.USUARIO_ID)).thenReturn(List.of());

        // WHEN
        List<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID);

        // THEN
        verify(repository).findAllByUsuarioId(FincaFixtures.USUARIO_ID);
        verifyNoMoreInteractions(repository);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnFincasWhenFincasFoundForUsuario(){
        // GIVEN
        when(usuarioRepository.existsById(FincaFixtures.USUARIO_ID)).thenReturn(true);
        List<Finca> expectedFincas = List.of(
                new Finca(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID
                )
        );
        when(repository.findAllByUsuarioId(FincaFixtures.USUARIO_ID))
                .thenReturn(expectedFincas);

        // WHEN
        List<Finca> result = useCase.execute(FincaFixtures.USUARIO_ID);

        // THEN
        verify(repository).findAllByUsuarioId(FincaFixtures.USUARIO_ID);
        assertEquals(expectedFincas, result);
    }

}
