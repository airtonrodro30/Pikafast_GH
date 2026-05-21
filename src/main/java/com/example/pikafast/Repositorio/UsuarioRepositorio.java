package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer>{
    
    Usuario findByEmail(String email); //SELECT * FROM usuario WHERE email = ?
    
    Usuario findByNombre(String Nombre); //SELECT * FROM usuario WHERE nombre = ? 
    
    long countByRol(Rol rol); //SELECT COUNT(*) FROM usuario WHERE rol = "rol"
}
