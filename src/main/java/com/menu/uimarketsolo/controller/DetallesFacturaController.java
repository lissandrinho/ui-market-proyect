package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.ClienteDAO;
import com.menu.uimarketsolo.dao.ReporteDAO;
import com.menu.uimarketsolo.model.Cliente;
import com.menu.uimarketsolo.model.DetalleFacturaItem;
import com.menu.uimarketsolo.model.Venta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetallesFacturaController {

    @FXML private TextField buscarClienteField;
    @FXML private Label clienteFacturaLabel;
    @FXML private Label fechaFacturaLabel;
    @FXML private Label totalFacturaLabel;

    @FXML private TableView<DetalleFacturaItem> facturaTableView;
    @FXML private TableColumn<DetalleFacturaItem, String> productoColumn;
    @FXML private TableColumn<DetalleFacturaItem, Integer> cantidadColumn;
    @FXML private TableColumn<DetalleFacturaItem, BigDecimal> precioUnitarioColumn;
    @FXML private TableColumn<DetalleFacturaItem, BigDecimal> subtotalColumn;

    private ReporteDAO reporteDAO;
    private ClienteDAO clienteDAO;

    @FXML
    public void initialize(){
        this.reporteDAO = new ReporteDAO();
        this.clienteDAO = new ClienteDAO();

        productoColumn.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        precioUnitarioColumn.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

    }

    @FXML
    private void handleBuscarFactura(){
        if (buscarClienteField.getText().isEmpty()){
            mostrarAlerta("Error", "Debes ingresar un número de factura.");
            return;
        }

        try {
            int facturaId = Integer.parseInt(buscarClienteField.getText());

            Venta venta = reporteDAO.getVentaPorId(facturaId);

            if (venta == null){
                mostrarAlerta("No Encontrada", "No se encontró ninguna venta con el número " + facturaId);
                limpiarCampos();
                return;
            }

            List<DetalleFacturaItem> detalles = reporteDAO.getDetallePorFacturaId(facturaId);

            clienteFacturaLabel.setText(venta.getNombreCliente());
            fechaFacturaLabel.setText(venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            totalFacturaLabel.setText(String.format("$ %.2f", venta.getVentaTotal()));
            facturaTableView.setItems(FXCollections.observableArrayList(detalles));

        }catch (NumberFormatException e){
            mostrarAlerta("Error de Formato", "El número de factura debe ser un número válido.");
            e.printStackTrace();
        }

    }

    private void limpiarCampos() {
        clienteFacturaLabel.setText("");
        fechaFacturaLabel.setText("");
        totalFacturaLabel.setText("");
        facturaTableView.getItems().clear();
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
