-- ==========================================================
-- Triggers de "updated_at"
-- ==========================================================
CREATE OR REPLACE FUNCTION trg_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Para todas las tablas con updated_at
DROP TRIGGER IF EXISTS usuario_set_updated_at ON usuario;
CREATE TRIGGER usuario_set_updated_at
    BEFORE UPDATE ON usuario
    FOR EACH row EXECUTE FUNCTION trg_set_updated_at();

DROP TRIGGER IF EXISTS finca_set_updated_at ON finca;
CREATE TRIGGER finca_set_updated_at
    BEFORE UPDATE ON finca
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

DROP TRIGGER IF EXISTS planta_set_updated_at ON planta;
CREATE TRIGGER planta_set_updated_at
    BEFORE UPDATE ON planta
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

DROP TRIGGER IF EXISTS lote_set_updated_at ON lote;
CREATE TRIGGER lote_set_updated_at
    BEFORE UPDATE ON lote
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

DROP TRIGGER IF EXISTS spot_set_updated_at ON spot;
CREATE TRIGGER spot_set_updated_at
    BEFORE UPDATE ON spot
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

-- ==========================================================
-- Trigger en spot (INSERT/UPDATE)
-- ==========================================================
-- Función de validación
CREATE OR REPLACE FUNCTION trg_spot_within_lote_geofence()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
    	SELECT 1
    	FROM lote l
    	WHERE l.id = NEW.lote_id
        AND l.geocerca IS NOT NULL
		AND ST_Covers(l.geocerca, NEW.geo)
	)
THEN
	RAISE EXCEPTION
		'El spot no está dentro de la geocerca del lote % o el lote no tiene geocerca', NEW.lote_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers en spot
DROP TRIGGER IF EXISTS spot_check_geofence_ins ON spot;
CREATE TRIGGER spot_check_geofence_ins
    BEFORE INSERT ON spot
    FOR EACH ROW EXECUTE FUNCTION trg_spot_within_lote_geofence();

DROP TRIGGER IF EXISTS spot_check_geofence_upd ON spot;
CREATE TRIGGER spot_check_geofence_upd
    BEFORE UPDATE OF geo, lote_id ON spot
FOR EACH ROW EXECUTE FUNCTION trg_spot_within_lote_geofence();

-- ==========================================================
-- Trigger en lote cuando cambia la geocerca
-- ==========================================================
CREATE OR REPLACE FUNCTION trg_lote_geofence_update_validate()
RETURNS TRIGGER AS $$
DECLARE
  v_count INT;
BEGIN
    IF TG_OP = 'UPDATE' AND NEW.geocerca IS DISTINCT FROM OLD.geocerca THEN
SELECT COUNT(*) INTO v_count
FROM spot s
WHERE s.lote_id = NEW.id
  AND NOT ST_Covers(NEW.geocerca, s.geo);

IF v_count > 0 THEN
      RAISE EXCEPTION 'No se puede actualizar geocerca: % spot(s) quedarían fuera', v_count;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS lote_geofence_update_validate ON lote;
CREATE TRIGGER lote_geofence_update_validate
    BEFORE UPDATE OF geocerca ON lote
FOR EACH ROW EXECUTE FUNCTION trg_lote_geofence_update_validate();