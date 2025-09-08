package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Usuario;
import com.menu.uimarketsolo.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
           while(rs.next()){
               Usuario usuario = new Usuario();
               usuario.setId(rs.getInt("id"));
               usuario.setNombreUsuario(rs.getString("nombre_usuario"));
               usuario.setNombreCompleto(rs.getString("nombre_completo"));
               usuario.setRol(rs.getString("rol"));
               usuarios.add(usuario);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Dentro de la clase UsuarioDAO.java

    public Usuario verificarCredenciales(String nombreUsuario, String contrasenaIngresada) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //Si el usuario existe, obtenemos su contraseña encriptada
                    String contrasenaDeLaBD = rs.getString("contrasena");

                    //Comparamos la contraseña ingresada con la encriptada
                    if (PasswordUtil.checkPassword(contrasenaIngresada, contrasenaDeLaBD)) {
                        //Si coinciden, creamos y devolvemos el objeto Usuario
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                        usuario.setNombreCompleto(rs.getString("nombre_completo"));
                        usuario.setRol(rs.getString("rol"));
                        return usuario;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; //Devuelve null si el usuario no existe o la contraseña es incorrecta
    }

    public void guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nombre_usuario, contrasena, nombre_completo, rol) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordUtil.hashPassword(usuario.getContrasena());

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setString(4, usuario.getRol());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarUsuario(Usuario usuario){
        String sql = "UPDATE usuarios SET nombre_usuario = ?, nombre_completo = ?, rol = ?"
                + (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty() ? ", contrasena = ?" : "")
                + " WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getRol());
            int paramIndex = 4;
            if(usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()){
                pstmt.setString(paramIndex++, usuario.getContrasena()); // Recordatorio: Encriptar en un futuro
            }
            pstmt.setInt(paramIndex, usuario.getId());
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void eliminarUsuario(int id){
        String sql = "UPDATE usuarios SET is_activo = false WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
