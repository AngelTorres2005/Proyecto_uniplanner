package com.example.uniplanner.model;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "prioridad",schema = "catalogo")
public class prioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Integer id_prioridad;

    @Column(name = "nombre")
    private String nombre;
}
