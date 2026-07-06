package com.example.pikafast.Controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Enums.EstadoPedido;
import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import com.example.pikafast.Servicio.PedidoServicio;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;

    @Autowired
    private PedidoServicio pedidoServicio;

    @GetMapping()
    public String mostrarPedidos(Model model) {

        model.addAttribute("activeMenu", "pedidos");
        model.addAttribute("contentPage", "admin/pedidos");

        model.addAttribute("pedidos", pedidoRepositorio.findAll());
        model.addAttribute("estadosAdmin", EstadoPedido.estadosGestionAdmin());

        return "dashboard";
    }

    @GetMapping("/ver/{id}")
    public String verPedido(@PathVariable Integer id, Model model) {

        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);

        model.addAttribute("activeMenu", "pedidos");
        model.addAttribute("contentPage", "admin/detalle-pedido");
        model.addAttribute("pageStylesheet", "/css/detalle-ordenes.css");
        model.addAttribute("pedido", pedido);
        model.addAttribute(
                "detalles",
                detallePedidoRepositorio.findByPedido_IdPedido(id)
        );

        return "dashboard";
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstadoPedido(@PathVariable Integer id,
            @RequestParam("estadoPedido") EstadoPedido estadoPedido,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoServicio.actualizarEstadoDesdeAdmin(id, estadoPedido);
            redirectAttributes.addFlashAttribute("pedidoExito", "El estado del pedido #ORD-" + id + " fue actualizado.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("pedidoError", ex.getMessage());
        }

        return "redirect:/dashboard/pedidos";
    }
}
