package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.CierreCaja;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CierreCajaDAO {

    public Map<String, BigDecimal> getTotalesDelDia(LocalDate fecha) {
        Map<String, BigDecimal> totales = new HashMap<>();
        String sql = "SELECT tipo_transaccion, SUM(monto) as total FROM transacciones_caja " +
                "WHERE DATE(fecha_transaccion) = ? GROUP BY tipo_transaccion";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setDate(1, Date.valueOf(fecha));
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    String tipo = rs.getString("tipo_transaccion");
                    BigDecimal total = rs.getBigDecimal("total");
                    totales.put(tipo, total);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    return totales;
    }


    public void guardarArqueo(CierreCaja cierre){
        String sql = "INSERT INTO cierres_caja(" +
                "fecha_cierre, total_sistema_efectivo, total_contado_efectivo, diferencia_efectivo, " +
                "total_sistema_tarjeta, total_contado_tarjeta, diferencia_tarjeta, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setTimestamp(1, Timestamp.valueOf(cierre.getFechaCierre()));
            pstmt.setBigDecimal(2, cierre.getTotalSistemaEfectivo());
            pstmt.setBigDecimal(3, cierre.getTotalContadoEfectivo());
            pstmt.setBigDecimal(4, cierre.getDiferenciaEfectivo());
            pstmt.setBigDecimal(5, cierre.getTotalSistemaTarjeta());
            pstmt.setBigDecimal(6, cierre.getTotalContadoTarjeta());
            pstmt.setBigDecimal(7, cierre.getDiferenciaTarjeta());

            if(cierre.getUsuarioId() == 0){
                pstmt.setNull(8, Types.INTEGER);
            }else {
                pstmt.setInt(8, cierre.getUsuarioId());
            }

            pstmt.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }

    }
}