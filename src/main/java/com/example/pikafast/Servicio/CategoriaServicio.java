package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Categoria;
import com.example.pikafast.Repositorio.CategoriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServicio{

    @Autowired
    private CategoriaRepositorio categoriaDAO;
    
    // Mostrar Todos los Registros
    public List<Categoria> getList() {
        return categoriaDAO.findAll();
    }

    // Guardar Un Registro
    public Categoria save(Categoria categoria) {
        return categoriaDAO.save(categoria);
    }

    // Obtener un Registro por id
    public Categoria get(Integer id) {
        return categoriaDAO.findById(id).orElse(null);
    }

    // Eliminar un Registro por id
    public void delete(Integer id) {
        categoriaDAO.deleteById(id);
    }
    
    /*
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
    */
}
