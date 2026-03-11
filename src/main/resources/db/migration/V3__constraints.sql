ALTER TABLE usuario
    ADD CONSTRAINT uq_usuario_email UNIQUE (email);

ALTER TABLE finca
    ADD CONSTRAINT fk_finca_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE;

ALTER TABLE finca
    ADD CONSTRAINT uq_finca_usuario_nombre UNIQUE (usuario_id, nombre);

ALTER TABLE lote
    ADD CONSTRAINT fk_lote_finca
        FOREIGN KEY (finca_id) REFERENCES finca (id) ON DELETE CASCADE;

ALTER TABLE lote
    ADD CONSTRAINT fk_lote_planta
        FOREIGN KEY (tipo_cultivo) REFERENCES planta (id) ON DELETE SET NULL;

ALTER TABLE lote
    ADD CONSTRAINT uq_lote_finca_nombre
        UNIQUE (finca_id, nombre);

ALTER TABLE spot
    ADD CONSTRAINT fk_spot_lote
        FOREIGN KEY (lote_id) REFERENCES lote (id) ON DELETE CASCADE;

ALTER TABLE spot
    ADD CONSTRAINT uq_spot_lote_linea_posicion
        UNIQUE (lote_id, linea, posicion);

ALTER TABLE spot
    ADD CONSTRAINT ck_spot_posicion_positive CHECK (posicion > 0);

ALTER TABLE spot
    ADD CONSTRAINT ck_spot_linea_positive CHECK (linea > 0);