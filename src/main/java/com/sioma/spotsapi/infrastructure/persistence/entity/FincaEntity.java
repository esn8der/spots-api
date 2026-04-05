package com.sioma.spotsapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "finca")
public class FincaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    public FincaEntity(String nombre, Long usuarioId) {
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }
}

