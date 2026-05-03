package com.example.uniplanner.repository;

import com.example.uniplanner.model.materias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface materiasRepository extends JpaRepository<materias, Integer> {
    @Query("SELECT m FROM materias m WHERE m.usuario.id_usuario = :id")
    List<materias> findByUsuario(@Param("id") Integer id);
}
