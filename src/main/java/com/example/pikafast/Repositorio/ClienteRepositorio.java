package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByUsuarioEmail(String email);
}
