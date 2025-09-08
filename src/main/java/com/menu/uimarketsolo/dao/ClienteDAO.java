package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> getAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setCedula(rs.getString("cedula"));
                cliente.setNombre(rs.getString("nombre_cliente"));
                cliente.setApellido(rs.getString("apellido_cliente"));
                cliente.setTelefono(rs.getString("telefono_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public void guardarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes(cedula, nombre_cliente, apellido_cliente, telefono_cliente, email_cliente) " +
                "VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellido());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.executeUpdate();
        }
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre_cliente = ?, apellido_cliente = ?, telefono_cliente = ?, email_cliente = ? " +
                "WHERE cedula = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getCedula()); // El 'cedula' para el WHERE

            pstmt.executeUpdate();

        }
    }

    public void eliminarCliente(String cedula) {
        String sql = "UPDATE clientes SET is_activo = false WHERE cedula = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cliente getClientePorCedula(String cedula) {
        String sql = "SELECT * FROM clientes WHERE cedula = ? AND is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setCedula(rs.getString("cedula"));
                    cliente.setNombre(rs.getString("nombre_cliente"));
                    cliente.setApellido(rs.getString("apellido_cliente"));
                    cliente.setTelefono(rs.getString("telefono_cliente"));
                    cliente.setEmail(rs.getString("email_cliente"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> buscarClientes(String termino) {
        List<Cliente> clientes = new ArrayList<>();
        // CORRECCIÓN: Usamos los nombres de columna correctos (nombre_cliente, apellido_cliente)
        String sql = "SELECT * FROM clientes " +
                "WHERE (CONCAT(nombre_cliente, ' ', apellido_cliente) LIKE ? OR cedula LIKE ?) AND is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termino + "%");
            pstmt.setString(2, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setCedula(rs.getString("cedula"));
                    cliente.setNombre(rs.getString("nombre_cliente"));
                    cliente.setApellido(rs.getString("apellido_cliente"));
                    cliente.setTelefono(rs.getString("telefono_cliente"));
                    cliente.setEmail(rs.getString("email_cliente"));
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public boolean existeCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cedula = ? AND is_activo = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
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