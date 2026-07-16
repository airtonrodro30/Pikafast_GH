package com.example.pikafast.Controlador;

import com.example.pikafast.DTO.DireccionClienteDTO;
import com.example.pikafast.DTO.PerfilClienteDTO;
import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Entidad.Direccion;
import com.example.pikafast.Entidad.Pedido;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Repositorio.DetallePedidoRepositorio;
import com.example.pikafast.Repositorio.PedidoRepositorio;
import com.example.pikafast.Servicio.ClienteServicio;
import com.example.pikafast.Servicio.DireccionServicio;
import com.example.pikafast.Servicio.PedidoServicio;
import com.example.pikafast.Servicio.UsuarioServicio;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class PerfilClienteControlador {

    private final ClienteServicio clienteServicio;
    private final DireccionServicio direccionServicio;
    private final UsuarioServicio usuarioServicio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final PedidoServicio pedidoServicio;

    public PerfilClienteControlador(ClienteServicio clienteServicio,
            DireccionServicio direccionServicio,
            UsuarioServicio usuarioServicio,
            DetallePedidoRepositorio detallePedidoRepositorio,
            PedidoRepositorio pedidoRepositorio,
            PedidoServicio pedidoServicio) {
        this.clienteServicio = clienteServicio;
        this.direccionServicio = direccionServicio;
        this.usuarioServicio = usuarioServicio;
        this.detallePedidoRepositorio = detallePedidoRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.pedidoServicio = pedidoServicio;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Principal principal,
            @RequestParam(name = "editarDatos", defaultValue = "false") boolean editarDatos,
            @RequestParam(name = "editarDireccion", defaultValue = "false") boolean editarDireccion,
            Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        Cliente cliente = obtenerClienteAutenticado(principal);
        Direccion direccion = obtenerDireccionPrincipal(cliente);

        cargarVistaPerfil(
                model,
                usuario,
                cliente,
                direccion,
                mapearPerfilCliente(cliente, usuario),
                mapearDireccion(direccion),
                editarDatos,
                editarDireccion
        );
        model.addAttribute("activeClientMenu", "perfil");

        return "client/perfil-cliente";
    }

    @GetMapping("/pedidos")
    public String mostrarPedidosCliente(Principal principal, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        Cliente cliente = obtenerClienteAutenticado(principal);
        List<Pedido> pedidos = cliente != null
                ? pedidoRepositorio.findByCliente_IdClienteOrderByFechaDesc(cliente.getIdCliente())
                : List.of();

        model.addAttribute("perfilDisponible", cliente != null);
        model.addAttribute("emailCuenta", usuario != null ? usuario.getEmail() : "");
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("activeClientMenu", "pedidos");

        return "client/perfil-pedidos";
    }

    @GetMapping("/pedidos/ver/{id}")
    public String mostrarDetallePedidoCliente(@PathVariable Integer id,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        Cliente cliente = obtenerClienteAutenticado(principal);

        if (cliente == null) {
            redirectAttributes.addFlashAttribute("pedidoError", "No se encontró un cliente asociado a la cuenta autenticada.");
            return "redirect:/cliente/pedidos";
        }

        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);
        if (pedido == null || pedido.getCliente() == null
                || !pedido.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            redirectAttributes.addFlashAttribute("pedidoError", "No puedes ver el detalle de ese pedido.");
            return "redirect:/cliente/pedidos";
        }

        model.addAttribute("perfilDisponible", true);
        model.addAttribute("emailCuenta", usuario != null ? usuario.getEmail() : "");
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", detallePedidoRepositorio.findByPedido_IdPedido(id));
        model.addAttribute("activeClientMenu", "pedidos");

        return "client/detalle-pedido";
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public String cancelarPedidoCliente(@PathVariable Integer id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoServicio.cancelarPedidoPorCliente(id, principal != null ? principal.getName() : null);
            redirectAttributes.addFlashAttribute("pedidoExito", "El pedido #ORD-" + id + " fue cancelado.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("pedidoError", ex.getMessage());
        }

        return "redirect:/cliente/pedidos";
    }

    @PostMapping("/datos")
    public String actualizarDatos(@ModelAttribute("perfilCliente") PerfilClienteDTO perfilCliente,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        Cliente cliente = obtenerClienteAutenticado(principal);
        Direccion direccion = obtenerDireccionPrincipal(cliente);

        if (usuario == null || cliente == null) {
            redirectAttributes.addFlashAttribute("perfilError", "No se encontró un perfil de cliente asociado a la cuenta autenticada.");
            return "redirect:/cliente/perfil";
        }

        if (tieneCamposVacios(
                perfilCliente.getNombres(),
                perfilCliente.getApellidos(),
                perfilCliente.getDni(),
                perfilCliente.getTelefono(),
                perfilCliente.getEmail())) {
            model.addAttribute("perfilError", "Completa todos los datos personales antes de guardar.");
            cargarVistaPerfil(model, usuario, cliente, direccion, perfilCliente, mapearDireccion(direccion), true, false);
            return "client/perfil-cliente";
        }

        Usuario usuarioPorEmail = usuarioServicio.findByEmail(perfilCliente.getEmail());
        if (usuarioPorEmail != null && !usuarioPorEmail.getIdUsuario().equals(usuario.getIdUsuario())) {
            model.addAttribute("perfilError", "El correo ingresado ya pertenece a otra cuenta.");
            cargarVistaPerfil(model, usuario, cliente, direccion, perfilCliente, mapearDireccion(direccion), true, false);
            return "client/perfil-cliente";
        }

        Optional<Cliente> clientePorDni = clienteServicio.findByDni(perfilCliente.getDni());
        if (clientePorDni.isPresent() && !clientePorDni.get().getIdCliente().equals(cliente.getIdCliente())) {
            model.addAttribute("perfilError", "El DNI ingresado ya pertenece a otro cliente.");
            cargarVistaPerfil(model, usuario, cliente, direccion, perfilCliente, mapearDireccion(direccion), true, false);
            return "client/perfil-cliente";
        }

        cliente.setNombres(perfilCliente.getNombres());
        cliente.setApellidos(perfilCliente.getApellidos());
        cliente.setDni(perfilCliente.getDni());
        cliente.setTelefono(perfilCliente.getTelefono());

        String emailAnterior = usuario.getEmail();
        usuario.setEmail(perfilCliente.getEmail());

        usuarioServicio.save(usuario);
        clienteServicio.save(cliente);

        if (!emailAnterior.equals(perfilCliente.getEmail())) {
            actualizarAutenticacion(perfilCliente.getEmail());
        }

        redirectAttributes.addFlashAttribute("perfilExito", "Tus datos personales fueron actualizados.");
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/direccion")
    public String guardarDireccion(@ModelAttribute("direccionCliente") DireccionClienteDTO direccionCliente,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        Cliente cliente = obtenerClienteAutenticado(principal);
        Direccion direccion = obtenerDireccionPrincipal(cliente);

        if (usuario == null || cliente == null) {
            redirectAttributes.addFlashAttribute("perfilError", "No se encontró un perfil de cliente asociado a la cuenta autenticada.");
            return "redirect:/cliente/perfil";
        }

        if (tieneCamposVacios(
                direccionCliente.getDepartamento(),
                direccionCliente.getProvincia(),
                direccionCliente.getDistrito(),
                direccionCliente.getDireccion(),
                direccionCliente.getCodigoPostal())) {
            model.addAttribute("perfilError", "Completa todos los datos de la dirección antes de guardar.");
            cargarVistaPerfil(model, usuario, cliente, direccion, mapearPerfilCliente(cliente, usuario), direccionCliente, false, true);
            return "client/perfil-cliente";
        }

        Direccion direccionAGuardar = direccion != null ? direccion : new Direccion();
        direccionAGuardar.setCliente(cliente);
        direccionAGuardar.setDepartamento(direccionCliente.getDepartamento());
        direccionAGuardar.setProvincia(direccionCliente.getProvincia());
        direccionAGuardar.setDistrito(direccionCliente.getDistrito());
        direccionAGuardar.setDireccion(direccionCliente.getDireccion());
        direccionAGuardar.setCodigoPostal(direccionCliente.getCodigoPostal());

        direccionServicio.save(direccionAGuardar);

        redirectAttributes.addFlashAttribute("direccionExito", direccion == null
                ? "Tu dirección fue registrada correctamente."
                : "Tu dirección fue actualizada correctamente.");
        return "redirect:/cliente/perfil";
    }

    private Usuario obtenerUsuarioAutenticado(Principal principal) {
        if (principal == null) {
            return null;
        }
        return usuarioServicio.findByEmail(principal.getName());
    }

    private Cliente obtenerClienteAutenticado(Principal principal) {
        if (principal == null) {
            return null;
        }
        return clienteServicio.findByUsuarioEmail(principal.getName()).orElse(null);
    }

    private Direccion obtenerDireccionPrincipal(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return null;
        }
        return direccionServicio.findPrincipalByClienteId(cliente.getIdCliente()).orElse(null);
    }

    private PerfilClienteDTO mapearPerfilCliente(Cliente cliente, Usuario usuario) {
        PerfilClienteDTO dto = new PerfilClienteDTO();
        if (cliente != null) {
            dto.setNombres(cliente.getNombres());
            dto.setApellidos(cliente.getApellidos());
            dto.setDni(cliente.getDni());
            dto.setTelefono(cliente.getTelefono());
        }
        if (usuario != null) {
            dto.setEmail(usuario.getEmail());
        }
        return dto;
    }

    private DireccionClienteDTO mapearDireccion(Direccion direccion) {
        DireccionClienteDTO dto = new DireccionClienteDTO();
        if (direccion != null) {
            dto.setDepartamento(direccion.getDepartamento());
            dto.setProvincia(direccion.getProvincia());
            dto.setDistrito(direccion.getDistrito());
            dto.setDireccion(direccion.getDireccion());
            dto.setCodigoPostal(direccion.getCodigoPostal());
        }
        return dto;
    }

    private void cargarVistaPerfil(Model model,
            Usuario usuario,
            Cliente cliente,
            Direccion direccion,
            PerfilClienteDTO perfilCliente,
            DireccionClienteDTO direccionCliente,
            boolean modoEdicionDatos,
            boolean modoEdicionDireccion) {
        model.addAttribute("perfilCliente", perfilCliente);
        model.addAttribute("direccionCliente", direccionCliente);
        model.addAttribute("clienteTieneDireccion", direccion != null);
        model.addAttribute("modoEdicionDatos", modoEdicionDatos);
        model.addAttribute("modoEdicionDireccion", modoEdicionDireccion || direccion == null);
        model.addAttribute("perfilDisponible", cliente != null);
        model.addAttribute("emailCuenta", usuario != null ? usuario.getEmail() : "");
    }

    private void actualizarAutenticacion(String nuevoEmail) {
        Authentication autenticacionActual = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacionActual == null) {
            return;
        }

        UsernamePasswordAuthenticationToken nuevaAutenticacion =
                new UsernamePasswordAuthenticationToken(
                        nuevoEmail,
                        autenticacionActual.getCredentials(),
                        autenticacionActual.getAuthorities()
                );

        nuevaAutenticacion.setDetails(autenticacionActual.getDetails());
        SecurityContextHolder.getContext().setAuthentication(nuevaAutenticacion);
    }

    private boolean tieneCamposVacios(String... valores) {
        for (String valor : valores) {
            if (valor == null || valor.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
