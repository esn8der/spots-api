package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UsuarioJpaRepositoryTest extends PostgresContainerConfig{

    @Autowired
    private UsuarioJpaRepository repository;

    @Test
    void shouldReturnTrueWhenEmailExistsIgnoringCase() {
        // GIVEN
        repository.save(new UsuarioEntity(
                UsuarioFixtures.NOMBRE,
                UsuarioFixtures.EMAIL,
                UsuarioFixtures.PASSWORD
                )
        );

        // WHEN
        boolean exists = repository.existsByEmailIgnoreCase(UsuarioFixtures.EMAIL.toUpperCase());

        // THEN
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // WHEN
        boolean exists = repository.existsByEmailIgnoreCase("otro@mail.com");

        // THEN
        assertFalse(exists);
    }
}