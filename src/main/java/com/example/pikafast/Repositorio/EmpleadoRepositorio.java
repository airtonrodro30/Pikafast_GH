package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepositorio extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByUsuarioEmail(String email);
}
