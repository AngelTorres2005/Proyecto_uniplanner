package com.example.uniplanner.repository;

import com.example.uniplanner.model.horarios;
import com.example.uniplanner.model.materias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface horariosRepository extends JpaRepository<horarios, Integer> {
    List<horarios> findByMateria(materias materia);

    @Query("SELECT COUNT(h) FROM horarios h " +
            "WHERE h.materia.usuario.id_usuario = :idUsuario " +
            "AND h.dia = :dia " +
            "AND (:inicio < h.hora_final AND :fin > h.hora_inicio)")
    long countChoques(@Param("idUsuario") Integer idUsuario,
                      @Param("dia") String dia,
                      @Param("inicio") LocalTime inicio,
                      @Param("fin") LocalTime fin);

    @Query("SELECT h FROM horarios h WHERE h.materia.usuario.id_usuario = :id")
    List<horarios> findByUsuario(@Param("id") Integer id);
}
