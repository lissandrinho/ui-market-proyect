package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.ProductoVenta;
import com.menu.uimarketsolo.model.Usuario;
import com.menu.uimarketsolo.model.Venta;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

public class VentaDAO {


    public boolean guardarVenta(Venta venta, List<ProductoVenta> detalles, String metodoPago) {
        String sqlVenta = "INSERT INTO ventas(total_venta, cliente_cedula, fecha_venta, usuario_id) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas(venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
        String sqlMovimientoStock = "INSERT INTO movimientos_stock(producto_id, tipo_movimiento, " +
                "cantidad, motivo, fecha_movimiento, usuario_id) VALUES (?, 'VENTA', ?, ?, ?, ?)";
        String sqlTransaccionCaja = "INSERT INTO transacciones_caja(tipo_transaccion, monto, venta_id) VALUES (?, ?, ?)";


        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int ventaId;
            try(PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta,
                    Statement.RETURN_GENERATED_KEYS)){
                pstmtVenta.setBigDecimal(1, venta.getVentaTotal());
                pstmtVenta.setString(2, venta.getClienteCedula());
                pstmtVenta.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                pstmtVenta.setInt(4, venta.getUsuarioId());
                pstmtVenta.executeUpdate();

                try(ResultSet rs = pstmtVenta.getGeneratedKeys()){
                    if(rs.next()){
                        ventaId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la venta.");
                    }

                }
            }
            for (ProductoVenta item : detalles){
                //Guardar detalle de venta
                try(PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)){
                    pstmtDetalle.setInt(1, ventaId);
                    pstmtDetalle.setInt(2, item.getProducto().getId());
                    pstmtDetalle.setInt(3, item.getCantidad());
                    pstmtDetalle.setDouble(4, item.getPrecioUnitario());
                    pstmtDetalle.executeUpdate();
                }

                // Actualizar stock
                try (PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {
                    pstmtUpdateStock.setInt(1, item.getCantidad());
                    pstmtUpdateStock.setInt(2, item.getProducto().getId());
                    pstmtUpdateStock.executeUpdate();
                }

                // Registrar movimiento de stock
                try (PreparedStatement pstmtMovimiento = conn.prepareStatement(sqlMovimientoStock)) {
                    pstmtMovimiento.setInt(1, item.getProducto().getId());
                    pstmtMovimiento.setInt(2, item.getCantidad());
                    pstmtMovimiento.setString(3, "Venta #" + ventaId);
                    pstmtMovimiento.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    pstmtMovimiento.setInt(5, venta.getUsuarioId());
                    pstmtMovimiento.executeUpdate();
                }
            }

                //Registrar el movimiento en caja
            try(PreparedStatement pstmtCaja = conn.prepareStatement(sqlTransaccionCaja)){
                pstmtCaja.setString(1, metodoPago);
                pstmtCaja.setBigDecimal(2, venta.getVentaTotal());
                pstmtCaja.setInt(3, ventaId);
                pstmtCaja.executeUpdate();
            }

            conn.commit();
            return true;

        }catch (SQLException e){
            e.printStackTrace();

            try{
                if(conn != null) conn.rollback();

            }catch (SQLException ex){
                ex.printStackTrace();
            }

            return false;

        }finally {
            try{
                if(conn != null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }

    public int getUltimoIdVenta(){
        String sql = "SELECT MAX(id) AS ultimo_id FROM ventas";
        int ultimoId = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                ultimoId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ultimoId;
    }

    public BigDecimal getTotalVentasHoy(LocalDate fecha) {
        String sql = "SELECT SUM(total_venta) FROM ventas WHERE DATE(fecha_venta) = ?";
        BigDecimal total = BigDecimal.ZERO;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal(1);
                    if (resultado != null) {
                        total = resultado;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<Venta> getUltimasVentas(int limite) {
        List<Venta> ventas = new ArrayList<>();
        // Se modifica la consulta para unir con la tabla de clientes y obtener el nombre
        String sql = "SELECT v.*, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_completo_cliente " +
                     "FROM ventas v " +
                     "JOIN clientes c ON v.cliente_cedula = c.cedula " +
                     "ORDER BY v.fecha_venta DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = new Venta();
                    venta.setId(rs.getInt("id"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_venta").toLocalDateTime());
                    venta.setVentaTotal(rs.getBigDecimal("total_venta"));
                    venta.setClienteCedula(rs.getString("cliente_cedula"));
                    venta.setUsuarioId(rs.getInt("usuario_id"));
                    venta.setNombreCliente(rs.getString("nombre_completo_cliente")); // Se asigna el nombre
                    ventas.add(venta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    public Map<String, BigDecimal> getVentasUltimos7Dias(){
        Map<String, BigDecimal> ventasSemanales = new LinkedHashMap<>();
        String sql = "SELECT DATE(fecha_venta) as dia, SUM(total_venta) as total " +
                "FROM ventas " +
                "WHERE fecha_venta >= CURDATE() - INTERVAL 6 DAY " +
                "GROUP BY DATE(fecha_venta) " +
                "ORDER BY dia ASC;";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()){
                    Date fechaSql = rs.getDate("Dia");
                    String diaSemana = fechaSql.toLocalDate().getDayOfWeek()
                            .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

                    BigDecimal totalDia = rs.getBigDecimal("Total");
                    ventasSemanales.put(diaSemana, totalDia);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ventasSemanales;
    }


}
