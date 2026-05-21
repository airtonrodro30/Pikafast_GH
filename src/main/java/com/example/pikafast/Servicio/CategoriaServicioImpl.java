package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Categoria;
import com.example.pikafast.Repositorio.CategoriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServicioImpl implements CategoriaServicio {

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAll();
    }

    @Override
    public void guardarCategoria(Categoria categoria) {
        categoriaRepositorio.save(categoria);
    }
    
    @Override
public void eliminarCategoria(Integer id) {

    categoriaRepositorio.deleteById(id);

}
}