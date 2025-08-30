package com.menu.uimarketsolo.model;

import java.util.Objects;

public class Marca {
    private int id;
    private String nombre;

    public Marca(){

    }

    public Marca(int id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Marca marcas = (Marca) o;
        return id == marcas.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
