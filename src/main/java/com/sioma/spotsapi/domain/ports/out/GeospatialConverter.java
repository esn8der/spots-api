package com.sioma.spotsapi.domain.ports.out;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.util.List;

public interface GeospatialConverter {
    Polygon toPolygon(List<List<Double>> coordinates);
    Point toPoint(List<Double> coordinates);
}