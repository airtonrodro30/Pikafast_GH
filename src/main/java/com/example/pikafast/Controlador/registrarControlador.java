package com.example.pikafast.Controlador;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class registrarControlador {
@GetMapping("/register")
    public String mostrarFormularioRegistro(){
        return "register";
    }
}
