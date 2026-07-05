package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Direccion;
import com.example.pikafast.Repositorio.DireccionRepositorio;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DireccionServicio {

    private final DireccionRepositorio direccionRepositorio;

    public DireccionServicio(DireccionRepositorio direccionRepositorio) {
        this.direccionRepositorio = direccionRepositorio;
    }

    public Optional<Direccion> findPrincipalByClienteId(Integer idCliente) {
        return direccionRepositorio.findFirstByClienteIdClienteOrderByIdDireccionAsc(idCliente);
    }

    public Direccion save(Direccion direccion) {
        return direccionRepositorio.save(direccion);
    }
}
