package com.menu.uimarketsolo.model;

public class MarcaReporteItem {
    private String nombre;
    private int cantidadVendida;

    public MarcaReporteItem() {

    }

    public MarcaReporteItem(String nombre, int cantidadVendida) {
        this.nombre = nombre;
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}