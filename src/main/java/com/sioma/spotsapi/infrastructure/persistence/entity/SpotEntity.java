package com.sioma.spotsapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Table(name = "spot")
public class SpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "geo", columnDefinition = "geography(Point,4326)")
    private Point coordenada;

    @Column(name = "lote_id")
    private Long loteId;

    private int linea;
    private int posicion;

    public SpotEntity() {}

    public SpotEntity(Point coordenada, Long loteId, int linea, int posicion) {
        this.coordenada = coordenada;
        this.loteId = loteId;
        this.linea = linea;
        this.posicion = posicion;
    }
}
