package com.sioma.spotsapi.fixtures;

import com.sioma.spotsapi.domain.model.Lote;
import org.locationtech.jts.geom.*;

public class LoteFixtures {
    public static final String NOMBRE = "Lote 1";
    public static final Long FINCA_ID = 1L;
    public static final Long TIPO_CULTIVO_ID = 1L;

    public static Polygon anyGeocerca() {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        Coordinate[] coords = new Coordinate[] {
                new Coordinate(-73.0, 4.0),
                new Coordinate(-73.0, 4.1),
                new Coordinate(-73.1, 4.1),
                new Coordinate(-73.1, 4.0),
                new Coordinate(-73.0, 4.0) // cerrado
        };

        LinearRing shell = factory.createLinearRing(coords);
        return factory.createPolygon(shell);
    }

    public static Lote anyLote() {
        return new Lote(
                NOMBRE,
                anyGeocerca(),
                FINCA_ID,
                TIPO_CULTIVO_ID
        );
    }
}
