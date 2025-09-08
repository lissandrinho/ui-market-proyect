package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.CierreCaja;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CierreCajaDAO {

    public Map<String, BigDecimal> getTotalesDelDia(LocalDate fecha) {
        Map<String, BigDecimal> totales = new HashMap<>();
        totales.put("Efectivo", BigDecimal.ZERO);
        totales.put("Tarjeta", BigDecimal.ZERO);

        String sql = "SELECT " +
                "  CASE " +
                "    WHEN tipo_transaccion = 'Efectivo' THEN 'Efectivo' " +
                "    ELSE 'Tarjeta' " +
                "  END as metodo_pago, " +
                "  SUM(monto) as total " +
                "FROM transacciones_caja " +
                "WHERE DATE(fecha_transaccion) = ? AND cierre_id IS NULL " + // <-- ÚNICO CAMBIO
                "GROUP BY metodo_pago";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String metodo = rs.getString("metodo_pago");
                    BigDecimal total = rs.getBigDecimal("total");
                    if (total != null) {
                        totales.put(metodo, total);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totales;
    }

    public int guardarArqueo(CierreCaja cierre) {
        String sqlGuardar = "INSERT INTO cierres_caja(" +
                "fecha_cierre, total_sistema_efectivo, total_contado_efectivo, diferencia_efectivo, " +
                "total_sistema_tarjeta, total_contado_tarjeta, diferencia_tarjeta, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlMarcar = "UPDATE transacciones_caja SET cierre_id = ? WHERE cierre_id IS NULL AND DATE(fecha_transaccion) = ?";

        Connection conn = null;
        int nuevoCierreId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Guardar el registro del cierre y obtener su ID
            try (PreparedStatement pstmtGuardar = conn.prepareStatement(sqlGuardar, Statement.RETURN_GENERATED_KEYS)) {
                pstmtGuardar.setTimestamp(1, Timestamp.valueOf(cierre.getFechaCierre()));
                pstmtGuardar.setBigDecimal(2, cierre.getTotalSistemaEfectivo());
                pstmtGuardar.setBigDecimal(3, cierre.getTotalContadoEfectivo());
                pstmtGuardar.setBigDecimal(4, cierre.getDiferenciaEfectivo());
                pstmtGuardar.setBigDecimal(5, cierre.getTotalSistemaTarjeta());
                pstmtGuardar.setBigDecimal(6, cierre.getTotalContadoTarjeta());
                pstmtGuardar.setBigDecimal(7, cierre.getDiferenciaTarjeta());
                pstmtGuardar.setInt(8, cierre.getUsuarioId());
                pstmtGuardar.executeUpdate();

                try (ResultSet rs = pstmtGuardar.getGeneratedKeys()) {
                    if (rs.next()) {
                        nuevoCierreId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del cierre de caja.");
                    }
                }
            }

            // 2. Marcar todas las transacciones abiertas del día con el nuevo ID de cierre
            try (PreparedStatement pstmtMarcar = conn.prepareStatement(sqlMarcar)) {
                pstmtMarcar.setInt(1, nuevoCierreId);
                pstmtMarcar.setDate(2, Date.valueOf(cierre.getFechaCierre().toLocalDate()));
                pstmtMarcar.executeUpdate();
            }

            conn.commit(); // Confirmar transacción

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Deshacer todo si algo falla
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1; // Indicar que falló
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nuevoCierreId;
    }

    public List<CierreCaja> getHistorialCierres(LocalDate fechaInicio, LocalDate fechaFin) {
        List<CierreCaja> historial = new ArrayList<>();
        String sql = "SELECT cc.*, u.nombre_completo " +
                "FROM cierres_caja cc " +
                "JOIN usuarios u ON cc.usuario_id = u.id " +
                "WHERE DATE(cc.fecha_cierre) BETWEEN ? AND ? " +
                "ORDER BY cc.fecha_cierre DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CierreCaja cierre = new CierreCaja();
                    cierre.setId(rs.getInt("id"));
                    cierre.setFechaCierre(rs.getTimestamp("fecha_cierre").toLocalDateTime());
                    cierre.setDiferenciaEfectivo(rs.getBigDecimal("diferencia_efectivo"));
                    cierre.setDiferenciaTarjeta(rs.getBigDecimal("diferencia_tarjeta"));
                    cierre.setNombreUsuario(rs.getString("nombre_completo")); // <-- Guardamos el nombre del usuario
                    historial.add(cierre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historial;
    }



    public List<CierreCaja> getHistorialConDiferencia(LocalDate fechaInicio, LocalDate fechaFin) {
        List<CierreCaja> historial = new ArrayList<>();
        String sql = "SELECT cc.*, u.nombre_completo " +
                "FROM cierres_caja cc " +
                "JOIN usuarios u ON cc.usuario_id = u.id " +
                "WHERE DATE(cc.fecha_cierre) BETWEEN ? AND ? " +
                "AND (cc.diferencia_efectivo != 0 OR cc.diferencia_tarjeta != 0) " +
                "ORDER BY cc.fecha_cierre DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CierreCaja cierre = new CierreCaja();
                    cierre.setId(rs.getInt("id"));
                    cierre.setFechaCierre(rs.getTimestamp("fecha_cierre").toLocalDateTime());
                    cierre.setDiferenciaEfectivo(rs.getBigDecimal("diferencia_efectivo"));
                    cierre.setDiferenciaTarjeta(rs.getBigDecimal("diferencia_tarjeta"));
                    cierre.setNombreUsuario(rs.getString("nombre_completo")); // <-- Guardamos el nombre del usuario
                    historial.add(cierre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historial;
    }
}