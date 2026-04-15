package com.example.pikafast.Controlador;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexControlador {
 @GetMapping("/")
    public String mostrarIndex(Model model) {
        return "index";
    }
}
