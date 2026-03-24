package com.sioma.spotsapi.web.dto;

import java.util.List;

public record GeoJsonPoint(
    String type,
    List<Double> coordinates
) {}