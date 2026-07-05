package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Cliente;
import com.example.pikafast.Repositorio.ClienteRepositorio;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public Cliente save(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    public Optional<Cliente> findByDni(String dni) {
        return clienteRepositorio.findByDni(dni);
    }

    public Optional<Cliente> findByUsuarioEmail(String email) {
        return clienteRepositorio.findByUsuarioEmail(email);
    }
}
