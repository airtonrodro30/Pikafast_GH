package com.example.pikafast.Controlador;

import com.example.pikafast.model.Usuario;
import com.example.pikafast.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class registrarControlador {

    private final UsuarioRepository repository;

    public registrarControlador(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(){

        return "register";
    }

    @PostMapping("/guardar-usuario")
    public String guardarUsuario(Usuario usuario){

        repository.save(usuario);

        return "redirect:/login";
    }
}