package com.example.uniplanner.repository;
import com.example.uniplanner.model.prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface prioridadRepository extends JpaRepository<prioridad, Integer> {
    Optional<prioridad> findByNombre(String nombre);
}
