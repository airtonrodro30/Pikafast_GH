package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Servicio.UsuarioServicio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/dashboard/usuarios") //ruta base
public class UsuarioControlador {
    
    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioControlador(UsuarioServicio usuarioServicio,
            PasswordEncoder passwordEncoder) {
        this.usuarioServicio = usuarioServicio;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping
    public String mostrarUsuarios(Model model) {
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("contentPage", "admin/usuarios");

        model.addAttribute("usuarios", usuarioServicio.getList());
        model.addAttribute("usuario", new Usuario());

        return "dashboard";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            Usuario usuarioExistente = usuarioServicio.get(usuario.getIdUsuario());

            if (usuario.getContrasenia() == null || usuario.getContrasenia().isBlank()) {
                usuario.setContrasenia(usuarioExistente.getContrasenia());
            } else {
                usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
            }
        } else {
            usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        }

        usuarioServicio.save(usuario);
        return "redirect:/dashboard/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioServicio.get(id);

        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("contentPage", "admin/usuarios");
        model.addAttribute("usuarios", usuarioServicio.getList());
        model.addAttribute("usuario", usuario);
        model.addAttribute("abrirModal", true);

        return "dashboard";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioServicio.delete(id);
        return "redirect:/dashboard/usuarios";
    }
}
