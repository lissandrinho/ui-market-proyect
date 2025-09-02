package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.ProductoVenta;
import com.menu.uimarketsolo.model.Venta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDAO {

    public boolean guardarVenta(Venta venta, List<ProductoVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas(total_venta, cliente_cedula, fecha_venta) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas(venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
        String sqlMovimientoStock = "INSERT INTO movimientos_stock(producto_id, tipo_movimiento, cantidad, motivo," +
                " fecha_movimiento) VALUES (?, 'VENTA', ?, ?, ?)";
        String sqlTransaccionCaja = "INSERT INTO transacciones_caja(tipo_transaccion, " +
                "monto, venta_id) VALUES ('VENTA', ?, ?)";


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
                    pstmtMovimiento.executeUpdate();
                }

                try(PreparedStatement pstmtcaja = conn.prepareStatement(sqlTransaccionCaja)){
                    pstmtcaja.setBigDecimal(1, venta.getVentaTotal());
                    pstmtcaja.setInt(2, ventaId);
                    pstmtcaja.executeUpdate();

                }
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

}
