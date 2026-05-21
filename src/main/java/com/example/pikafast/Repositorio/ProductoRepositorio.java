package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Producto;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

}
