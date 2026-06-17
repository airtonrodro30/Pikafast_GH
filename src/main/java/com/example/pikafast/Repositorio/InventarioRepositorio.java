package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepositorio extends JpaRepository<Inventario, Integer> {

}