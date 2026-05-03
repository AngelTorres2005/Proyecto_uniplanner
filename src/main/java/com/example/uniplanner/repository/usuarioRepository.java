package com.example.uniplanner.repository;

import com.example.uniplanner.model.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface usuarioRepository extends JpaRepository<usuarios, Integer> {
    Optional<usuarios> findByCorreo(String correo);
}
