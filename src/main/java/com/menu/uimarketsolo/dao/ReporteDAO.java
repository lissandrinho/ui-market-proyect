package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {

    public List<VentaResumen> getResumenVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<VentaResumen> resumenes = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha_venta, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) as cliente_nombre, " +
                "SUM(dv.cantidad) as total_items, v.total_venta " +
                "FROM ventas v " +
                "JOIN clientes c ON v.cliente_cedula = c.cedula " +
                "JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "WHERE DATE(v.fecha_venta) BETWEEN ? AND ? " +
                "GROUP BY v.id, v.fecha_venta, cliente_nombre, v.total_venta " +
                "ORDER BY v.fecha_venta DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VentaResumen resumen = new VentaResumen();
                    resumen.setFacturaNumero(rs.getInt("id"));
                    resumen.setFecha(rs.getTimestamp("fecha_venta").toLocalDateTime().toLocalDate());
                    resumen.setClienteNombre(rs.getString("cliente_nombre"));
                    resumen.setTotalItems(rs.getInt("total_items"));
                    resumen.setTotalVenta(rs.getBigDecimal("total_venta"));
                    resumenes.add(resumen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumenes;
    }

    public List<Producto> getProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.nombre, SUM(dv.cantidad) as total_vendido " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE DATE(v.fecha_venta) BETWEEN ? AND ? " +
                "GROUP BY p.nombre " +
                "ORDER BY total_vendido DESC " +
                "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setNombre(rs.getString("nombre"));
                    producto.setStock(rs.getInt("total_vendido"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    public List<MarcaReporteItem> getMarcasMasVendidas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<MarcaReporteItem> marcas = new ArrayList<>();
        String sql = "SELECT m.nombre, SUM(dv.cantidad) as total_vendido " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "JOIN marcas m ON p.marca_id = m.id " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE DATE(v.fecha_venta) BETWEEN ? AND ? " +
                "GROUP BY m.nombre " +
                "ORDER BY total_vendido DESC " +
                "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MarcaReporteItem marca = new MarcaReporteItem();
                    marca.setNombre(rs.getString("nombre"));
                    marca.setCantidadVendida(rs.getInt("total_vendido"));
                    marcas.add(marca);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marcas;
    }



    public Map<String, BigDecimal> getResumenPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, BigDecimal> resumen = new HashMap<>();

        String sql = "SELECT " +
                "  SUM(t.total_venta) as ingresos_totales, " +
                "  COUNT(t.id) as ventas_realizadas, " +
                "  SUM(t.total_items) as productos_vendidos " +
                "FROM ( " +
                "  SELECT v.id, v.total_venta, COALESCE(SUM(dv.cantidad), 0) as total_items " +
                "  FROM ventas v " +
                "  LEFT JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "  WHERE DATE(v.fecha_venta) BETWEEN ? AND ? " +
                "  GROUP BY v.id, v.total_venta " +
                ") as t";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    resumen.put("ingresosTotales", rs.getBigDecimal("ingresos_totales"));
                    resumen.put("ventasRealizadas", rs.getBigDecimal("ventas_realizadas"));
                    resumen.put("productosVendidos", rs.getBigDecimal("productos_vendidos"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumen;
    }

    public List<DetalleFacturaItem> getDetallePorFacturaId(int facturaId) {
        List<DetalleFacturaItem> detalles = new ArrayList<>();
        // Esta consulta une 3 tablas para obtener el nombre del producto
        String sql = "SELECT p.nombre, dv.cantidad, dv.precio_unitario, (dv.cantidad * dv.precio_unitario) as subtotal " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "WHERE dv.venta_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, facturaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFacturaItem item = new DetalleFacturaItem();
                    item.setProductoNombre(rs.getString("nombre"));
                    item.setCantidad(rs.getInt("cantidad"));
                    item.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));
                    detalles.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    public Venta getVentaPorId(int ventaId) {
        String sql = "SELECT v.*, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_completo_cliente " +
                     "FROM ventas v " +
                     "JOIN clientes c ON v.cliente_cedula = c.cedula " +
                     "WHERE v.id = ?";
        Venta venta = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    venta = new Venta();
                    venta.setId(rs.getInt("id"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_venta").toLocalDateTime());
                    venta.setVentaTotal(rs.getBigDecimal("total_venta"));
                    venta.setNombreCliente(rs.getString("nombre_completo_cliente"));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venta;
    }
}