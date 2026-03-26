package com.sioma.spotsapi.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "planta")
public class PlantaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    public PlantaEntity() {}

    public PlantaEntity(String nombre) {
        this.nombre = nombre;
    }
}