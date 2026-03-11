package com.sioma.spotsapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "finca")
public class FincaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "usuario_id")
    private Long usuarioId;

    public FincaEntity() {}

    public FincaEntity(String nombre, Long usuarioId) {
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }
}
