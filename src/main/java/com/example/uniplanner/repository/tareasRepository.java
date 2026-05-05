package com.example.uniplanner.repository;

import com.example.uniplanner.model.tareas;
import com.example.uniplanner.model.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface tareasRepository extends JpaRepository<tareas, Integer> {
    @Query("SELECT t FROM tareas t WHERE t.usuario.id_usuario = :id")
    List<tareas> buscarPorUsuario(@Param("id") Integer id);

    @Query("SELECT t FROM tareas t WHERE t.usuario.id_usuario = :id AND t.estatus IS NULL")
    List<tareas> buscarTareasPendientes(@Param("id") Integer id);

    @Query(value = "SELECT * FROM operaciones.tareas t " + " " +
            " WHERE t.id_usuario = :id " +
            " AND t.estatus IS NOT NULL " +
            " AND t.fecha_entregada IS NOT NULL " +
            " ORDER BY t.fecha_entregada DESC LIMIT 2",
            nativeQuery = true)
    List<tareas> buscarUltimas2Tareas(@Param("id") Integer id);

    @Query(value = "SELECT * FROM operaciones.tareas t " +
            "WHERE t.id_usuario = :id " +
            "AND t.fecha_entrega IS NOT NULL " +
            "AND t.fecha_entregada IS NULL " +
            "ORDER BY t.fecha_entrega ASC LIMIT 3",
            nativeQuery = true)
    List<tareas> buscar3TareasAVencer(@Param("id") Integer id);

    @Query("SELECT t FROM tareas t WHERE t.fecha_entrega = :fecha AND t.estatus IS NULL")
    List<tareas> buscarTareasParaRecordatorio(@Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM tareas t WHERE t.fecha_entrega = :fecha AND t.usuario.id_usuario = :id AND t.estatus IS NULL")
    List<tareas> buscarTareasParaRecordatorioID(@Param("fecha") LocalDate fecha, @Param("id") Integer id);
}
