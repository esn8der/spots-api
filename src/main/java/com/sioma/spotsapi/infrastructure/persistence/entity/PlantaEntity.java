package com.sioma.spotsapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "planta")
public class PlantaEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String nombre;

    public PlantaEntity() {}

    public PlantaEntity(String nombre) {
        this.nombre = nombre;
    }
}