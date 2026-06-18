package com.example.pikafast.Config;

import com.example.pikafast.Entidad.Empleado;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.EmpleadoRepositorio;
import com.example.pikafast.Servicio.UsuarioServicio;
import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioServicio usuarioServicio;
    private final EmpleadoRepositorio empleadoRepositorio;

    public GlobalControllerAdvice(UsuarioServicio usuarioServicio, EmpleadoRepositorio empleadoRepositorio) {
        this.usuarioServicio = usuarioServicio;
        this.empleadoRepositorio = empleadoRepositorio;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Principal principal) {
        if (principal == null) {
            return null;
        }

        return usuarioServicio.findByEmail(principal.getName());
    }

    @ModelAttribute("empleadoActual")
    public Empleado empleadoActual(Principal principal) {
        if (principal == null) {
            return null;
        }

        return empleadoRepositorio.findByUsuarioEmail(principal.getName()).orElse(null);
    }
}
