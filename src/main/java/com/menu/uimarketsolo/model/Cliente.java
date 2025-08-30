package com.menu.uimarketsolo.model;

import java.util.Objects;

public class Cliente {
    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private boolean isActivo;

    public Cliente(){

    }

    public Cliente(String cedula, String nombre, String apellido, String telefono, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public String getCedula() {
        return cedula;
    }
    public void setCedula(String cedula){
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public void setApellido (String apellido){
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail (String email){
        this.email = email;
    }

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + "(" + cedula + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return  Objects.equals(cedula, cliente.cedula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cedula);
    }
}
