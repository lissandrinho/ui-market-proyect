package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.Producto;


import java.sql.*;
import java.time.LocalDate;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class ProductoDAO {

    //Mostramos nuestra lista de productos dentro de nuestra tabla de productos.

    public List<Producto> getAllProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, m.nombre AS nombre_marca, c.nombre AS nombre_categoria, prov.nombre AS nombre_proveedor " +
                "FROM productos p " +
                "LEFT JOIN marcas m ON p.marca_id = m.id " +
                "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id " +
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
                producto.setCategoriaId(rs.getInt("categoria_id"));
                producto.setProveedorId(rs.getInt("proveedor_id"));
                producto.setNombreCategoria(rs.getString("nombre_categoria"));
                producto.setNombreProveedor(rs.getString("nombre_proveedor"));

                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    //Guardar los productos dentro del Formulario Agregar Nuevo producto.

    public void guardarProducto(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos(sku, nombre, descripcion, precio_venta, stock, imagen_path, marca_id, categoria_id, proveedor_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSku());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagenPath());
            pstmt.setInt(7, producto.getMarcaId());
            pstmt.setInt(8, producto.getCategoriaId());
            pstmt.setInt(9, producto.getProveedorId());

            pstmt.executeUpdate();
        }
    }

    //Actualizamos nuestro producto ya existente en nuestra base de datos
    //Esto se usa en el formulario para editar nuestro producto

    public void actualizarProducto(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET sku = ?, nombre = ?, descripcion = ?, precio_venta = ?, " +
                "stock = ?, imagen_path = ?, marca_id = ?, categoria_id = ?, proveedor_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSku());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagenPath());
            pstmt.setInt(7, producto.getMarcaId());
            pstmt.setInt(8, producto.getCategoriaId());
            pstmt.setInt(9, producto.getProveedorId());
            pstmt.setInt(10, producto.getId());

            pstmt.executeUpdate();
        }
    }

    //Eliminamos un producto seleccionado dentro de nuestra lista o tableview
    //Lo usamos dentro de la ventana principal de Stock o productos

    public void eliminarProducto(int id) throws SQLException {
        String sql = "UPDATE productos SET is_activo = false WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }


//Logica para darle funcion a nuestra busqueda a traves del controller
//Buscamos un producto en nuestro textfield que funciona como un filtro

    public List<Producto> buscarProducto(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, m.nombre AS nombre_marca, c.nombre AS nombre_categoria, prov.nombre AS nombre_proveedor " +
                "FROM productos p " +
                "LEFT JOIN marcas m ON p.marca_id = m.id " +
                "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id " +
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
                    producto.setCategoriaId(rs.getInt("categoria_id"));
                    producto.setProveedorId(rs.getInt("proveedor_id"));
                    producto.setNombreCategoria(rs.getString("nombre_categoria"));
                    producto.setNombreProveedor(rs.getString("nombre_proveedor"));
                    productos.add(producto);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public int getTotalProductosVendidosHoy(LocalDate fecha) {
        // Hacemos un JOIN entre detalle_ventas y ventas para poder filtrar por la fecha
        String sql = "SELECT SUM(dv.cantidad) FROM detalle_ventas dv " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE DATE(v.fecha_venta) = ?";
        int total = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<Producto> getProductosConStockBajo(int limiteStock) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock < ? AND is_activo = true ORDER BY stock ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limiteStock);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setStock(rs.getInt("stock"));

                    productos.add(producto);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    //validamos si existe un duplicado de Sku en nuestra base de datos
    public boolean existeSku(String sku) {
        String sql = "SELECT COUNT(*) FROM productos WHERE sku = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sku);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Devuelve true si el conteo es mayor a 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ajustarStock(int productoId, int nuevoStock, int stockAnterior, String motivo, int usuarioId) {
        String sqlUpdateStock = "UPDATE productos SET stock = ? WHERE id = ?";
        String sqlMovimientoStock = "INSERT INTO movimientos_stock(producto_id, tipo_movimiento, cantidad, motivo, fecha_movimiento, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            //Actualizar el stock del producto
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateStock)) {
                pstmtUpdate.setInt(1, nuevoStock);
                pstmtUpdate.setInt(2, productoId);
                pstmtUpdate.executeUpdate();
            }

            //Registrar el movimiento de stock
            int cantidadMovimiento = Math.abs(nuevoStock - stockAnterior);
            String tipoMovimiento;
            if (nuevoStock > stockAnterior) {
                tipoMovimiento = "AJUSTE_ENTRADA";
            } else {
                tipoMovimiento = "AJUSTE_SALIDA";
            }

            try (PreparedStatement pstmtMovimiento = conn.prepareStatement(sqlMovimientoStock)) {

                pstmtMovimiento.setInt(1, productoId);
                pstmtMovimiento.setString(2, tipoMovimiento);
                pstmtMovimiento.setInt(3, cantidadMovimiento);
                pstmtMovimiento.setString(4, motivo);
                pstmtMovimiento.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pstmtMovimiento.setInt(6, usuarioId);
                pstmtMovimiento.executeUpdate();
            }


            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("##### ERROR EN LA TRANSACCIÓN DE AJUSTE DE STOCK #####");
            if (conn != null) {
                try {
                    System.err.println("Intentando hacer rollback...");
                    conn.rollback();
                    System.err.println("Rollback exitoso.");
                } catch (SQLException ex) {
                    System.err.println("¡Error crítico al intentar hacer rollback!");
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Devolver al estado normal
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}