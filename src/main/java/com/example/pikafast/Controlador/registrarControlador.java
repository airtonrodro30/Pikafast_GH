package com.example.pikafast.Controlador;

<<<<<<< HEAD
import com.example.pikafast.model.Usuario;
import com.example.pikafast.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
=======
//import com.example.WebProyect.DTO.RegistroDTO;
//import com.example.WebProyect.Entidad.Cliente;
//import com.example.WebProyect.Servicio.ClienteServicio;
import com.example.pikafast.Entidad.Usuario;
import com.example.pikafast.Enums.Rol;
import com.example.pikafast.Servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
>>>>>>> 2c511dbd7c6f59e772b315474f413c2ddb721ff0

@Controller
public class registrarControlador {

<<<<<<< HEAD
    private final UsuarioRepository repository;

    public registrarControlador(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(){

        return "register";
=======
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario nuevoUsuario) {
        /* Registrar Usuario */

        // 1. Encriptamos la contraseña que YA viene dentro de nuevoUsuario
        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        // 2. Asignamos los valores por defecto que no vienen del formulario
        nuevoUsuario.setRol(Rol.CLIENTE);
        nuevoUsuario.setActivo(true);

        // 3. Guardamos el objeto que recibimos y modificamos
        Usuario usuarioGuardado = usuarioServicio.save(nuevoUsuario);

        return "redirect:/login";
>>>>>>> 2c511dbd7c6f59e772b315474f413c2ddb721ff0
    }

    @PostMapping("/guardar-usuario")
    public String guardarUsuario(Usuario usuario){

        repository.save(usuario);

        return "redirect:/login";
    }
}