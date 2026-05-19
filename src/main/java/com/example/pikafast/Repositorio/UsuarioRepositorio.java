package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer>{
    
    List<Usuario> getByEstadoTrue(); //SELECT * FROM usuario WHERE estado = 1
    
    Usuario findByEmail(String email); //SELECT * FROM usuario WHERE email = ?
    
    Usuario findByNombre(String Nombre); //SELECT * FROM usuario WHERE nombre = ? 
    
    long countByRol(String rol); //SELECT COUNT(*) FROM usuario WHERE rol = "rol"
}
