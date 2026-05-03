package com.example.uniplanner.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios", schema = "seguridad")
public class usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int id_usuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "correo")
    private String correo;

    @Column(name = "password_hash")
    private String password_hash;

    @Column(name = "id_rol")
    private int id_rol;

    @Column(name = "fecha_alta")
    private java.time.LocalDate fechaAlta;

    @Column(name = "fecha_modificacion")
    private java.time.LocalDate fecha_modificacion;

    @Column(name = "estado")
    private String estado;

}