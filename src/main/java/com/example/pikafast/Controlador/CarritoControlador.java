package com.example.pikafast.Controlador;

import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Entidad.Direccion;
import com.example.pikafast.Servicio.ClienteServicio;
import com.example.pikafast.Servicio.DireccionServicio;
import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarritoControlador {

    private final ClienteServicio clienteServicio;
    private final DireccionServicio direccionServicio;

    public CarritoControlador(ClienteServicio clienteServicio, DireccionServicio direccionServicio) {
        this.clienteServicio = clienteServicio;
        this.direccionServicio = direccionServicio;
    }
    
    @GetMapping("/carrito")
    public String mostrarCarritoDeCompras(){
        return "carrito";
    }

    @GetMapping("/pago")
    public String mostrarPago(Principal principal, Model model) {
        Cliente cliente = principal != null
                ? clienteServicio.findByUsuarioEmail(principal.getName()).orElse(null)
                : null;

        Direccion direccion = (cliente != null && cliente.getIdCliente() != null)
                ? direccionServicio.findPrincipalByClienteId(cliente.getIdCliente()).orElse(null)
                : null;

        model.addAttribute("clientePago", cliente);
        model.addAttribute("direccionPago", direccion);
        model.addAttribute("clienteTieneDireccion", direccion != null);

        return "pago";
    }
}
