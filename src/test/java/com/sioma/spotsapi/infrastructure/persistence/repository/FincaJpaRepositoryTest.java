package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
class FincaJpaRepositoryTest {

    @Autowired
    private FincaJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Test
    void shouldReturnTrueWhenFincaAlreadyExists() {
        // GIVEN
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        FincaEntity finca = repository.save(
                new FincaEntity(
                        FincaFixtures.NOMBRE,
                        usuario.getId()
                )
        );

        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                finca.getNombre().toUpperCase(),
                finca.getUsuarioId()
        );

        // THEN
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenFincaDoesNotExist() {
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                "Otra Finca",
                FincaFixtures.USUARIO_ID + 1
        );

        // THEN
        assertFalse(exists);
    }

    @Test
    void shouldReturnEmptyListWhenNoFincasFound() {
        // GIVEN
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        // WHEN
        List<FincaEntity> fincas = repository.findAllByUsuarioId(usuario.getId());

        // THEN
        assertTrue(fincas.isEmpty());
    }

    @Test
    void shouldReturnFincasByUsuarioId() {
        // GIVEN

        UsuarioEntity usuario1 = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        UsuarioEntity usuario2 = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        "otro@mail.com",
                        UsuarioFixtures.PASSWORD
                )
        );

        repository.save(new FincaEntity("Finca 1", usuario1.getId()));
        repository.save(new FincaEntity("Finca 2", usuario1.getId()));
        repository.save(new FincaEntity("Finca 3", usuario2.getId()));

        // WHEN
        List<FincaEntity> result = repository.findAllByUsuarioId(usuario1.getId());

        // THEN
        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(f -> f.getUsuarioId()
                                .equals(usuario1.getId())
                        )
        );
        assertEquals(
                Set.of("Finca 1", "Finca 2"),
                result.stream()
                        .map(FincaEntity::getNombre)
                        .collect(Collectors.toSet())
        );
    }
}
