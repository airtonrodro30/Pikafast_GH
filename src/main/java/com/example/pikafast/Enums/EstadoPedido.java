
package com.example.pikafast.Enums;

import java.util.List;

public enum EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeCancelarCliente() {
        return this == PENDIENTE;
    }

    public boolean esEstadoFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    public boolean puedeGestionarAdmin() {
        return this != CANCELADO && this != ENTREGADO;
    }

    public static List<EstadoPedido> estadosGestionAdmin() {
        return List.of(EN_PREPARACION, EN_CAMINO, ENTREGADO);
    }
}
