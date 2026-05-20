package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/inventario")
public class InventarioControlador {
    
    // Instancia InventarioServicio
    
    @GetMapping()
    public String mostrarInventario(Model model) {
        model.addAttribute("activeMenu", "inventario");
        model.addAttribute("contentPage", "admin/inventario");
        return "dashboard";
    }
}
