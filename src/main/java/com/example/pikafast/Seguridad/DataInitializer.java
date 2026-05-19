package com.example.pikafast.Seguridad;


import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/* Esta Clase configura un Usuario Tipo ADMIN al Iniciar el Proyecto */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private UsuarioRepositorio usuarioDAO;
    private BCryptPasswordEncoder passwordEncoder;
    
    // Insercion de Dependencias
    public DataInitializer(UsuarioRepositorio usuarioDAO, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }
    

    @Override
    public void run(String... args) throws Exception {
        if(usuarioDAO.countByRol("ADMIN") == 0){
            Usuario admin = new Usuario();
            
            admin.setNombre("TestUser");
            admin.setEmail("admin@pikafast.com");
            admin.setContrasenia(passwordEncoder.encode("admin123"));
            admin.setTelefono("123 456 789");
            admin.setRol("ADMIN");
            admin.setEstado(true);
            
            usuarioDAO.save(admin);
            System.out.println("Usuario ADMIN creado");
        }
    }
    
}
