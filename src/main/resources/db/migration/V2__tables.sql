CREATE TABLE usuario
(
    id BIGSERIAL PRIMARY KEY,
    nombre        TEXT NOT NULL,
    email         TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE finca
(
    id BIGSERIAL PRIMARY KEY,
    nombre     TEXT   NOT NULL,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE planta
(
    id     SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE lote
(
    id BIGSERIAL PRIMARY KEY,
    finca_id     BIGINT NOT NULL,
    nombre       TEXT   NOT NULL,
    tipo_cultivo INT,
    geocerca GEOGRAPHY(POLYGON, 4326),
    on_agp       BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE spot
(
    id BIGSERIAL PRIMARY KEY,
    lote_id  BIGINT  NOT NULL,
    geo GEOGRAPHY(POINT, 4326) NOT NULL,
    posicion INTEGER NOT NULL,
    linea    INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);