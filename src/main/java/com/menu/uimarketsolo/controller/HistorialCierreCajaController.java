package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.CierreCajaDAO;
import com.menu.uimarketsolo.model.CierreCaja;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialCierreCajaController {

    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerhasta;
    @FXML private Button filtrarFechaButton;
    @FXML private TableView<CierreCaja> cierreCajaTableView;
    @FXML private TableColumn<CierreCaja, LocalDateTime> fechaCierreColumn;
    @FXML private TableColumn<CierreCaja, String> cierreUsuarioColumn;
    @FXML private TableColumn<CierreCaja, BigDecimal> diferenciaEfectivoColumn;
    @FXML private TableColumn<CierreCaja, BigDecimal> diferenciaTarjetaColumn;
    @FXML private CheckBox checkMostrarDiferencias;

    private CierreCajaDAO cierreCajaDAO;

    @FXML
    public void initialize(){
        this.cierreCajaDAO = new CierreCajaDAO();
        configurarTabla();


        datePickerhasta.setValue(LocalDate.now());
        datePickerDesde.setValue(LocalDate.now().minusMonths(1));


        handleFiltrarFecha();
    }
    private void configurarTabla() {
        fechaCierreColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCierre"));
        cierreUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        diferenciaEfectivoColumn.setCellValueFactory(new PropertyValueFactory<>("diferenciaEfectivo"));
        diferenciaTarjetaColumn.setCellValueFactory(new PropertyValueFactory<>("diferenciaTarjeta"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        fechaCierreColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : formatter.format(item));
            }
        });
    }


    @FXML
    private void handleFiltrarFecha() {
        LocalDate fechaInicio = datePickerDesde.getValue();
        LocalDate fechaFin = datePickerhasta.getValue();

        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error", "Por favor, seleccione un rango de fechas válido.");
            return;
        }

        List<CierreCaja> historial;
        if (checkMostrarDiferencias.isSelected()) {
            historial = cierreCajaDAO.getHistorialConDiferencia(fechaInicio, fechaFin);
        } else {
            historial = cierreCajaDAO.getHistorialCierres(fechaInicio, fechaFin);
        }

        cierreCajaTableView.setItems(FXCollections.observableArrayList(historial));
    }




    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}