package com.menu.uimarketsolo.model;

import java.util.Objects;

public class ProductoVenta {
    private Producto producto;
    private int productoId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String sku;

    public ProductoVenta() {
    }

    public ProductoVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.productoId = producto.getId();
        this.nombre = producto.getNombre();
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.subtotal = producto.getPrecioVenta() * cantidad;
        this.sku = producto.getSku();
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precioUnitario * cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "ProductoVenta{" +
                "productoId=" + productoId +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductoVenta that = (ProductoVenta) o;
        return productoId == that.productoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoId);
    }
}
