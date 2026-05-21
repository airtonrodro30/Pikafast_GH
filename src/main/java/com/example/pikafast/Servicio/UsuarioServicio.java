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
        return usuarioDAO.findAll();
    }

    // Guardar Un Registro
    public Usuario save(Usuario usuario) {
        return usuarioDAO.save(usuario);
    }

    // Obtener un Registro por id
    public Usuario get(Integer id) {
        return usuarioDAO.findById(id).orElse(null);
    }

    // Obtener un Registro por email
    public Usuario findByEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }

    // Eliminar un Registro por id
    public void delete(Integer id) {
        usuarioDAO.deleteById(id);
    }
}
