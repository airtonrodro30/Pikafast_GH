package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Producto;
import com.example.pikafast.Repositorio.ProductoRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicio {
    
    @Autowired
    private ProductoRepositorio productoDAO;
    
    // Mostrar Todos los Registros
    public List<Producto> getList() {
        return productoDAO.findAll();
    }

    // Guardar Un Registro
    public Producto save(Producto producto) {
        return productoDAO.save(producto);
    }

    // Obtener un Registro por id
    public Producto get(Integer id) {
        return productoDAO.findById(id).orElse(null);
    }

    // Eliminar un Registro por id
    public void delete(Integer id) {
        productoDAO.deleteById(id);
    }
    
}
