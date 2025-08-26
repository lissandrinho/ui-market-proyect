package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MovimientoStockDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.MovimientoStock;
import com.menu.uimarketsolo.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class AjusteStockController {
    @FXML
    private Label ajusteLabel;
    @FXML
    private Label stockActualLabel;
    @FXML
    private TextField nuevoStockField;
    @FXML
    private TextArea motivoTxtArea;
    @FXML
    private Button guardarButton;
    @FXML
    private Button cancelarButton;


    private Producto productoAjustar;
    private ProductoDAO productoDAO;
    private MovimientoStockDAO movimientoStockDAO;

    public void initialize() {
        this.productoDAO = new ProductoDAO();
        this.movimientoStockDAO = new MovimientoStockDAO();

    }

    public void initData(Producto producto) {
        this.productoAjustar = producto;

        // --- PISTAS PARA DEPURAR ---
        System.out.println("initData llamado. Producto recibido: " + producto.getNombre());
        System.out.println("Label de ajuste (ajusteLabel) es: " + ajusteLabel);
        System.out.println("Label de stock (stockActualLabel) es: " + stockActualLabel);

        // Rellenamos los campos
        ajusteLabel.setText(producto.getNombre());
        stockActualLabel.setText(String.valueOf(producto.getStock()));
    }

    @FXML
    private void handleGuardarAjuste() {
        if (nuevoStockField.getText().isEmpty() || motivoTxtArea.getText().isEmpty()) {
            mostrarAlerta("Error", "Debés ingresar el nuevo stock y un motivo.");
            return;
        }

        int nuevoStock;
        try {
            nuevoStock = Integer.parseInt(nuevoStockField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El nuevo stock debe ser un número válido.");
            return;
        }

        int stockActual = productoAjustar.getStock();
        int cantidadAjustada = nuevoStock - stockActual;

        productoAjustar.setStock(nuevoStock);
        productoDAO.actualizarProducto(productoAjustar);

        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProductoId(productoAjustar.getId());
        movimiento.setTipoMovimiento(cantidadAjustada >= 0 ? "AJUSTE_POSITIVO" : "AJUSTE_NEGATIVO");
        movimiento.setCantidad(cantidadAjustada);
        movimiento.setMotivo(motivoTxtArea.getText());
        movimiento.setFechaMovimiento(LocalDateTime.now());


        movimientoStockDAO.guardarMovimiento(movimiento); //

        mostrarAlertaDeExito("Ajuste Exitoso", "El stock del producto se ha actualizado correctamente.");
        Stage stage = (Stage) nuevoStockField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelarButton() {
        Stage stage = (Stage) nuevoStockField.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void mostrarAlertaDeExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}


