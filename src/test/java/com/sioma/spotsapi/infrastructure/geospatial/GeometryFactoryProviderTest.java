package com.sioma.spotsapi.infrastructure.geospatial;

import com.sioma.spotsapi.infrastructure.geospatial.exception.InvalidGeoSpatialException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GeometryFactoryProvider - Conversión geo-espacial")
class GeometryFactoryProviderTest {

    // Instancia directa, sin Spring, sin @Autowired
    private final GeometryFactoryProvider converter = new GeometryFactoryProvider();

    @Nested
    @DisplayName("toPoint()")
    class ToPoint {

        @Test
        @DisplayName("convierte lista de 2 coordenadas a Point con SRID 4326")
        void shouldCreatePointSuccessfully() {
            // Given: Coordenadas de un punto válido
            var coordinates = List.of(-73.647243, 3.896533);

            // When: Intentamos crear un Point
            Point point = converter.toPoint(coordinates);

            // Then: El Point se crea correctamente
            assertNotNull(point);
            assertEquals(-73.647243, point.getX(), 0.0000001);
            assertEquals(3.896533, point.getY(), 0.0000001);
            assertEquals(4326, point.getSRID()); // ✅ WGS84
        }

        @Test
        @DisplayName("lanza excepción si la lista no tiene exactamente 2 coordenadas")
        void shouldThrowExceptionIfCoordinatesListHasInvalidSize() {
            // Given: Coordenadas de un punto inválido
            var coordinates = List.of(-73.0); // ❌ Solo 1 coordenada

            // When & Then: Debe lanzar excepción
            InvalidGeoSpatialException ex = assertThrows(
                    InvalidGeoSpatialException.class,
                    () -> converter.toPoint(coordinates)
            );
            assertTrue(ex.getMessage().contains("Un punto debe tener 2 coordenadas"));
        }
    }

    @Nested
    @DisplayName("toPolygon()")
    class ToPolygon {

        @Test
        @DisplayName("convierte lista cerrada de coordenadas a Polygon válido")
        void shouldCreatePolygonSuccessfully() {
            // Given: Polígono cuadrado cerrado
            var coordinates = List.of(
                    List.of(-73.0, 4.0),
                    List.of(-73.0, 4.1),
                    List.of(-73.1, 4.1),
                    List.of(-73.1, 4.0),
                    List.of(-73.0, 4.0) // ← Cierre
            );

            // When: Intentamos crear un Polygon
            Polygon polygon = converter.toPolygon(coordinates);

            // Then: El Polygon se crea correctamente
            assertNotNull(polygon);
            assertEquals(4326, polygon.getSRID());
            assertTrue(polygon.isValid());
            assertEquals(5, polygon.getNumPoints());
        }

        @Test
        @DisplayName("lanza excepción si el polígono no está cerrado")
        void shouldThrowExceptionIfPolygonIsNotClosed() {
            // Given: Primer y último punto diferente
            var coordinates = List.of(
                    List.of(-73.0, 4.0),
                    List.of(-73.0, 4.1),
                    List.of(-73.1, 4.1),
                    List.of(-73.1, 4.0)
                    // ❌ Falta cierre
            );

            // When & Then: Debe lanzar excepción
            InvalidGeoSpatialException ex = assertThrows(
                    InvalidGeoSpatialException.class,
                    () -> converter.toPolygon(coordinates)
            );
            assertTrue(ex.getMessage().contains("El polígono debe estar cerrado"));
        }

        @Test
        @DisplayName("lanza excepción si el polígono tiene menos de 4 puntos")
        void shouldThrowExceptionIfPolygonHasLessThanFourPoints() {
            // Given: Lista de coords con menos de 4 puntos
            var coordinates = List.of(
                    List.of(-73.0, 4.0),
                    List.of(-73.0, 4.1),
                    List.of(-73.1, 4.1)
            );

            // When & Then: Debe lanzar excepción
            assertThrows(InvalidGeoSpatialException.class,
                    () -> converter.toPolygon(coordinates));
        }

        @Test
        @DisplayName("lanza excepción si el polígono no es válido geométricamente")
        void shouldThrowExceptionIfPolygonIsNotValidGeometry() {
            // Given: Polígono que se cruza a sí mismo ("bowtie")
            var coordinates = List.of(
                    List.of(-73.0, 4.0),
                    List.of(-73.1, 4.1),
                    List.of(-73.0, 4.1),
                    List.of(-73.1, 4.0),
                    List.of(-73.0, 4.0)
            );

            // When & Then: Debe lanzar excepción
            InvalidGeoSpatialException ex = assertThrows(
                    InvalidGeoSpatialException.class,
                    () -> converter.toPolygon(coordinates)
            );
            assertTrue(ex.getMessage().contains("La geocerca no es válida"));
        }
    }
}