package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarritoControlador {
    
    @GetMapping("/carrito")
    public String mostrarCarritoDeCompras(){
        return "carrito";
    }
}
