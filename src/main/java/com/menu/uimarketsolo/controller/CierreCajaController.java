package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.CierreCajaDAO;
import com.menu.uimarketsolo.model.CierreCaja;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


public class CierreCajaController {
    @FXML private Label fechaDiaLabel;
    @FXML private Label totalEfectivoLabel;
    @FXML private Label totalTarjetaLabel;
    @FXML private TextField fieldTotalEfectivo;
    @FXML private TextField fieldTotalTarjeta;
    @FXML private Label diferenciaLabel;
    @FXML private Button btnConfirmarCierre;

    private CierreCajaDAO cierreCajaDAO;
    private BigDecimal totalSistemaEfectivo = BigDecimal.ZERO;
    private BigDecimal totalSistemaTarjeta = BigDecimal.ZERO;


    @FXML
    public void initialize() {
        this.cierreCajaDAO = new CierreCajaDAO();

        fechaDiaLabel.setText(LocalDate.now().toString());
        cargarTotalesDelSistema();

        fieldTotalEfectivo.textProperty().addListener((o, ov, nv) -> calcularDiferenciaTotal());
        fieldTotalTarjeta.textProperty().addListener((o, ov, nv) -> calcularDiferenciaTotal());
    }

    private void cargarTotalesDelSistema(){

        Map<String, BigDecimal> totalesHoy = cierreCajaDAO.getTotalesDelDia(LocalDate.now());
        totalSistemaEfectivo = totalesHoy.getOrDefault("Efectivo", BigDecimal.ZERO);

        BigDecimal totalDebito = totalesHoy.getOrDefault("Tarjeta de Débito", BigDecimal.ZERO);
        BigDecimal totalCredito = totalesHoy.getOrDefault("Tarjeta de Crédito", BigDecimal.ZERO);
        totalSistemaTarjeta = totalDebito.add(totalCredito);

        totalEfectivoLabel.setText(String.format("$ %.2f", totalSistemaEfectivo));
        totalTarjetaLabel.setText(String.format("$ %.2f", totalSistemaTarjeta));

        calcularDiferenciaTotal();

    }

    private void calcularDiferenciaTotal() {

        BigDecimal contadoEfectivo = BigDecimal.ZERO;
        BigDecimal contadoTarjeta = BigDecimal.ZERO;
        try {
            if (!fieldTotalEfectivo.getText().isEmpty()) {
                contadoEfectivo = new BigDecimal(fieldTotalEfectivo.getText());
            }
            if (!fieldTotalTarjeta.getText().isEmpty()) {
                contadoTarjeta = new BigDecimal(fieldTotalTarjeta.getText());
            }
        }catch (NumberFormatException e){
            diferenciaLabel.setText("Valor inválido");
            diferenciaLabel.setTextFill(Color.ORANGE);
            return;
        }

        BigDecimal diferenciaEfectivo = contadoEfectivo.subtract(totalSistemaEfectivo);
        BigDecimal diferenciaTarjeta = contadoTarjeta.subtract(totalSistemaTarjeta);

        BigDecimal diferenciaTotal = diferenciaEfectivo.add(diferenciaTarjeta);

        diferenciaLabel.setText(String.format("$ %.2f", diferenciaTotal));
        if(diferenciaTotal.compareTo(BigDecimal.ZERO) == 0){
            diferenciaLabel.setStyle("-fx-text-fill: green;");
        } else {
            diferenciaLabel.setStyle("-fx-text-fill: red;");
        }
    }


    @FXML
    private void handleConfirmarCierre() {
       BigDecimal contadoEfectivo = new BigDecimal(fieldTotalEfectivo.getText());
         BigDecimal contadoTarjeta = new BigDecimal(fieldTotalTarjeta.getText());

        CierreCaja cierreCaja = new  CierreCaja();
        cierreCaja.setFechaCierre(LocalDateTime.now());
        cierreCaja.setTotalSistemaEfectivo(totalSistemaEfectivo);
        cierreCaja.setTotalContadoEfectivo(contadoEfectivo);
        cierreCaja.setDiferenciaEfectivo(contadoEfectivo.subtract(totalSistemaEfectivo));
        cierreCaja.setTotalSistemaTarjeta(totalSistemaTarjeta);
        cierreCaja.setTotalContadoTarjeta(contadoTarjeta);
        cierreCaja.setDiferenciaTarjeta(contadoTarjeta.subtract(totalSistemaTarjeta));
        //cierreCaja.setUsuarioId();

        cierreCajaDAO.guardarArqueo(cierreCaja);

        mostrarAlertaDeExito("Cierre Exitoso", "El cierre de caja se ha guardado correctamente.");
        Stage stage = (Stage) btnConfirmarCierre.getScene().getWindow();
        stage.close();
    }



    private void mostrarAlertaDeExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

