package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByCliente_IdClienteOrderByFechaDesc(Integer idCliente);

    @Query("""
        select p.estadoPedido, count(p)
        from Pedido p
        group by p.estadoPedido
        order by p.estadoPedido
    """)
    List<Object[]> contarPedidosPorEstado();

    @Query("""
        select function('month', p.fecha), count(p)
        from Pedido p
        where p.fecha is not null
        group by function('month', p.fecha)
        order by function('month', p.fecha)
    """)
    List<Object[]> contarPedidosPorMes();
}
