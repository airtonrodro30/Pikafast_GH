package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Categoria;
import com.example.pikafast.Entidad.Producto;
import com.example.pikafast.Servicio.CategoriaServicio;
import com.example.pikafast.Servicio.ProductoServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class CatalogoControlador {
    
    @Autowired
    private ProductoServicio productoServicio;
    
    @Autowired
    private CategoriaServicio categoriaServicio;
    
    @GetMapping("/productos")
    public String mostrarCatalogo(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) String orden,
            Model model) {
        
        List<Producto> productos = productoServicio.filtrarProductos(
                categoriaId,
                precioMin,
                precioMax,
                orden
        );
        
        List<Categoria> categorias = categoriaServicio.getList();
        
        Categoria categoriaSeleccionada = null;
        if (categoriaId != null) {
            categoriaSeleccionada = categoriaServicio.get(categoriaId);
        }
        
        
        model.addAttribute("listaProductos", productos);
        model.addAttribute("listaCategorias", categorias);
        model.addAttribute("categoriaSeleccionada", categoriaSeleccionada);
        return "catalogo";
    }
;
}
