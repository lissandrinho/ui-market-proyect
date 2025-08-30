package com.menu.uimarketsolo.model;

import java.util.Objects;

public class Proveedor {
        private int id;
        private String nombre;
        private String contacto;
        private String telefono;
        private String email;
        private String direccion;
        private boolean is_activo;

        //constructor vacio
        public Proveedor(){

        }

    public Proveedor(int id, String nombre, String contacto, String telefono, String email, String direccion, boolean is_activo) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.is_activo = is_activo;
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isIs_activo() {
        return is_activo;
    }

    public void setIs_activo(boolean is_activo) {
        this.is_activo = is_activo;
    }


    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", contacto='" + contacto + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", is_activo='" + is_activo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return id == proveedor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
