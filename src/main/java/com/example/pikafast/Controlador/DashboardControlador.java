package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardControlador {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("contentPage", "admin/home");
        return "dashboard";
    }

    @GetMapping("/dashboard/usuarios")
    public String mostrarUsuarios(Model model) {
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("contentPage", "admin/usuarios");
        return "dashboard";
    }
    
    @GetMapping("/dashboard/productos")
    public String mostrarProductos(Model model) {
        model.addAttribute("activeMenu", "productos");
        model.addAttribute("contentPage", "admin/productos");
        return "dashboard";
    }
    
    @GetMapping("/dashboard/pedidos")
    public String mostrarPedidos(Model model) {
        model.addAttribute("activeMenu", "pedidos");
        model.addAttribute("contentPage", "admin/pedidos");
        return "dashboard";
    }
    
    @GetMapping("/dashboard/categorias")
    public String mostrarCategorias(Model model) {
        model.addAttribute("activeMenu", "categorias");
        model.addAttribute("contentPage", "admin/categorias");
        return "dashboard";
    }
    
    @GetMapping("/dashboard/inventario")
    public String mostrarInventario(Model model) {
        model.addAttribute("activeMenu", "inventario");
        model.addAttribute("contentPage", "admin/inventario");
        return "dashboard";
    }
}
