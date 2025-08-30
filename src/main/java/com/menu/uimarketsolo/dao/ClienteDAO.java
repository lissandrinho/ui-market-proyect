package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Cliente;

import java.lang.ref.Cleaner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

//    Obtenemos todos los clientes activos de nuestra base de datos
//    Devuelve la lista completa de todos los clientes
    public List<Cliente> getAllClientes() {

        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE is_activo = true";

        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()){
                Cliente cliente = new Cliente();
                cliente.setCedula(rs.getString("cedula"));
                cliente.setNombre(rs.getString("nombre_cliente"));     // El nombre va en el nombre
                cliente.setApellido(rs.getString("apellido_cliente")); // El apellido va en el apellido
                cliente.setTelefono(rs.getString("telefono_cliente")); // Sin el "_cliente"
                cliente.setEmail(rs.getString("email_cliente"));       // Sin el "_cliente"
                cliente.setActivo(rs.getBoolean("is_activo"));
                clientes.add(cliente);

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return clientes;

    }



     //Actualiza un cliente existente en la base de datos.

    public void actualizarCliente(Cliente cliente){
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ? WHERE cedula = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getCedula());

            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();


        }
    }

    public void eliminarCliente(Cliente cedula){
        String sql = "UPDATE clientes SET is_activo = false WHERE cedula = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, cedula.getCedula());
            stmt.executeUpdate();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }



}