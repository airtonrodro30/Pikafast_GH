package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.UsuarioRepositorio;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImp implements UserDetailsService {
    
    private final UsuarioRepositorio usuarioDAO;
    
    public UserDetailsServiceImp(UsuarioRepositorio usuarioDAO){
        this.usuarioDAO = usuarioDAO;
    }
    

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.findByEmail(email); // SQL Busca por email
        
        if (usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
        if (!usuario.getActivo()){
            throw new UsernameNotFoundException("Usuario está inactivo");
        }
        
        return User.builder()
                .username(usuario.getEmail()) // Utiliza correo como username
                .password(usuario.getContrasenia())
                .roles(usuario.getRol().name()) // ADMIN
                .build();
    }
    
}
