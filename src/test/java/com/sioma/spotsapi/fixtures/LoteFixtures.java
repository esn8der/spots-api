package com.sioma.spotsapi.fixtures;

import com.sioma.spotsapi.domain.model.Lote;
import org.locationtech.jts.geom.*;

import java.util.UUID;

public class LoteFixtures {
    public static final Long ID = 1L;
    public static final String NOMBRE = "Lote 1";
    public static final Long FINCA_ID = 1L;
    public static final Long TIPO_CULTIVO_ID = 1L;

    public static Polygon anyGeocerca() {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        Coordinate[] coords = new Coordinate[]{
                new Coordinate(-73.0, 4.0),
                new Coordinate(-73.0, 4.1),
                new Coordinate(-73.1, 4.1),
                new Coordinate(-73.1, 4.0),
                new Coordinate(-73.0, 4.0) // cerrado
        };

        LinearRing shell = factory.createLinearRing(coords);
        return factory.createPolygon(shell);
    }

    public static Lote loteContainingPoint(Point point) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        double x = point.getX();
        double y = point.getY();

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(x - 0.001, y - 0.001),
                new Coordinate(x + 0.001, y - 0.001),
                new Coordinate(x + 0.001, y + 0.001),
                new Coordinate(x - 0.001, y + 0.001),
                new Coordinate(x - 0.001, y - 0.001) // cerrar polígono
        };

        LinearRing shell = factory.createLinearRing(coordinates);
        Polygon polygon = factory.createPolygon(shell);

        return new Lote(
                1L,
                "Lote dentro",
                polygon,
                1L,
                1L
        );
    }

    public static Lote loteNOTContainingPoint(Point point) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        double x = point.getX();
        double y = point.getY();

        // Creamos un polígono lejos del punto (lo desplazamos)
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(x + 1, y + 1),
                new Coordinate(x + 2, y + 1),
                new Coordinate(x + 2, y + 2),
                new Coordinate(x + 1, y + 2),
                new Coordinate(x + 1, y + 1) // cerrar
        };

        LinearRing shell = factory.createLinearRing(coordinates);
        Polygon polygon = factory.createPolygon(shell);

        return new Lote(
                1L,
                "Lote fuera",
                polygon,
                1L,
                1L
        );
    }

    public static String uniqueName() {
        return NOMBRE + "-" + UUID.randomUUID().toString().substring(0, 6);
    }

    public static Lote anyLote() {
        return new Lote(
                ID,
                NOMBRE,
                anyGeocerca(),
                FINCA_ID,
                TIPO_CULTIVO_ID
        );
    }
}
