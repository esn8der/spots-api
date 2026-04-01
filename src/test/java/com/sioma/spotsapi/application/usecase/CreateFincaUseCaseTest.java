package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreateFincaUseCaseTest {

    @Mock
    FincaRepository repository;

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    CreateFincaUseCase useCase;

    @Test
    void  shouldThrowExceptionWhenUsuarioDoesNotExist() {
        // GIVEN
        givenUsuarioExists(false);

        // WHEN + THEN
        assertThrows(
                UsuarioNotFoundException.class,
                () -> useCase.execute(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID
                )
        );

        // THEN
        thenLoteIsNotSaved();
    }

    @Test
    void shouldThrowExceptionWhenFincaAlreadyExistsForUser() {
        // GIVEN
        givenUsuarioExists(true);
        givenFincaExists(true);

        // WHEN + THEN
        assertThrows(
                FincaAlreadyExistsException.class,
                () -> useCase.execute(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID
                )
        );

        // THEN
        thenLoteIsNotSaved();
    }

    @Test
    void shouldCreateFincaSuccessfully() {
        // GIVEN
        givenUsuarioExists(true);
        givenFincaExists(false);

        // WHEN
        useCase.execute(FincaFixtures.NOMBRE, FincaFixtures.USUARIO_ID);

        // THEN
        ArgumentCaptor<Finca> fincaCaptor = ArgumentCaptor.forClass(Finca.class);
        verify(repository).save(fincaCaptor.capture());

        Finca fincaSaved = fincaCaptor.getValue();

        assertEquals(FincaFixtures.NOMBRE, fincaSaved.getNombre());
        assertEquals(FincaFixtures.USUARIO_ID, fincaSaved.getUsuarioId());
    }

    private void givenUsuarioExists(boolean exists) {
        Optional<Usuario> usuario = exists ? Optional.of(mock(Usuario.class)) : Optional.empty();
        when(usuarioRepository.findById(FincaFixtures.USUARIO_ID))
                .thenReturn(usuario);
    }

    private void givenFincaExists(boolean exists){
        when(repository
                .existsByNombreIgnoreCaseAndUsuarioId(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID))
                .thenReturn(exists);

    }

    private void thenLoteIsNotSaved() {
        verify(repository, never())
                .save(any(Finca.class));
    }
}
