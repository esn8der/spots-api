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
    private Long idUsuario;

    public FincaEntity() {}

    public FincaEntity(String nombre, Long idUsuario) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }
}
