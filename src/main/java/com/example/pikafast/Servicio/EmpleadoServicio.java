package com.example.pikafast.Servicio;

import com.example.pikafast.Entidad.Empleado;
import com.example.pikafast.Repositorio.EmpleadoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


public class EmpleadoServicio {
    @Autowired
    private EmpleadoRepositorio empleadoDAO;

    // Mostrar Todos los Registros
    public List<Empleado> getList() {
        return empleadoDAO.findAll();
    }

    // Guardar Un Registro
    public Empleado save(Empleado empleado) {
        return empleadoDAO.save(empleado);
    }

    // Obtener un Registro por id
    public Empleado get(Integer id) {
        return empleadoDAO.findById(id).orElse(null);
    }

    // Eliminar un Registro por id
    public void delete(Integer id) {
        empleadoDAO.deleteById(id);
    }
}
