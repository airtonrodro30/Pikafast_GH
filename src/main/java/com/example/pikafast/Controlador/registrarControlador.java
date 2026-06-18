package com.example.pikafast.Controlador;

import com.example.pikafast.DTO.RegistroClienteDTO;
import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Enums.Rol;
import com.example.pikafast.Servicio.ClienteServicio;
import com.example.pikafast.Servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class registrarControlador {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UsuarioServicio usuarioServicio;

    @Autowired
    ClienteServicio clienteServicio;

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroCliente", new RegistroClienteDTO());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute("registroCliente") RegistroClienteDTO registroCliente, Model model) {
        if (usuarioServicio.findByEmail(registroCliente.getEmail()) != null) {
            model.addAttribute("errorRegistro", "El correo ingresado ya está registrado.");
            return "registrar";
        }

        if (clienteServicio.findByDni(registroCliente.getDni()).isPresent()) {
            model.addAttribute("errorRegistro", "El DNI ingresado ya está registrado.");
            return "registrar";
        }

        if (registroCliente.getContrasenia() == null
                || !registroCliente.getContrasenia().equals(registroCliente.getConfirmarContrasenia())) {
            model.addAttribute("errorRegistro", "La contraseña y su confirmación no coinciden.");
            return "registrar";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(registroCliente.getEmail());
        nuevoUsuario.setContrasenia(passwordEncoder.encode(registroCliente.getContrasenia()));
        nuevoUsuario.setRol(Rol.CLIENTE);
        nuevoUsuario.setActivo(true);

        Usuario usuarioGuardado = usuarioServicio.save(nuevoUsuario);

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombres(registroCliente.getNombres());
        nuevoCliente.setApellidos(registroCliente.getApellidos());
        nuevoCliente.setDni(registroCliente.getDni());
        nuevoCliente.setTelefono(registroCliente.getTelefono());
        nuevoCliente.setUsuario(usuarioGuardado);

        clienteServicio.save(nuevoCliente);

        return "redirect:/login";
    }
}
