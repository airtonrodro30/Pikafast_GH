package com.example.pikafast.Servicio;


import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.UsuarioRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRepositorio usuarioDAO;

    // Mostrar Todos los Registros
    public List<Usuario> getList() {
        return usuarioDAO.getByEstadoTrue();
    }
    
    
    // Guardar Un Registro
    public Usuario save(Usuario usuario) {
        return usuarioDAO.save(usuario);
    }

    // Obtener un Registro por id
    public Usuario get(Integer id) {
        return usuarioDAO.findById(id).orElse(null);
    }

    // Eliminar un Registro por id (Borrado Logico)
    public void delete(Integer id) {
        usuarioDAO.deleteById(id);
        Usuario usuario = usuarioDAO.findById(id).orElse(null);
        
        if(usuario != null){
            usuario.setEstado(false);
            usuarioDAO.save(usuario);
        }
    }
}
