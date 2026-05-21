package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Inventario;
import com.example.pikafast.Repositorio.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServicio {

    @Autowired
    private InventarioRepositorio inventarioRepositorio;

    public List<Inventario> listarInventario() {
        return inventarioRepositorio.findAll();
    }

    public Inventario obtenerPorId(Integer id){
    return inventarioRepositorio.findById(id).orElse(null);
}

    public void eliminar(Integer id) {
        inventarioRepositorio.deleteById(id);
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepositorio.save(inventario);
    }
}