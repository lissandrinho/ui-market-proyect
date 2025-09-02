package com.menu.uimarketsolo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Producto {
    private int id;
    private String sku;
    private String descripcion;
    private String nombre;
    private double precioVenta;
    private int stock;
    private LocalDateTime fechaCreacion;
    private String imagenPath;
    private int marcaId;
    private int proveedorId;
    private String nombreMarca;
    private String nombreProveedor;


    // Constructor vacío
    public Producto() {
    }

    public Producto(int id, String sku, String nombre, double precioVenta, int stock,
                    LocalDateTime fechaCreacion, String descripcion ,String imagenPath, int marca_id, int proveedor_id){

        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.fechaCreacion = fechaCreacion;
        this.imagenPath = imagenPath;
        this.marcaId = marca_id;
        this.proveedorId = proveedor_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    public int getMarcaId() {
        return marcaId;
    }
    public void setMarcaId(int marcaId) {
        this.marcaId = marcaId;
    }

    public int getProveedorId() {
        return proveedorId; }

    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    @Override
    public String toString() {
        return nombre + " (SKU: " + sku + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producto productos = (Producto) o;
        return id == productos.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
