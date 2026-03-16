package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class FincaJpaRepositoryTest extends PostgresContainerConfig {

    @Autowired
    private FincaJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Test
    @DisplayName("Should return true when finca already exists by usuarioId and nombre ignore case")
    void shouldReturnTrueWhenFincaAlreadyExists(){
        // GIVEN
        usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        repository.save(
                new FincaEntity(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID
                )
        );

        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                FincaFixtures.NOMBRE.toUpperCase(),
                FincaFixtures.USUARIO_ID
        );

        // THEN
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when finca does not exists by usuarioId and nombre ignore case")
    void shouldReturnFalseWhenFincaDoesNotExist(){
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndUsuarioId(
                "Otra Finca",
                FincaFixtures.USUARIO_ID + 1
        );

        // THEN
        assertFalse(exists);
    }
}
