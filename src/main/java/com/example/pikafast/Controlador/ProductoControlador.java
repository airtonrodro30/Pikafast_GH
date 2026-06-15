package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Producto;
import com.example.pikafast.Servicio.ProductoServicio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.pikafast.Servicio.CategoriaServicio;

@Controller
@RequestMapping("/dashboard/productos")
public class ProductoControlador {

    private final ProductoServicio productoServicio;
    private final CategoriaServicio categoriaServicio;

    public ProductoControlador(ProductoServicio productoServicio, CategoriaServicio categoriaServicio) {
        this.productoServicio = productoServicio;
        this.categoriaServicio = categoriaServicio;
    }

    @GetMapping()
    public String mostrarProductos(Model model) {
        model.addAttribute("activeMenu", "productos");
        model.addAttribute("contentPage", "admin/productos");

        model.addAttribute("productos", productoServicio.getList());
        model.addAttribute("categorias", categoriaServicio.getList());
        model.addAttribute("producto", new Producto());
        return "dashboard";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
            @RequestParam("imagenProducto") MultipartFile file) {

        //Guardar Imagen y asignar Nombre de la imagen
        try {
            // Agrega Imagen Nueva si no existe una imagen agregada en ese registro
            if (file.isEmpty()) {
                // No subió nueva imagen, mantener la anterior
                if (producto.getIdProducto() != null) {
                    Producto existente = productoServicio.get(producto.getIdProducto());
                    if (existente != null) {
                        producto.setImagen(existente.getImagen());
                    }
                }
            } else {
                // Subió nueva imagen
                String fileName = file.getOriginalFilename();
                Path path = Paths.get("uploaded-images/" + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                producto.setImagen(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        productoServicio.save(producto);

        return "redirect:/dashboard/productos";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Integer id, Model model) {
        Producto producto = productoServicio.get(id);

        model.addAttribute("activeMenu", "productos");
        model.addAttribute("contentPage", "admin/productos");

        model.addAttribute("productos", productoServicio.getList());
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaServicio.getList());

        model.addAttribute("abrirModal", true);

        return "dashboard";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoServicio.delete(id);

        return "redirect:/dashboard/productos";
    }
}
