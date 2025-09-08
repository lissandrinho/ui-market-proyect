package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.dao.ReporteDAO;
import com.menu.uimarketsolo.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportesController {

    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerhasta;
    @FXML private Button filtrarFechaButton;

    @FXML private TableView<Producto> productoTableView;
    @FXML private TableColumn<Producto, String> ProductoMasVendidoColumn;
    @FXML private TableColumn<Producto, Integer> cantidadMasVendidaColumn;

    @FXML private TableView<MarcaReporteItem> marcasTableView;
    @FXML private TableColumn<MarcaReporteItem, String> marcaMasVendidaColumn;
    @FXML private TableColumn<MarcaReporteItem, Integer> marcaCantidadMasVendidaColumn;

    @FXML private TableView<VentaResumen> ventasTableView;
    @FXML private TableColumn<VentaResumen, Integer> facturaNumeroColumn;
    @FXML private TableColumn<VentaResumen, String> clienteColumn;
    @FXML private TableColumn<VentaResumen, LocalDate> fechaColumn;
    @FXML private TableColumn<VentaResumen, String> totalItemsColumn;
    @FXML private TableColumn<VentaResumen, BigDecimal> totalVentaColumn;

    @FXML private Label ventasRealizadasLabel;
    @FXML private Label cantidadVendidosLabel;
    @FXML private Label ingresosTotalesLabel;

    private ReporteDAO reporteDAO;

    @FXML
    public void initialize(){
        this.reporteDAO = new ReporteDAO();
        datePickerDesde.setValue(LocalDate.now());
        datePickerhasta.setValue(LocalDate.now());

        configurarTablaDetalle();
        configurarTablaProductos();
        configurarTablaMarcas();
        handleFiltrarFecha();
    }

    private void configurarTablaDetalle() {
        facturaNumeroColumn.setCellValueFactory(new PropertyValueFactory<>("facturaNumero"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        totalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        totalVentaColumn.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        fechaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
    }

    private void configurarTablaProductos() {
        ProductoMasVendidoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cantidadMasVendidaColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }


    private void configurarTablaMarcas() {
        marcaMasVendidaColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        marcaCantidadMasVendidaColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
    }

    @FXML
    private void handleFiltrarFecha() {
        LocalDate fechaInicio = datePickerDesde.getValue();
        LocalDate fechaFin = datePickerhasta.getValue();

        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error de Fechas", "Por favor, seleccione un rango de fechas válido.");
            return;
        }

        // LÓGICA PARA LOS LABELS DE RESUMEN
        Map<String, BigDecimal> resumen = reporteDAO.getResumenPorFechas(fechaInicio, fechaFin);
        ventasRealizadasLabel.setText(resumen.getOrDefault("ventasRealizadas", BigDecimal.ZERO).toPlainString());
        cantidadVendidosLabel.setText(resumen.getOrDefault("productosVendidos", BigDecimal.ZERO).toPlainString());
        ingresosTotalesLabel.setText(String.format("$ %.2f", resumen.getOrDefault("ingresosTotales", BigDecimal.ZERO)));

        // Cargar las tablas
        List<VentaResumen> ventasResumidas = reporteDAO.getResumenVentas(fechaInicio, fechaFin);
        ventasTableView.setItems(FXCollections.observableArrayList(ventasResumidas));
        
        List<Producto> productosMasVendidos = reporteDAO.getProductosMasVendidos(fechaInicio, fechaFin);
        productoTableView.setItems(FXCollections.observableArrayList(productosMasVendidos));

        List<MarcaReporteItem> marcasMasVendidas = reporteDAO.getMarcasMasVendidas(fechaInicio, fechaFin);
        marcasTableView.setItems(FXCollections.observableArrayList(marcasMasVendidas));
    }

    @FXML
    private void handleDetallesFacturaClick(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioDetallesFacturaView.fxml"));
            Parent root = loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detalles de Factura");
            dialogStage.initModality(Modality.APPLICATION_MODAL);


            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlehistorialCierreCajaClick() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioHistorialCierreCajaView.fxml"));
                Parent root = loader.load();


                Stage dialogStage = new Stage();
                dialogStage.setTitle("Historial de Cierre de Caja");
                dialogStage.initModality(Modality.APPLICATION_MODAL);


                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
