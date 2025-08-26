package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.MovimientoStock;


import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimientoStockDAO {

    public void guardarMovimiento(MovimientoStock movimientoStock){
        String sql = "INSERT INTO movimientos_stock(producto_id, tipo_movimiento, " +
                "cantidad, motivo, fecha_movimiento) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movimientoStock.getProductoId());
            pstmt.setString(2, movimientoStock.getTipoMovimiento());
            pstmt.setInt(3, movimientoStock.getCantidad());
            pstmt.setString(4, movimientoStock.getMotivo());
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(movimientoStock.getFechaMovimiento()));

            pstmt.executeUpdate();
        }catch (SQLException e){
            System.err.println("Error al guardar el movimiento de stock: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
