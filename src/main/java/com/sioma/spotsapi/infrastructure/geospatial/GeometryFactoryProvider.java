package com.sioma.spotsapi.infrastructure.geospatial;

import com.sioma.spotsapi.domain.exception.InvalidGeoSpatialException;
import com.sioma.spotsapi.domain.ports.out.GeospatialConverter;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeometryFactoryProvider implements GeospatialConverter {

    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public Polygon toPolygon(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.size() < 4)
            throw new InvalidGeoSpatialException("Un polígono debe tener al menos 4 puntos");

        if (!coordinates.getFirst().equals(coordinates.getLast()))
            throw new InvalidGeoSpatialException("El polígono debe estar cerrado");

        Coordinate[] coords = coordinates.stream()
                .map(c -> new Coordinate(c.getFirst(), c.get(1)))
                .toArray(Coordinate[]::new);

        try {
            LinearRing shell = geometryFactory.createLinearRing(coords);
            Polygon polygon = geometryFactory.createPolygon(shell);
            if (!polygon.isValid())
                throw new InvalidGeoSpatialException("La geocerca no es válida");
            return polygon;
        } catch (IllegalArgumentException _) {
            throw new InvalidGeoSpatialException("Formato de geocerca inválido");
        }
    }

    @Override
    public Point toPoint(List<Double> coordinates) {
        if (coordinates.size() != 2)
            throw new InvalidGeoSpatialException("Un punto debe tener 2 coordenadas");
        return geometryFactory.createPoint(
                new Coordinate(coordinates.getFirst(), coordinates.get(1))
        );
    }
}