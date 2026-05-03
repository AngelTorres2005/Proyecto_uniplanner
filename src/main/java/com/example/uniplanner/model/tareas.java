package com.example.uniplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "tareas",schema = "operaciones")
public class tareas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer id_tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private materias materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private prioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private usuarios usuario;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private java.time.LocalDate fecha_creacion;

    @Column(name = "fecha_entrega")
    private java.time.LocalDate fecha_entrega;

    @Column(name = "estatus")
    private String estatus;

    @Column(name = "fecha_entregada")
    private java.time.LocalDate fecha_entregada;



}
