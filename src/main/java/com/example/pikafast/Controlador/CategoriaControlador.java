package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/categorias")
public class CategoriaControlador {
    
    // Instacia de Categoria Servcio
    
    @GetMapping()
    public String mostrarCategorias(Model model) {
        model.addAttribute("activeMenu", "categorias");
        model.addAttribute("contentPage", "admin/categorias");
        return "dashboard";
    }
}
