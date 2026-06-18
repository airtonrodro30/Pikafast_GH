package com.example.pikafast.Controlador;

import com.example.pikafast.DTO.ConfirmarCompraDTO;
import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Servicio.CompraServicio;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compras")
public class CompraControlador {

    private final CompraServicio compraServicio;

    public CompraControlador(CompraServicio compraServicio) {
        this.compraServicio = compraServicio;
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarCompra(@RequestBody ConfirmarCompraDTO dto, Principal principal) {
        try {
            Pedido pedido = compraServicio.confirmarCompra(principal != null ? principal.getName() : null, dto);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ok", true);
            response.put("mensaje", "Compra registrada correctamente.");
            response.put("idPedido", pedido.getIdPedido());
            response.put("precioTotal", pedido.getPrecioTotal());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ok", false);
            response.put("mensaje", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
