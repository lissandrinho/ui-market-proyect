package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    //Obtenemos todas las marcas dentro de marcasComboBox
    public List<Marca> getAllMarcas() {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT * FROM marcas ORDER BY nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Marca marca = new Marca();
                marca.setId(rs.getInt("id"));
                marca.setNombre(rs.getString("nombre"));
                marcas.add(marca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marcas;
    }

    public Marca guardarMarcaYDevolver(Marca marca) {
        // Le pedimos a JDBC que nos devuelva las claves generadas (el nuevo ID)
        String sql = "INSERT INTO marcas(nombre) VALUES(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, marca.getNombre());
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Si se guardó, obtenemos el ID que se generó
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        marca.setId(rs.getInt(1)); // Asignamos el nuevo ID al objeto
                        return marca; // Devolvemos el objeto completo
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Devolvemos null si algo falló
    }

    public void actualizarMarca(Marca marca) {
        String sql = "UPDATE marcas SET nombre = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, marca.getNombre());
            pstmt.setInt(2, marca.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public void eliminarMarca(int marcaId) {
        if (marcaId == 1) {
            System.err.println("Error: No se puede eliminar la marca genérica.");
            return;
        }

        String sqlReasignar = "UPDATE productos SET marca_id = 1 WHERE marca_id = ?";
        String sqlEliminar = "DELETE FROM marcas WHERE id = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtReasignar = conn.prepareStatement(sqlReasignar)) {
                pstmtReasignar.setInt(1, marcaId);
                pstmtReasignar.executeUpdate();

            }

            try (PreparedStatement pstmtEliminar = conn.prepareStatement(sqlEliminar)) {
                pstmtEliminar.setInt(1, marcaId);
                pstmtEliminar.executeUpdate();

            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error en la transacción de eliminación de marca. Revirtiendo cambios.");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

    }

    public List<Marca> getMarcasPorProveedor(int proveedorId) {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT m.* FROM marcas m " +
                "JOIN proveedores_marcas pm ON m.id = pm.marca_id " +
                "WHERE pm.proveedor_id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proveedorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Marca marca = new Marca();
                    marca.setId(rs.getInt("id"));
                    marca.setNombre(rs.getString("nombre"));
                    marcas.add(marca);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marcas;
    }
}