package com.sioma.spotsapi.infrastructure.geospatial;

import com.sioma.spotsapi.domain.exception.InvalidGeocercaException;
import com.sioma.spotsapi.web.dto.GeoJsonPolygon;
import org.locationtech.jts.geom.*;

import java.util.List;

public class GeometryFactoryProvider {

    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    private GeometryFactoryProvider() {}

    public static Polygon fromGeoJson(GeoJsonPolygon geoJson) {

        List<List<Double>> coords = geoJson.coordinates().getFirst();

        if (coords == null || coords.size() < 4) {
            throw new InvalidGeocercaException(
                    "Un polígono debe tener al menos 4 puntos"
            );
        }

        if (!coords.getFirst().equals(coords.getLast())) {
            throw new InvalidGeocercaException(
                    "El polígono debe estar cerrado (primer punto igual al último)"
            );
        }

        Coordinate[] coordinates = coords.stream()
                .map(c -> new Coordinate(c.getFirst(), c.get(1)))
                .toArray(Coordinate[]::new);

        try {
            LinearRing shell = geometryFactory.createLinearRing(coordinates);
            Polygon polygon = geometryFactory.createPolygon(shell);

            // Validar geometría
            if (!polygon.isValid()) {
                throw new InvalidGeocercaException(
                        "La geocerca no es válida (puede estar autointersectada)"
                );
            }

            return polygon;

        } catch (IllegalArgumentException _) {
            throw new InvalidGeocercaException("Formato de geocerca inválido");
        }
    }
}