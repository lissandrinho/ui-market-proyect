package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Producto;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    //Mostramos nuestra lista de productos dentro de nuestra tabla de productos.

    public List<Producto> getAllProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql =    "SELECT p.*, m.nombre AS nombre_marca " +
                        "FROM productos p " +
                        "LEFT JOIN marcas m ON p.marca_id = m.id " +
                        "WHERE p.is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();

                producto.setId(rs.getInt("id"));
                producto.setSku(rs.getString("sku"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecioVenta(rs.getDouble("precio_venta"));
                producto.setStock(rs.getInt("stock"));
                producto.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                producto.setImagenPath(rs.getString("imagen_path"));
                producto.setMarcaId(rs.getInt("marca_id"));
                producto.setNombreMarca(rs.getString("nombre_marca"));

                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    //Guardar los productos dentro del Formulario Agregar Nuevo producto.

    public void guardarProducto(Producto producto) {
        String sql = "INSERT INTO productos(sku, nombre, descripcion, precio_venta, stock, imagen_path, marca_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSku());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagenPath());
            pstmt.setInt(7, producto.getMarcaId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Actualizamos nuestro producto ya existente en nuestra base de datos
    //Esto se usa en el formulario para editar nuestro producto

    public void actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET sku = ?, nombre = ?, descripcion = ?, precio_venta = ?, " +
                "stock = ?, imagen_path = ?, marca_id = ?, proveedor_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSku());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagenPath());
            pstmt.setInt(7, producto.getMarcaId());
            pstmt.setInt(8, producto.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Eliminamos un producto seleccionado dentro de nuestra lista o tableview
    //Lo usamos dentro de la ventana principal de Stock o productos

    public void eliminarProducto(int id) {
        String sql = "UPDATE productos SET is_activo = false WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//Logica para darle funcion a nuestra busqueda a traves del controller
//Buscamos un producto en nuestro textfield que funciona como un filtro

    public List<Producto> buscarProducto(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, m.nombre AS nombre_marca " +
                "FROM productos p " +
                "LEFT JOIN marcas m ON p.marca_id = m.id " +
                "WHERE (p.sku LIKE ? OR p.nombre LIKE ?) AND p.is_activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termino + "%");
            pstmt.setString(2, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("id"));
                    producto.setSku(rs.getString("sku"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecioVenta(rs.getDouble("precio_venta"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    producto.setImagenPath(rs.getString("imagen_path"));
                    producto.setMarcaId(rs.getInt("marca_id"));
                    producto.setNombreMarca(rs.getString("nombre_marca"));
                    productos.add(producto);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

}
