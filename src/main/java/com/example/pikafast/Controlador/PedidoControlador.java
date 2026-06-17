package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/pedidos")
public class PedidoControlador {
    
    // Instancia de PedidoServicio
    
    @GetMapping()
    public String mostrarPedidos(Model model) {
        model.addAttribute("activeMenu", "pedidos");
        model.addAttribute("contentPage", "admin/pedidos");
        return "dashboard";
    }
}
