package com.sioma.spotsapi.web.dto;

import java.util.List;

public record GeoJsonPolygon(
        String type,
        List<List<List<Double>>> coordinates
) {}