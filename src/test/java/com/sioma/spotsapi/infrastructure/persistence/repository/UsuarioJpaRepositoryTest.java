package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("UsuarioJpaRepository - Pruebas de infraestructura")
class UsuarioJpaRepositoryTest {

    @Autowired
    private UsuarioJpaRepository repository;

    @Nested
    @DisplayName("existsByEmailIgnoreCase()")
    class ExistsByEmailIgnoreCase {

        @Test
        @DisplayName("devuelve true cuando el email existe ignorando mayúsculas/minúsculas")
        void shouldReturnTrueWhenEmailExistsIgnoringCase() {
            // GIVEN
            String emailOriginal = UsuarioFixtures.EMAIL;
            repository.save(new UsuarioEntity(
                    UsuarioFixtures.NOMBRE,
                    emailOriginal,
                    UsuarioFixtures.PASSWORD
            ));

            // WHEN: Buscamos el mismo email en mayúsculas
            boolean exists = repository.existsByEmailIgnoreCase(emailOriginal.toUpperCase());

            // THEN
            assertTrue(exists, "Debe encontrar el usuario aunque el caso del email sea diferente");
        }

        @Test
        @DisplayName("devuelve true cuando el email existe con caso mixto")
        void shouldReturnTrueWhenEmailExistsWithMixedCase() {
            // GIVEN
            String emailOriginal = UsuarioFixtures.EMAIL;
            repository.save(new UsuarioEntity(
                    UsuarioFixtures.NOMBRE,
                    emailOriginal,
                    UsuarioFixtures.PASSWORD
            ));

            // WHEN: Buscamos con caso mixto (ej: "TeSt@mAiL.CoM")
            String emailBuscado = emailOriginal.substring(0, 4).toLowerCase() +
                    emailOriginal.substring(4).toUpperCase();

            boolean exists = repository.existsByEmailIgnoreCase(emailBuscado);

            // THEN
            assertTrue(exists, "Debe encontrar el usuario con cualquier combinación de mayúsculas/minúsculas en el email");
        }

        @Test
        @DisplayName("devuelve false cuando el email no existe")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            // WHEN: Buscamos un email que nunca fue registrado
            boolean exists = repository.existsByEmailIgnoreCase(UsuarioFixtures.uniqueEmail());

            // THEN
            assertFalse(exists, "Debe retornar false para emails no registrados");
        }

        @Test
        @DisplayName("devuelve false para string vacío")
        void shouldReturnFalseForEmptyString() {
            // WHEN: Buscamos con string vacío
            boolean exists = repository.existsByEmailIgnoreCase("");

            // THEN
            assertFalse(exists, "Debe retornar false para string vacío");
        }

        @Test
        @DisplayName("maneja correctamente emails con subdominios y dominios complejos")
        void shouldHandleEmailsWithSubdomainsAndComplexDomains() {
            // GIVEN: Guardamos un email con subdominio y dominio complejo
            String emailComplejo = "usuario@subdominio.ejemplo.co.uk";
            repository.save(new UsuarioEntity(
                    UsuarioFixtures.NOMBRE,
                    emailComplejo,
                    UsuarioFixtures.PASSWORD
            ));

            // WHEN: Buscamos ignorando caso
            boolean exists = repository.existsByEmailIgnoreCase("USUARIO@SUBDOMINIO.EJEMPLO.CO.UK");

            // THEN
            assertTrue(exists, "Debe encontrar emails con dominios complejos ignorando el caso");
        }

        @Test
        @DisplayName("diferencia correctamente entre emails similares pero distintos")
        void shouldDistinguishBetweenSimilarButDifferentEmails() {
            // GIVEN: Guardamos un email específico
            String emailOriginal = "usuario@test.com";
            repository.save(new UsuarioEntity(
                    UsuarioFixtures.NOMBRE,
                    emailOriginal,
                    UsuarioFixtures.PASSWORD
            ));

            // WHEN: Buscamos emails similares pero distintos
            boolean existsWithTypo = repository.existsByEmailIgnoreCase("usuar1o@test.com");
            boolean existsWithDifferentDomain = repository.existsByEmailIgnoreCase("usuario@test.co");

            // THEN
            assertFalse(existsWithTypo, "No debe encontrar email con typo");
            assertFalse(existsWithDifferentDomain, "No debe encontrar email con dominio diferente");
        }
    }
}