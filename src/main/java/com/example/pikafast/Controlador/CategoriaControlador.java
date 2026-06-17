package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.pikafast.Servicio.CategoriaServicio;

@Controller
@RequestMapping("/dashboard/categorias")
public class CategoriaControlador {

    @Autowired
    private CategoriaServicio categoriaServicio;

    @GetMapping
    public String mostrarCategorias(Model model) {

        model.addAttribute("activeMenu", "categorias");
        model.addAttribute("contentPage", "admin/categorias");

        model.addAttribute("categoria", new Categoria());

        model.addAttribute("listaCategorias",
                categoriaServicio.getList());

        return "dashboard";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(Categoria categoria){

        categoriaServicio.save(categoria);

        return "redirect:/dashboard/categorias";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Integer id){

        categoriaServicio.delete(id);

        return "redirect:/dashboard/categorias";
    }
}

