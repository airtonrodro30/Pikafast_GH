package com.example.pikafast.Controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;

    @GetMapping()
    public String mostrarPedidos(Model model) {

        model.addAttribute("activeMenu", "pedidos");
        model.addAttribute("contentPage", "admin/pedidos");

        model.addAttribute("pedidos", pedidoRepositorio.findAll());

        return "dashboard";
    }

    @GetMapping("/ver/{id}")
    public String verPedido(@PathVariable Integer id, Model model) {

        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);

        model.addAttribute("pedido", pedido);
        model.addAttribute(
                "detalles",
                detallePedidoRepositorio.findByPedido_IdPedido(id)
        );

        return "admin/detalle-pedido";
    }
}
