-- ==========================================================
-- Índices
-- ==========================================================
-- Búsquedas por relaciones
CREATE INDEX IF NOT EXISTS idx_lote_finca ON lote (finca_id);
CREATE INDEX IF NOT EXISTS idx_spot_lote ON spot (lote_id);

-- Índices espaciales
CREATE INDEX IF NOT EXISTS idx_spot_geo ON spot USING GIST (geo);
CREATE INDEX IF NOT EXISTS idx_lote_geocerca_gist ON lote USING GIST (geocerca);
CREATE INDEX IF NOT EXISTS idx_spot_lote_linea ON spot(lote_id, linea);