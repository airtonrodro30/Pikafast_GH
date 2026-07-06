package com.example.pikafast.Controlador;

import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/dashboard/api")
public class DashboardRestControlador {

    private final PedidoRepositorio pedidoRepositorio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;

    public DashboardRestControlador(PedidoRepositorio pedidoRepositorio,
            DetallePedidoRepositorio detallePedidoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.detallePedidoRepositorio = detallePedidoRepositorio;
    }

    @GetMapping("/chart")
    public Map<String, Object> obtenerChart(@RequestParam String tipo) {
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();
        String title;

        switch (tipo) {
            case "productos-mas-vendidos" -> {
                title = "Productos mas vendidos";
                for (Object[] row : detallePedidoRepositorio.obtenerProductosMasVendidos()) {
                    labels.add((String) row[0]);
                    values.add((Number) row[1]);
                }
            }
            case "pedidos-por-estado" -> {
                title = "Numero de pedidos por estado";
                for (Object[] row : pedidoRepositorio.contarPedidosPorEstado()) {
                    labels.add(String.valueOf(row[0]));
                    values.add((Number) row[1]);
                }
            }
            case "pedidos-por-mes" -> {
                title = "Numero de pedidos por mes";
                for (Object[] row : pedidoRepositorio.contarPedidosPorMes()) {
                    labels.add(obtenerNombreMes((Number) row[0]));
                    values.add((Number) row[1]);
                }
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de grafico no soportado: " + tipo
            );
        }

        return Map.of(
                "title", title,
                "labels", labels,
                "values", values
        );
    }

    private String obtenerNombreMes(Number monthNumber) {
        return switch (monthNumber.intValue()) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "Mes " + monthNumber.intValue();
        };
    }
}
