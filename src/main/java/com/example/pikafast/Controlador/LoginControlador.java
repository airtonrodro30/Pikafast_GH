package com.example.pikafast.Controlador;

import com.example.pikafast.model.Usuario;
import com.example.pikafast.repository.UsuarioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginControlador {

    private final UsuarioRepository repository;

    public LoginControlador(
            UsuarioRepository repository) {

        this.repository = repository;
    }

    @GetMapping("/login")
    public String mostrarLogin() {

        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String password,
            Model model) {

        Usuario usuario =
                repository.findByCorreoAndPassword(
                        correo,
                        password
                );

        if (usuario != null) {

            System.out.println(
                    usuario.getNombre()
            );

            return "redirect:/";
        }

        model.addAttribute(
                "error",
                "Correo o contraseña incorrectos"
        );

        return "login";
    }
}