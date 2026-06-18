package com.example.pikafast.Repositorio;

import com.example.pikafast.Entidad.Direccion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepositorio extends JpaRepository<Direccion, Integer> {
    Optional<Direccion> findFirstByClienteIdClienteOrderByIdDireccionAsc(Integer idCliente);
}
