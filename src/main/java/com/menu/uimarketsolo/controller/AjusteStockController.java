package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.ProductoDAO;

import com.menu.uimarketsolo.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


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

    public void initialize() {
        this.productoDAO = new ProductoDAO();
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

        try {
            int nuevoStock = Integer.parseInt(nuevoStockField.getText());

            if (nuevoStock < 0) {
                mostrarAlerta("Error de Validación", "El stock no puede ser un número negativo.");
                return;
            }

            String motivo = motivoTxtArea.getText();
            int stockAnterior = productoAjustar.getStock();

            if (nuevoStock == stockAnterior) {
                mostrarAlerta("Información", "El nuevo stock es igual al stock actual. No se realizó ningún ajuste.");
                return;
            }

            int usuarioId = SessionManager.getInstance().getUsuarioLogueado().getId();
            
            boolean exito = productoDAO.ajustarStock(
                    productoAjustar.getId(),
                    nuevoStock,
                    stockAnterior,
                    motivo,
                    usuarioId
            );

            if (exito) {
                mostrarAlertaDeExito("Éxito", "El stock ha sido ajustado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo realizar el ajuste de stock. Revise la conexión a la base de datos.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El nuevo stock debe ser un número válido.");
        }
    }

    @FXML
    private void handleCancelarButton() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) guardarButton.getScene().getWindow();
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
