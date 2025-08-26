package com.menu.uimarketsolo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Venta {
    private int id;
    private LocalDateTime fechaVenta;
    private BigDecimal ventaTotal;
    private String clienteCedula;

    public Venta(){

    }

    public Venta(int id, LocalDateTime fechaVenta, BigDecimal ventaTotal, String clienteCedula){
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.ventaTotal = ventaTotal;
        this.clienteCedula = clienteCedula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(BigDecimal ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public String getClienteCedula() {
        return clienteCedula;
    }

    public void setClienteCedula(String clienteCedula) {
        this.clienteCedula = clienteCedula;
    }

    @Override
    public String toString() {
        return "Ventas{" +
                "id=" + id +
                ", fechaVenta=" + fechaVenta +
                ", ventaTotal=" + ventaTotal +
                ", clienteCedula=" + clienteCedula +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta ventas = (Venta) o;
        return id == ventas.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}




