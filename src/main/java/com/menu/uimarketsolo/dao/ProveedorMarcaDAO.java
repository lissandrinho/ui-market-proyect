package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProveedorMarcaDAO {

    public void asignarMarcaAProveedor(int proveedorId, int marcaId) {
        String sql = "INSERT INTO proveedores_marcas (proveedor_id, marca_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proveedorId);
            pstmt.setInt(2, marcaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarAsignacionProveedor(int proveedorId, int marcaId) {

        String sqlDelete = "DELETE FROM proveedores_marcas WHERE marca_id = ?";

        String sqlInsert = "INSERT INTO proveedores_marcas (proveedor_id, marca_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, marcaId);
                pstmtDelete.executeUpdate();
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setInt(1, proveedorId);
                pstmtInsert.setInt(2, marcaId);
                pstmtInsert.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAsignacionPorMarca(int marcaId) {
        String sql = "DELETE FROM proveedores_marcas WHERE marca_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, marcaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAsignacionesPorProveedor(int proveedorId) {
        String sql = "DELETE FROM proveedores_marcas WHERE proveedor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proveedorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}