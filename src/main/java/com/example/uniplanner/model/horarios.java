package com.example.uniplanner.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Data
@Table(name = "horarios", schema = "academico")
public class horarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer id_horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private materias materia;

    @Column(name = "dia")
    private String dia;

    @Column(name = "hora_inicio")
    private LocalTime hora_inicio;

    @Column(name = "hora_final")
    private LocalTime hora_final;
}
