package com.sioma.spotsapi.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.locationtech.jts.geom.Polygon;


@Entity
@Getter
@Table(name = "lote")
public class LoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "finca_id", nullable = false)
    private Long fincaId;

    @Column(name = "tipo_cultivo", nullable = false)
    private Long tipoCultivoId;

    @Column(columnDefinition = "geography(Polygon,4326)")
    private Polygon geocerca;

    @Column(name = "on_agp")
    private boolean onAgp;

    public LoteEntity() {}

    public LoteEntity(String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {
        this.nombre = nombre;
        this.geocerca = geocerca;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }
}
