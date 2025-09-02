package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.TransaccionCaja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TransaccionCajaDAO {

    public void guardarTransaccion(TransaccionCaja transaccionCaja) {
        String sql = "INSERT INTO transacciones_caja(fecha_transaccion, tipo_transaccion, monto, descripcion, venta_id) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(transaccionCaja.getFechaTransaccion()));
            pstmt.setString(2, transaccionCaja.getTipoTransaccion());
            pstmt.setDouble(3, transaccionCaja.getMonto());
            pstmt.setString(4, transaccionCaja.getDescripcion());

            if (transaccionCaja.getVentaId() == 0) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(5, transaccionCaja.getVentaId());
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar la transacción de caja: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Posiblemente agregue mas metodos
}
