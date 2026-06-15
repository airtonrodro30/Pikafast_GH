package com.example.pikafast.Config;

import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Servicio.UsuarioServicio;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioServicio usuarioServicio;

    public GlobalControllerAdvice(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Principal principal) {

        if (principal == null) {
            return null;
        }

        return usuarioServicio.findByEmail(principal.getName());
    }
}
