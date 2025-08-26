package com.menu.uimarketsolo.model;

import java.util.Objects;

public class DetalleVenta {
    private int id;
    private int ventaId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;

 public DetalleVenta(){

 }

 public DetalleVenta(int id, int ventaId, int productoId, int cantidad, double precioUnitario){
     this.id = id;
     this.ventaId = ventaId;
     this.productoId = productoId;
     this.cantidad = cantidad;
     this.precioUnitario = precioUnitario;
 }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }
    public int getVentaId() {
        return ventaId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    public int getProductoId() {
        return productoId;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public int getCantidad() {
        return cantidad;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", productoId=" + productoId +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta that = (DetalleVenta) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
