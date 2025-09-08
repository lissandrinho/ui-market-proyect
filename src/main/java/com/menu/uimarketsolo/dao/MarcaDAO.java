package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    public List<Marca> getAllMarcas() {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT * FROM marcas WHERE is_activo = true ORDER BY nombre";
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
        String sql = "INSERT INTO marcas(nombre) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, marca.getNombre());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    marca.setId(generatedKeys.getInt(1));
                    return marca;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public void eliminarMarca(int id) {

        String sqlReasignarProductos = "UPDATE productos SET marca_id = 1 WHERE marca_id = ?";
        String sqlEliminarMarca = "UPDATE marcas SET is_activo = false WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            //Reasignar productos a la marca genérica (ID 1)
            try (PreparedStatement pstmtReasignar = conn.prepareStatement(sqlReasignarProductos)) {
                pstmtReasignar.setInt(1, id);
                pstmtReasignar.executeUpdate();
            }

            //Marcar la marca como inactiva
            try (PreparedStatement pstmtEliminar = conn.prepareStatement(sqlEliminarMarca)) {
                pstmtEliminar.setInt(1, id);
                pstmtEliminar.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Marca> getMarcasPorProveedor(int proveedorId) {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT m.* FROM marcas m JOIN proveedores_marcas pm ON m.id = pm.marca_id WHERE pm.proveedor_id = ? AND m.is_activo = true";
        try (Connection conn = DatabaseConnection.getConnection();
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

    public boolean existeMarcaPorNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM marcas WHERE nombre = ? AND is_activo = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}