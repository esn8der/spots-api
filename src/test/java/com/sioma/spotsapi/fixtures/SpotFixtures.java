package com.sioma.spotsapi.fixtures;

import org.locationtech.jts.geom.*;
import java.util.List;

public class SpotFixtures {
    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    public static final Long LOTE_ID = 1L;
    public static final int LINEA = 1;
    public static final int POSICION = 1;

    public static Point point(double lon, double lat) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }

    public static Point validPoint() {
        return geometryFactory.createPoint(new Coordinate(-73.647243, 3.896533));
    }

    public static List<Double> validCoordinates() {
        return List.of(-73.647243, 3.896533);
    }
}