package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Producto;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    
    @Query("""
        SELECT p FROM Producto p
        WHERE (:categoriaId IS NULL OR p.categoria.idCategoria = :categoriaId)
        AND (:precioMin IS NULL OR p.precio >= :precioMin)
        AND (:precioMax IS NULL OR p.precio <= :precioMax)
    """)
    List<Producto> filtrarProductos(
            @Param("categoriaId") Integer categoriaId,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            Sort sort
    );
}
