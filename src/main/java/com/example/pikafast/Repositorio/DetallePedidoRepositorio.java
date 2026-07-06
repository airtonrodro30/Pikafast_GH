package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedido_IdPedido(Integer idPedido);

    @Query("""
        select d.producto.nombre, sum(d.cantidad)
        from DetallePedido d
        group by d.producto.idProducto, d.producto.nombre
        order by sum(d.cantidad) desc
    """)
    List<Object[]> obtenerProductosMasVendidos();
}
