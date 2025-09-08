package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.CierreCajaDAO;
import com.menu.uimarketsolo.model.CierreCaja;
import com.menu.uimarketsolo.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private DatePicker datePickerFecha;
    @FXML private VBox vboxFecha;

    private CierreCajaDAO cierreCajaDAO;
    private BigDecimal totalSistemaEfectivo = BigDecimal.ZERO;
    private BigDecimal totalSistemaTarjeta = BigDecimal.ZERO;
    private Usuario usuarioLogueado;

    @FXML
    public void initialize() {
        this.cierreCajaDAO = new CierreCajaDAO();

        fechaDiaLabel.setText(LocalDate.now().toString());
        cargarTotalesDelSistema();

        this.usuarioLogueado = SessionManager.getInstance().getUsuarioLogueado();
        if (this.usuarioLogueado != null){
            configurarVistaPorRol();
        }

        fieldTotalEfectivo.textProperty().addListener((o, ov, nv) -> calcularDiferenciaTotal());
        fieldTotalTarjeta.textProperty().addListener((o, ov, nv) -> calcularDiferenciaTotal());
    }

    public void initData(Usuario usuario){
        this.usuarioLogueado = usuario;
        configurarVistaPorRol();
    }

    private void configurarVistaPorRol() {
        boolean esAdmin = usuarioLogueado.getRol().equalsIgnoreCase("admin");

        vboxFecha.setVisible(esAdmin);
        vboxFecha.setManaged(esAdmin);
        if (esAdmin) {
            datePickerFecha.setValue(LocalDate.now());
            datePickerFecha.valueProperty().addListener((obs, oldDate, newDate) ->
                    cargarTotalesDelSistema());
        }
        cargarTotalesDelSistema();
    }

    private void cargarTotalesDelSistema() {
        LocalDate fecha = datePickerFecha != null && datePickerFecha.getValue() != null
                ? datePickerFecha.getValue()
                : LocalDate.now();

        Map<String, BigDecimal> totalesHoy = cierreCajaDAO.getTotalesDelDia(fecha);

        // Obtenemos directamente los valores que devuelve el DAO
        totalSistemaEfectivo = totalesHoy.getOrDefault("Efectivo", BigDecimal.ZERO);
        totalSistemaTarjeta = totalesHoy.getOrDefault("Tarjeta", BigDecimal.ZERO);

        // Actualizamos los labels
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
       // Se inicializan las variables con un valor seguro (cero).
       BigDecimal contadoEfectivo = BigDecimal.ZERO;
       BigDecimal contadoTarjeta = BigDecimal.ZERO;

        try {

            String textoEfectivo = fieldTotalEfectivo.getText();
            if (!textoEfectivo.isEmpty()) {
                contadoEfectivo = new BigDecimal(textoEfectivo);
            }

            String textoTarjeta = fieldTotalTarjeta.getText();
            if (!textoTarjeta.isEmpty()) {
                contadoTarjeta = new BigDecimal(textoTarjeta);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Los montos ingresados deben ser números válidos.");
            return;
        }

        CierreCaja cierreCaja = new  CierreCaja();
        cierreCaja.setUsuarioId(SessionManager.getInstance().getUsuarioLogueado().getId());
        cierreCaja.setFechaCierre(LocalDateTime.now());
        cierreCaja.setTotalSistemaEfectivo(totalSistemaEfectivo);
        cierreCaja.setTotalContadoEfectivo(contadoEfectivo);
        cierreCaja.setDiferenciaEfectivo(contadoEfectivo.subtract(totalSistemaEfectivo));
        cierreCaja.setTotalSistemaTarjeta(totalSistemaTarjeta);
        cierreCaja.setTotalContadoTarjeta(contadoTarjeta);
        cierreCaja.setDiferenciaTarjeta(contadoTarjeta.subtract(totalSistemaTarjeta));

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
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
