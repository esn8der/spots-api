CREATE UNIQUE INDEX idx_spot_lote_geo_unique
    ON spot (
             lote_id,
             ROUND(ST_X(geo::geometry)::numeric, 6),
             ROUND(ST_Y(geo::geometry)::numeric, 6)
        );