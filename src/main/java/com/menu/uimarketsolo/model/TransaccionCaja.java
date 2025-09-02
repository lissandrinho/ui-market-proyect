package com.menu.uimarketsolo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TransaccionCaja {
    private int id;
    private LocalDateTime fechaTransaccion;
    private String tipoTransaccion;
    private double monto;
    private String descripcion;
    private int ventaId;

    // Constructor vacío
    public TransaccionCaja() {
    }

    public TransaccionCaja(int id, LocalDateTime fechaTransaccion, String tipoTransaccion, double monto, String descripcion) {
        this.id = id;
        this.fechaTransaccion = fechaTransaccion;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.ventaId = ventaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getVentaId() {
        return ventaId;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    @Override
    public String toString() {
        return "TransaccionCaja{" +
                "id=" + id +
                ", fechaTransaccion=" + fechaTransaccion +
                ", tipoTransaccion='" + tipoTransaccion + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", ventaId=" + ventaId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionCaja that = (TransaccionCaja) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
