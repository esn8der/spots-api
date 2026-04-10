package com.sioma.spotsapi.domain.model;

import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.SpotFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lote - Pruebas de dominio")
class LoteTest {

    @Nested
    @DisplayName("crearSpot()")
    class CrearSpot {

        @Test
        @DisplayName("crea el spot cuando el punto está dentro de la geocerca")
        void shouldCreateSpotWhenPointIsInsideGeofence() {
            // Given: Un lote con una geocerca conocida
            Polygon geocerca = LoteFixtures.anyGeocerca();
            Lote lote = new Lote(LoteFixtures.ID, LoteFixtures.NOMBRE, geocerca,
                    LoteFixtures.FINCA_ID, LoteFixtures.TIPO_CULTIVO_ID);

            // And: Un punto que sabemos que está dentro (centro del polígono)
            Point puntoInterior = SpotFixtures.point(-73.05, 4.05);

            // When: Intentamos crear un spot
            Spot spot = lote.crearSpot(puntoInterior, new SpotPosition(1, 1));

            // Then: El spot se crea correctamente
            assertNull(spot.getId());
            assertEquals(puntoInterior, spot.getCoordenada());
            assertEquals(LoteFixtures.ID, spot.getLoteId());
            assertEquals(1, spot.getLinea());
            assertEquals(1, spot.getPosicion());
        }

        @Test
        @DisplayName("lanza PointOutsideLoteException cuando el punto está fuera")
        void shouldThrowExceptionWhenPointIsOutsideGeofence() {
            // Given: Un lote con geocerca
            Lote lote = LoteFixtures.anyLote();

            // And: Un punto claramente fuera del polígono
            Point puntoExterior = SpotFixtures.point(-75.0, 6.0); // Muy lejos

            // When + Then: Debe lanzar excepción
            SpotPosition position = new SpotPosition(1, 1);
            assertThrows(PointOutsideLoteException.class,
                    () -> lote.crearSpot(puntoExterior, position)
            );
        }

        @Test
        @DisplayName("lanza PointOutsideLoteException cuando el punto está en el borde")
        void shouldThrowExceptionWhenPointIsOnGeofenceBorder() {
            // Given: Un lote con geocerca
            Lote lote = LoteFixtures.anyLote();

            // And: Un punto exactamente en el borde (primera coordenada del polígono)
            Point puntoEnBorde = SpotFixtures.point(-73.0, 4.0);

            // When + Then: Debe lanzar excepción (contains() de JTS excluye el borde)
            SpotPosition position = new SpotPosition(1, 1);
            assertThrows(PointOutsideLoteException.class,
                    () -> lote.crearSpot(puntoEnBorde, position)
            );
        }
    }

    @Nested
    @DisplayName("marcarComoEnAgp()")
    class MarcarComoEnAgp {

        @Test
        @DisplayName("cambia el estado onAgp a true")
        void shouldChangeOnAgpStateToTrue() {
            // Given: Un lote recién creado (onAgp = false por defecto)
            Lote lote = LoteFixtures.anyLote();
            assertFalse(lote.isOnAgp());

            // When
            lote.marcarComoEnAgp();

            // Then
            assertTrue(lote.isOnAgp());
        }

        @Test
        @DisplayName("puede llamarse múltiples veces sin error")
        void shouldNotFailWhenCalledMultipleTimes() {
            // Given
            Lote lote = LoteFixtures.anyLote();

            // When
            lote.marcarComoEnAgp();
            lote.marcarComoEnAgp(); // Segunda llamada

            // Then: No lanza excepción y el estado permanece true
            assertTrue(lote.isOnAgp());
        }
    }

    @Nested
    @DisplayName("renombrar() / withNombre()")
    class Renombrar {

        @Test
        @DisplayName("withNombre() retorna nueva instancia con nombre actualizado")
        void withNombreRetornaNuevaInstanciaConNombreActualizado() {
            // GIVEN
            Lote loteOriginal = LoteFixtures.anyLote();
            String nuevoNombre = "Nuevo Nombre";

            // WHEN
            Lote loteRenombrado = loteOriginal.withNombre(nuevoNombre);

            // THEN: Nueva instancia con nombre actualizado
            assertNotSame(loteOriginal, loteRenombrado, "Debe retornar nueva instancia (inmutabilidad)");
            assertEquals(nuevoNombre, loteRenombrado.getNombre(), "El nuevo nombre debe ser el esperado");

            // AND: Los demás campos se conservan
            assertEquals(loteOriginal.getId(), loteRenombrado.getId());
            assertEquals(loteOriginal.getGeocerca(), loteRenombrado.getGeocerca());
            assertEquals(loteOriginal.getFincaId(), loteRenombrado.getFincaId());
            assertEquals(loteOriginal.getTipoCultivoId(), loteRenombrado.getTipoCultivoId());
        }

        @Test
        @DisplayName("withNombre() lanza excepción si el nombre es null")
        void withNombreLanzaExcepcionSiNombreEsNull() {
            // GIVEN
            Lote lote = LoteFixtures.anyLote();

            // WHEN + THEN
            assertThrows(
                    IllegalArgumentException.class,
                    () -> lote.withNombre(null),
                    "Debe lanzar excepción cuando el nombre es null"
            );
        }

        @Test
        @DisplayName("withNombre() lanza excepción si el nombre está vacío")
        void withNombreLanzaExcepcionSiNombreEstaVacio() {
            // GIVEN
            Lote lote = LoteFixtures.anyLote();

            // WHEN + THEN
            assertThrows(
                    IllegalArgumentException.class,
                    () -> lote.withNombre(""),
                    "Debe lanzar excepción cuando el nombre está vacío"
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> lote.withNombre("   "),
                    "Debe lanzar excepción cuando el nombre tiene solo espacios"
            );
        }

        @Test
        @DisplayName("renombrar() delega correctamente a withNombre()")
        void renombrarDelegaAWithNombre() {
            // GIVEN
            Lote lote = LoteFixtures.anyLote();
            String nuevoNombre = "Nombre vía alias";

            // WHEN
            Lote resultadoViaAlias = lote.renombrar(nuevoNombre);
            Lote resultadoDirecto = lote.withNombre(nuevoNombre);

            // THEN: Ambos deben producir el mismo resultado
            assertEquals(resultadoViaAlias.getNombre(), resultadoDirecto.getNombre());
            assertEquals(resultadoViaAlias.getId(), resultadoDirecto.getId());
            // Nota: Son instancias diferentes (inmutabilidad), pero con mismo estado
        }
    }


}