package com.sioma.spotsapi.infrastructure.persistence.entity;

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

    private String nombre;

    @Column(name = "finca_id")
    private Long fincaId;

    @Column(name = "tipo_cultivo")
    private Long tipoCultivoId;

    @Column(columnDefinition = "geography(Polygon,4326)")
    private Polygon geocerca;

    @Column(name = "on_agp")
    private final boolean onAgp = false;

    public LoteEntity() {}

    public LoteEntity(String nombre, Long fincaId, Long tipoCultivoId) {
        this.nombre = nombre;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
    }
}
