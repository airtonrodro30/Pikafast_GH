package com.example.pikafast.Entidad;

import com.example.pikafast.Enums.TipoInventario;
import com.example.pikafast.Enums.UnidadMedida;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_inventario")
    private TipoInventario tipoInventario;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida")
    private UnidadMedida unidadMedida;

    private BigDecimal stock;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo;

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoInventario getTipoInventario() {
        return tipoInventario;
    }

    public void setTipoInventario(TipoInventario tipoInventario) {
        this.tipoInventario = tipoInventario;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}