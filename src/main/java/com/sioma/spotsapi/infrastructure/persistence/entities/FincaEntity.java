package com.sioma.spotsapi.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "finca")
public class FincaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    public FincaEntity() {}

    public FincaEntity(String nombre, Long usuarioId) {
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }
}
