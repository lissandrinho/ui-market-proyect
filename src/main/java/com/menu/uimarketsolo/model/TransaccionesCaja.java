package com.menu.uimarketsolo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TransaccionesCaja {
    private int id;
    private LocalDateTime fechaTransaccion;
    private String tipoTransaccion;
    private double monto;
    private String descripcion;

    // Constructor vacío
    public TransaccionesCaja() {
    }

    public TransaccionesCaja(int id, LocalDateTime fechaTransaccion, String tipoTransaccion, double monto, String descripcion) {
        this.id = id;
        this.fechaTransaccion = fechaTransaccion;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.descripcion = descripcion;
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

    @Override
    public String toString() {
        return "TransaccionesCaja{" +
                "id=" + id +
                ", fechaTransaccion=" + fechaTransaccion +
                ", tipoTransaccion='" + tipoTransaccion + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionesCaja that = (TransaccionesCaja) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
