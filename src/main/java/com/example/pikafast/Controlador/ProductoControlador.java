package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/productos")
public class ProductoControlador {
    
    // importar Producto Servicio
    
    @GetMapping()
    public String mostrarProductos(Model model) {
        model.addAttribute("activeMenu", "productos");
        model.addAttribute("contentPage", "admin/productos");
        return "dashboard";
    }
}
