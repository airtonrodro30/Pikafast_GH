package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Enums.EstadoPedido;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final ClienteServicio clienteServicio;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, ClienteServicio clienteServicio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.clienteServicio = clienteServicio;
    }

    @Transactional
    public Pedido actualizarEstadoDesdeAdmin(Integer idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el pedido seleccionado."));

        if (!EstadoPedido.estadosGestionAdmin().contains(nuevoEstado)) {
            throw new IllegalArgumentException("El panel admin solo puede asignar estados de gestión válidos.");
        }

        EstadoPedido estadoActual = pedido.getEstadoPedido();
        if (estadoActual == null) {
            throw new IllegalStateException("El pedido no tiene un estado actual válido.");
        }

        if (estadoActual == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede actualizar un pedido cancelado.");
        }

        if (estadoActual == EstadoPedido.ENTREGADO && nuevoEstado != EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("Un pedido entregado ya no puede cambiar de estado.");
        }

        pedido.setEstadoPedido(nuevoEstado);
        return pedidoRepositorio.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedidoPorCliente(Integer idPedido, String emailCliente) {
        if (emailCliente == null || emailCliente.isBlank()) {
            throw new IllegalArgumentException("No se pudo identificar al cliente autenticado.");
        }

        Cliente cliente = clienteServicio.findByUsuarioEmail(emailCliente)
                .orElseThrow(() -> new IllegalStateException("No existe un cliente asociado a la cuenta autenticada."));

        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el pedido seleccionado."));

        if (pedido.getCliente() == null || !cliente.getIdCliente().equals(pedido.getCliente().getIdCliente())) {
            throw new IllegalStateException("No tienes permisos para cancelar este pedido.");
        }

        EstadoPedido estadoActual = pedido.getEstadoPedido();
        if (estadoActual == null || !estadoActual.puedeCancelarCliente()) {
            throw new IllegalStateException("Este pedido ya no puede cancelarse.");
        }

        pedido.setEstadoPedido(EstadoPedido.CANCELADO);
        return pedidoRepositorio.save(pedido);
    }
}
