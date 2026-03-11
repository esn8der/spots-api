CREATE INDEX idx_lote_finca ON lote (finca_id);
CREATE INDEX idx_spot_lote ON spot (lote_id);

CREATE INDEX idx_spot_geo ON spot USING GIST (geo);
CREATE INDEX idx_lote_geocerca_gist ON lote USING GIST (geocerca);

CREATE INDEX idx_spot_lote_linea ON spot (lote_id, linea);