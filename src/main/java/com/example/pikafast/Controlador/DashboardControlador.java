package com.example.pikafast.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardControlador {

    @GetMapping("/dashboard")
    public String mostrarDashboard(){
        return "dashboard";
    }
}
