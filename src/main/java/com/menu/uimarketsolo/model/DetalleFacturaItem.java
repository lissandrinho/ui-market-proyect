package com.menu.uimarketsolo.model;

import java.math.BigDecimal;
import java.util.Objects;

public class DetalleFacturaItem {

    private String productoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleFacturaItem() {

    }

    public DetalleFacturaItem(String productoNombre, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getProductoNombre() {
        return productoNombre;
    }
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DetalleFacturaItem that = (DetalleFacturaItem) o;
        return cantidad == that.cantidad && Objects.equals(productoNombre, that.productoNombre) && Objects.equals(precioUnitario, that.precioUnitario) && Objects.equals(subtotal, that.subtotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoNombre, cantidad, precioUnitario, subtotal);
    }
}