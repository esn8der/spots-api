-- ==========================================================
-- 1) Extensiones necesarias
-- ==========================================================
CREATE EXTENSION IF NOT EXISTS postgis;

-- ==========================================================
-- 2) Tablas base
-- ==========================================================
-- ===USUARIO===
CREATE TABLE IF NOT EXISTS usuario
(
    id BIGSERIAL PRIMARY KEY,
    nombre        TEXT        NOT NULL,
    email         TEXT UNIQUE NOT NULL,
    password_hash TEXT        NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ===FINCA===
CREATE TABLE IF NOT EXISTS finca
(
    id BIGSERIAL PRIMARY KEY,
    nombre     TEXT   NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Nombre único de finca por usuario
    CONSTRAINT uq_finca_usuario_nombre UNIQUE (usuario_id, nombre)
);

-- ===PLANTA===
CREATE TABLE IF NOT EXISTS planta
(
    id     SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ===LOTES===
CREATE TABLE IF NOT EXISTS lote
(
    id BIGSERIAL PRIMARY KEY,
    finca_id     BIGINT NOT NULL REFERENCES finca (id) ON DELETE CASCADE,
    nombre       TEXT   NOT NULL,
    tipo_cultivo INT    REFERENCES planta (id) ON DELETE SET NULL,
    geocerca GEOGRAPHY (POLYGON, 4326),
    on_agp       BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_lote_finca_nombre UNIQUE (finca_id, nombre)
);

-- ===SPOTS=== (plantas/posiciones en el lote)
CREATE TABLE IF NOT EXISTS spot
(
    id BIGSERIAL PRIMARY KEY,
    lote_id  BIGINT  NOT NULL REFERENCES lote (id) ON DELETE CASCADE,
    geo GEOGRAPHY (POINT, 4326) NOT NULL,
    posicion INTEGER NOT NULL,
    linea    INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Un spot por (lote, linea, posicion)
    CONSTRAINT uq_spot_lote_linea_posicion UNIQUE (lote_id, linea, posicion),
    -- Evitar duplicar puntos exactos en el mismo lote
    -- CONSTRAINT uq_spot_lote_geo UNIQUE (lote_id, geo), --Eliminado por problemas con geometrías con exactitud binaria.
    -- Validaciones básicas de dominio
    CONSTRAINT ck_spot_posicion_positive CHECK (posicion > 0),
    CONSTRAINT ck_spot_linea_positive CHECK (linea > 0)
);