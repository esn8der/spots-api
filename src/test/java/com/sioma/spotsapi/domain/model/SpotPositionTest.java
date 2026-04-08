package com.sioma.spotsapi.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SpotPosition - Tests de dominio")
class SpotPositionTest {

    @Nested
    @DisplayName("Constructor con validación")
    class ConstructorTest {
        @Test
        @DisplayName("Lanzar excepción si posición es menor que 1")
        void shouldThrowExceptionWhenInvalidPosition() {
            // Given: Línea válida y posición inválida
            int linea = 1;
            int posicion = -1;

            // When + Then: Debe lanzar excepción
            assertThrows(IllegalArgumentException.class,
                    () -> new SpotPosition(linea, posicion)
            );
        }

        @Test
        @DisplayName("Lanzar excepción si linea es menor a 1")
        void shouldThrowExceptionWhenInvalidLinea() {
            // Given: Línea inválida y posición válida
            int linea = -1;
            int posicion = 1;

            // When + Then: Debe lanzar excepción
            assertThrows(IllegalArgumentException.class,
                    () -> new SpotPosition(linea, posicion)
            );
        }

        @Test
        @DisplayName("Lanzar excepción si ambos valores son inválidos")
        void shouldThrowExceptionWhenInvalidArguments() {
            // Given: Línea inválida y posición inválida
            int linea = -1;
            int posicion = -1;

            // When + Then: Debe lanzar excepción
            assertThrows(IllegalArgumentException.class,
                    () -> new SpotPosition(linea, posicion)
            );
        }

        @Test
        @DisplayName("Crear instancia válida con valores positivos")
        void shouldCreateSpotPositionWhenValidArguments() {
            // Given: Línea válida y posición válida
            int linea = 1;
            int posicion = 1;

            // When: Intentamos crear instancia
            SpotPosition spotPosition = new SpotPosition(linea, posicion);

            // Then: La instancia se crea correctamente
            assertEquals(linea, spotPosition.linea());
            assertEquals(posicion, spotPosition.posicion());
        }
    }
}
