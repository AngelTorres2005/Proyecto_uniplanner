package com.example.uniplanner.repository;

import com.example.uniplanner.model.preguntas;
import com.example.uniplanner.model.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface preguntasRepository extends JpaRepository<preguntas, Integer> {
    @Query("SELECT p FROM preguntas p WHERE p.id_usuario = :id_usuario")
    Optional<preguntas> findById_usuario(@Param("id_usuario") int id_usuario);
}

