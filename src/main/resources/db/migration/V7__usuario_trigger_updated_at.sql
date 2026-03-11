CREATE TRIGGER usuario_set_updated_at
    BEFORE UPDATE ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION trg_set_updated_at();