package com.menu.uimarketsolo.dao;

import com.menu.uimarketsolo.database.DatabaseConnection;
import com.menu.uimarketsolo.model.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO {

    /**
     * Guarda un solo detalle de venta en la base de datos.
     * Este método se usaría dentro de la transacción de guardarVenta en VentaDAO.
     * @param detalle el objeto DetalleVenta a guardar.
     */
    public void guardarDetalleVenta(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_ventas(venta_id, producto_id," +
                " cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,detalle.getVentaId());
            pstmt.setInt(2,detalle.getProductoId());
            pstmt.setInt(3,detalle.getCantidad());
            pstmt.setDouble(4,detalle.getPrecioUnitario());
            pstmt.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Obtiene todos los detalles (productos) de una venta específica.
     * Muy útil para reimprimir un recibo o ver el detalle de una venta en el historial.
     * @param ventaId el ID de la venta de la que se quieren obtener los detalles.
     * @return una lista con los detalles de la venta.
     */

    public List<DetalleVenta> getDetallesPorVentaId(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ventas WHERE venta_id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,ventaId);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setId(rs.getInt("id"));
                    detalle.setVentaId(rs.getInt("venta_id"));
                    detalle.setProductoId(rs.getInt("producto_id"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    detalles.add(detalle);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return detalles;
    }


}
