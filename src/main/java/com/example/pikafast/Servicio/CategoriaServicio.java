package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Categoria;
import java.util.List;

public interface CategoriaServicio {

    List<Categoria> listarCategorias();

    void guardarCategoria(Categoria categoria);
    void eliminarCategoria(Integer id);
}