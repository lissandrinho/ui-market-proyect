package com.menu.uimarketsolo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MovimientoStock {
    private int id;
    private int productoId;
    private LocalDateTime fechaMovimiento;
    private String tipoMovimiento;
    private int cantidad;
    private String motivo;
    private int UsuarioId;

    public MovimientoStock(){

    }

    public MovimientoStock(int id, int productoId, LocalDateTime fechaMovimiento, String tipoMovimiento, int cantidad, String motivo){
        this.id = id;
        this.productoId = productoId;
        this.fechaMovimiento = fechaMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getProductoId(){
        return productoId;
    }
    public void setProductoId(int productoId){
        this.productoId = productoId;
    }
    public LocalDateTime getFechaMovimiento(){
        return fechaMovimiento;
    }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento){
        this.fechaMovimiento = fechaMovimiento;
    }
    public String getTipoMovimiento(){
        return tipoMovimiento;
    }
    public void setTipoMovimiento(String tipoMovimiento){
        this.tipoMovimiento = tipoMovimiento;
    }
    public int getCantidad(){
        return cantidad;
    }
    public void setCantidad(int cantidad){
        this.cantidad = cantidad;
    }
    public String getMotivo(){
        return motivo;
    }
    public void setMotivo(String motivo){
        this.motivo = motivo;
    }
    public int getUsuarioId() {
        return UsuarioId;
    }
    public void setUsuarioId(int usuarioId) {
        this.UsuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "movimientoStock{" +
                "id=" + id +
                ", productoId=" + productoId +
                ", fechaMovimiento=" + fechaMovimiento +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", cantidad=" + cantidad +
                ", Motivo='" + motivo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoStock that = (MovimientoStock) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
