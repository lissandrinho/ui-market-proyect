package com.menu.uimarketsolo.model;

import java.math.BigDecimal;

public class ReporteItem {

    private int facturaNumero;
    private String cliente;
    private String producto;
    private String marca;
    private int cantidad;
    private BigDecimal subtotal;
    private BigDecimal ventaTotal; // Nuevo campo para el total de la factura

    public ReporteItem() {
    }

    public ReporteItem(int facturaNumero, String cliente, String producto, String marca, int cantidad, BigDecimal subtotal, BigDecimal ventaTotal) {
        this.facturaNumero = facturaNumero;
        this.cliente = cliente;
        this.producto = producto;
        this.marca = marca;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.ventaTotal = ventaTotal;
    }

    // --- Getters y Setters ---
    public int getFacturaNumero() {
        return facturaNumero;
    }

    public void setFacturaNumero(int facturaNumero) {
        this.facturaNumero = facturaNumero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(BigDecimal ventaTotal) {
        this.ventaTotal = ventaTotal;
    }
}