package com.menu.uimarketsolo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class VentaResumen {
    private int facturaNumero;
    private LocalDate fecha;
    private String clienteNombre;
    private int totalItems;
    private BigDecimal totalVenta;

    public VentaResumen(){

    }
    public VentaResumen(int facturaNumero, LocalDate fecha, String clienteNombre, int totalItems, BigDecimal totalVenta) {
        this.facturaNumero = facturaNumero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.totalItems = totalItems;
        this.totalVenta = totalVenta;
    }

    public int getFacturaNumero() {
        return facturaNumero;
    }

    public void setFacturaNumero(int facturaNumero) {
        this.facturaNumero = facturaNumero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VentaResumen that = (VentaResumen) o;
        return facturaNumero == that.facturaNumero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facturaNumero);
    }
}
