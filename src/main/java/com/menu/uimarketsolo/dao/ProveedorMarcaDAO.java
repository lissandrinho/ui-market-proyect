package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProveedorMarcaDAO {

    public void asignarMarcaAProveedor(int proveedorId, int marcaId) {
        String sql = "INSERT INTO proveedores_marcas (proveedor_id, marca_id) VALUES (?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, proveedorId);
            pstmt.setInt(2, marcaId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void quitarTodasLasMarcasDeProveedor(int proveedorId) {
        String sql = "DELETE FROM proveedores_marcas WHERE proveedor_id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, proveedorId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
