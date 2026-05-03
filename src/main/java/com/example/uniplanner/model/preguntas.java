package com.example.uniplanner.model;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "preguntas", schema = "seguridad")
public class preguntas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Integer id_pregunta;

    @Column(name = "id_usuario")
    private Integer id_usuario;

    @Column(name = "pregunta1")
    private String pregunta1;

    @Column(name = "pregunta2")
    private String pregunta2;

    @Column(name = "pregunta3")
    private String pregunta3;
}
