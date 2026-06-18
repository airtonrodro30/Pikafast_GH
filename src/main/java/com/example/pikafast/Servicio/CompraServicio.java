package com.example.pikafast.Servicio;

import com.example.pikafast.DTO.ConfirmarCompraDTO;
import com.example.pikafast.DTO.ItemPedidoDTO;
import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Entidad.DetallePedido;
import com.example.pikafast.Entidad.Direccion;
import com.example.pikafast.Entidad.Pago;
import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Entidad.Producto;
import com.example.pikafast.Enums.EstadoPago;
import com.example.pikafast.Enums.EstadoPedido;
import com.example.pikafast.Enums.MetodoPago;
import com.example.pikafast.Enums.TipoEnvio;
import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PagoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import com.example.pikafast.Repositorio.ProductoRepositorio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraServicio {

    private final ClienteServicio clienteServicio;
    private final DireccionServicio direccionServicio;
    private final PedidoRepositorio pedidoRepositorio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;
    private final PagoRepositorio pagoRepositorio;
    private final ProductoRepositorio productoRepositorio;

    public CompraServicio(ClienteServicio clienteServicio,
            DireccionServicio direccionServicio,
            PedidoRepositorio pedidoRepositorio,
            DetallePedidoRepositorio detallePedidoRepositorio,
            PagoRepositorio pagoRepositorio,
            ProductoRepositorio productoRepositorio) {
        this.clienteServicio = clienteServicio;
        this.direccionServicio = direccionServicio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.detallePedidoRepositorio = detallePedidoRepositorio;
        this.pagoRepositorio = pagoRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    @Transactional
    public Pedido confirmarCompra(String emailAutenticado, ConfirmarCompraDTO dto) {
        if (emailAutenticado == null || emailAutenticado.isBlank()) {
            throw new IllegalArgumentException("No se pudo identificar al usuario autenticado.");
        }

        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("No se puede confirmar una compra con el carrito vacío.");
        }

        MetodoPago metodoPago = parseMetodoPago(dto.getMetodoPago());
        TipoEnvio tipoEnvio = parseTipoEnvio(dto.getTipoEnvio());

        Cliente cliente = clienteServicio.findByUsuarioEmail(emailAutenticado)
                .orElseThrow(() -> new IllegalStateException("No existe un cliente asociado a la cuenta autenticada."));

        Direccion direccion = direccionServicio.findPrincipalByClienteId(cliente.getIdCliente())
                .orElseThrow(() -> new IllegalStateException("El cliente no tiene una dirección registrada."));

        String direccionEntrega = construirDireccionEntrega(direccion);
        if (direccionEntrega.isBlank()) {
            throw new IllegalStateException("La dirección de entrega del cliente está incompleta.");
        }

        Map<Integer, Integer> cantidadesPorProducto = normalizarItems(dto.getItems());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDireccionEntrega(direccionEntrega);
        pedido.setPrecioTotal(BigDecimal.ZERO);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTipoEnvio(tipoEnvio);
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
        pedido = pedidoRepositorio.save(pedido);

        BigDecimal totalPedido = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> item : cantidadesPorProducto.entrySet()) {
            Producto producto = productoRepositorio.findById(item.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el producto con id " + item.getKey() + "."));

            BigDecimal precioUnitario = producto.getPrecio();
            if (precioUnitario == null) {
                throw new IllegalStateException("El producto " + producto.getNombre() + " no tiene un precio válido.");
            }

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getValue()));

            DetallePedido detallePedido = new DetallePedido();
            detallePedido.setPedido(pedido);
            detallePedido.setProducto(producto);
            detallePedido.setCantidad(item.getValue());
            detallePedido.setPrecioUnitario(precioUnitario);
            detallePedido.setSubtotal(subtotal);
            detallePedidoRepositorio.save(detallePedido);

            totalPedido = totalPedido.add(subtotal);
        }

        pedido.setPrecioTotal(totalPedido);
        pedido = pedidoRepositorio.save(pedido);

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(totalPedido);
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pago.setCodigoOperacion(null);
        pago.setFechaPago(LocalDateTime.now());
        pagoRepositorio.save(pago);

        return pedido;
    }

    private MetodoPago parseMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new IllegalArgumentException("Debes seleccionar un método de pago válido.");
        }

        try {
            return MetodoPago.valueOf(metodoPago.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El método de pago enviado no es válido.");
        }
    }

    private TipoEnvio parseTipoEnvio(String tipoEnvio) {
        if (tipoEnvio == null || tipoEnvio.isBlank()) {
            return TipoEnvio.DELIVERY;
        }

        try {
            TipoEnvio envio = TipoEnvio.valueOf(tipoEnvio.trim().toUpperCase());
            if (envio != TipoEnvio.DELIVERY) {
                throw new IllegalArgumentException("Por ahora solo se permite el tipo de envío DELIVERY.");
            }
            return envio;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El tipo de envío enviado no es válido.");
        }
    }

    private Map<Integer, Integer> normalizarItems(List<ItemPedidoDTO> items) {
        Map<Integer, Integer> cantidadesPorProducto = new LinkedHashMap<>();

        for (ItemPedidoDTO item : items) {
            if (item.getIdProducto() == null) {
                throw new IllegalArgumentException("Todos los productos del carrito deben tener un id válido.");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Las cantidades del carrito deben ser mayores a cero.");
            }

            cantidadesPorProducto.merge(item.getIdProducto(), item.getCantidad(), Integer::sum);
        }

        return cantidadesPorProducto;
    }

    private String construirDireccionEntrega(Direccion direccion) {
        StringBuilder direccionCompleta = new StringBuilder();
        appendSegment(direccionCompleta, direccion.getDireccion());
        appendSegment(direccionCompleta, direccion.getDistrito());
        appendSegment(direccionCompleta, direccion.getProvincia());
        appendSegment(direccionCompleta, direccion.getDepartamento());

        if (direccion.getCodigoPostal() != null && !direccion.getCodigoPostal().isBlank()) {
            appendSegment(direccionCompleta, "CP " + direccion.getCodigoPostal().trim());
        }

        return direccionCompleta.toString();
    }

    private void appendSegment(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value.trim());
    }
}
