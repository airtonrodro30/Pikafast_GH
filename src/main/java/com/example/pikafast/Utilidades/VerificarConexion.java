package com.example.pikafast.Utilidades;


import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.UsuarioRepositorio;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* @author usuario */
@Service
public class VerificarConexion {

    @Autowired
    UsuarioRepositorio usuarioDAO;

    @PostConstruct
    public void verificar() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            System.out.println("\nMostrando Datos de Prueba Tabla usuario");
            System.out.println("================================================");
            usuarios.forEach(u
                    -> System.out.println(
                            "ID: " + u.getIdUsuario()
                            + " Nombre: " + u.getNombre()
                            + " Password: " + u.getContrasenia())
            );

            System.out.println("\nConexion a la BASE DE DATOS ESTABLECIDA\n");
        } catch (Exception e) {
            System.err.println("ERROR al verificar conexión:");
            e.printStackTrace();
        }
    }
}
