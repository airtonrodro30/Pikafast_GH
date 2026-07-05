package com.example.pikafast.Seguridad;


import com.example.pikafast.Entidad.Empleado;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Enums.Cargo;
import com.example.pikafast.Enums.Rol;
import com.example.pikafast.Repositorio.EmpleadoRepositorio;
import com.example.pikafast.Repositorio.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/* Esta Clase configura un Usuario Tipo ADMIN al Iniciar el Proyecto */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private UsuarioRepositorio usuarioDAO;
    private BCryptPasswordEncoder passwordEncoder;
    private EmpleadoRepositorio empleadoDAO;
    
    // Insercion de Dependencias
    public DataInitializer( UsuarioRepositorio usuarioDAO, 
                            BCryptPasswordEncoder passwordEncoder,
                            EmpleadoRepositorio empleadoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
        this.empleadoDAO = empleadoDAO;
    }
    
    

    @Override
    public void run(String... args) throws Exception {
        if(usuarioDAO.countByRol(Rol.ADMIN) == 0){
            Usuario admin = new Usuario();
            Empleado empleado = new Empleado();
            // Usuario de tipo ADMIN
            admin.setEmail("admin@pikafast.com");
            admin.setContrasenia(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            
            // Creación del empleado (datos)
            empleado.setNombres("TestUser");
            empleado.setApellidos("Bot");
            empleado.setTelefono("123 456 789");
            empleado.setCargo(Cargo.ADMINISTRADOR);
            empleado.setUsuario(admin);
          
            usuarioDAO.save(admin);
            empleadoDAO.save(empleado);
            
            System.out.println("Usuario ADMIN creado");
        }
    }
    
}
