package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Empleado;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.EmpleadoRepositorio;
import com.example.pikafast.Servicio.UsuarioServicio;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final EmpleadoRepositorio empleadoRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioControlador(UsuarioServicio usuarioServicio,
            EmpleadoRepositorio empleadoRepositorio,
            PasswordEncoder passwordEncoder) {
        this.usuarioServicio = usuarioServicio;
        this.empleadoRepositorio = empleadoRepositorio;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping
    public String mostrarUsuarios(Model model) {
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("contentPage", "admin/usuarios");

        model.addAttribute("usuarios", usuarioServicio.getList());
        model.addAttribute("empleadosPorUsuarioId", obtenerEmpleadosPorUsuarioId());
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
        model.addAttribute("empleadosPorUsuarioId", obtenerEmpleadosPorUsuarioId());
        model.addAttribute("usuario", usuario);
        model.addAttribute("abrirModal", true);

        return "dashboard";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioServicio.delete(id);
        return "redirect:/dashboard/usuarios";
    }

    private Map<Integer, Empleado> obtenerEmpleadosPorUsuarioId() {
        return empleadoRepositorio.findAll().stream()
                .filter(empleado -> empleado.getUsuario() != null && empleado.getUsuario().getIdUsuario() != null)
                .collect(Collectors.toMap(
                        empleado -> empleado.getUsuario().getIdUsuario(),
                        Function.identity(),
                        (empleadoExistente, empleadoNuevo) -> empleadoExistente
                ));
    }
}
