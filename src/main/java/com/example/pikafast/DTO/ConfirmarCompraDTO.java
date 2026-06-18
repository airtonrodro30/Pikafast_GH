package com.example.pikafast.DTO;

import java.util.ArrayList;
import java.util.List;

public class ConfirmarCompraDTO {
    private String metodoPago;
    private String tipoEnvio;
    private List<ItemPedidoDTO> items = new ArrayList<>();

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public List<ItemPedidoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoDTO> items) {
        this.items = items;
    }
}
