package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Inventario;
import com.example.pikafast.Servicio.InventarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/inventario")
public class InventarioControlador {

    @Autowired
    private InventarioServicio inventarioServicio;

    @GetMapping()
    public String mostrarInventario(Model model) {

        model.addAttribute("inventarios",
                inventarioServicio.listarInventario());

        model.addAttribute("inventario", new Inventario());

        model.addAttribute("activeMenu", "inventario");
        model.addAttribute("contentPage", "admin/inventario");

        return "dashboard";
    }

    @PostMapping("/guardar")
    public String guardarInventario(
            @ModelAttribute Inventario inventario) {

        inventarioServicio.guardar(inventario);

        return "redirect:/dashboard/inventario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarInventario(@PathVariable Integer id) {

        inventarioServicio.eliminar(id);

        return "redirect:/dashboard/inventario";
    }

    @GetMapping("/editar/{id}")
    public String editarInventario(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute("inventarios",
                inventarioServicio.listarInventario());

        model.addAttribute("inventario",
                inventarioServicio.obtenerPorId(id));

        model.addAttribute("activeMenu", "inventario");
        model.addAttribute("contentPage", "admin/inventario");

        return "dashboard";
    }
}