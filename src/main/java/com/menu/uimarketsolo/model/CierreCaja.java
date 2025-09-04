package com.menu.uimarketsolo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CierreCaja {

    private int id;
    private LocalDateTime fechaCierre;
    private BigDecimal totalSistemaEfectivo;
    private BigDecimal totalContadoEfectivo;
    private BigDecimal diferenciaEfectivo;
    private BigDecimal totalSistemaTarjeta;
    private BigDecimal totalContadoTarjeta;
    private BigDecimal diferenciaTarjeta;
    private int usuarioId;

    public CierreCaja(){

    }

    public CierreCaja(int id, LocalDateTime fechaCierre, BigDecimal totalSistemaEfectivo,
                      BigDecimal totalContadoEfectivo, BigDecimal diferenciaEfectivo, BigDecimal totalSistemaTarjeta,
                      BigDecimal totalContadoTarjeta, BigDecimal diferenciaTarjeta, int usuarioId) {
        this.id = id;
        this.fechaCierre = fechaCierre;
        this.totalSistemaEfectivo = totalSistemaEfectivo;
        this.totalContadoEfectivo = totalContadoEfectivo;
        this.diferenciaEfectivo = diferenciaEfectivo;
        this.totalSistemaTarjeta = totalSistemaTarjeta;
        this.totalContadoTarjeta = totalContadoTarjeta;
        this.diferenciaTarjeta = diferenciaTarjeta;
        this.usuarioId = usuarioId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getTotalSistemaEfectivo() {
        return totalSistemaEfectivo;
    }

    public void setTotalSistemaEfectivo(BigDecimal totalSistemaEfectivo) {
        this.totalSistemaEfectivo = totalSistemaEfectivo;
    }

    public BigDecimal getTotalContadoEfectivo() {
        return totalContadoEfectivo;
    }

    public void setTotalContadoEfectivo(BigDecimal totalContadoEfectivo) {
        this.totalContadoEfectivo = totalContadoEfectivo;
    }

    public BigDecimal getDiferenciaEfectivo() {
        return diferenciaEfectivo;
    }

    public void setDiferenciaEfectivo(BigDecimal diferenciaEfectivo) {
        this.diferenciaEfectivo = diferenciaEfectivo;
    }

    public BigDecimal getTotalSistemaTarjeta() {
        return totalSistemaTarjeta;
    }

    public void setTotalSistemaTarjeta(BigDecimal totalSistemaTarjeta) {
        this.totalSistemaTarjeta = totalSistemaTarjeta;
    }

    public BigDecimal getTotalContadoTarjeta() {
        return totalContadoTarjeta;
    }

    public void setTotalContadoTarjeta(BigDecimal totalContadoTarjeta) {
        this.totalContadoTarjeta = totalContadoTarjeta;
    }

    public BigDecimal getDiferenciaTarjeta() {
        return diferenciaTarjeta;
    }

    public void setDiferenciaTarjeta(BigDecimal diferenciaTarjeta) {
        this.diferenciaTarjeta = diferenciaTarjeta;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "CierreCaja{" +
                "id=" + id +
                ", fechaCierre=" + fechaCierre +
                ", totalSistemaEfectivo=" + totalSistemaEfectivo +
                ", totalContadoEfectivo=" + totalContadoEfectivo +
                ", diferenciaEfectivo=" + diferenciaEfectivo +
                ", totalSistemaTarjeta=" + totalSistemaTarjeta +
                ", totalContadoTarjeta=" + totalContadoTarjeta +
                ", diferenciaTarjeta=" + diferenciaTarjeta +
                ", usuarioId=" + usuarioId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == null) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CierreCaja that = (CierreCaja) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
