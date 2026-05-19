package com.example.pikafast.repository;

import com.example.pikafast.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

        Usuario findByCorreoAndPassword(
        String correo,
        String password
);
}

