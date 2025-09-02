package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.ProveedorDAO;
import com.menu.uimarketsolo.model.Proveedor;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioProveedorController {
    @FXML
    private Label labelFormularioProveedor;
    @FXML
    private TextField fieldNombreEmpresa;
    @FXML
    private TextField fieldNombreContacto;
    @FXML
    private TextField fieldTelefonoProveedor;
    @FXML
    private TextField fieldDireccion;
    @FXML
    private TextField fieldEmailProveedor;
    @FXML private Button btnGuardarProveedor;

    private ProveedorDAO proveedorDAO;
    private Proveedor proveedorAEditar;

    @FXML
    private void initialize() {
        this.proveedorDAO = new ProveedorDAO();
    }

    public void initData(Proveedor proveedor, String titulo) {
        this.proveedorAEditar = proveedor;
        this.labelFormularioProveedor.setText(titulo);

        if (proveedor != null) {

            fieldNombreEmpresa.setText(proveedor.getNombre());
            fieldNombreContacto.setText(proveedor.getContacto());
            fieldTelefonoProveedor.setText(proveedor.getTelefono());
            fieldEmailProveedor.setText(proveedor.getEmail());
            fieldDireccion.setText(proveedor.getDireccion());
        }
    }

    @FXML
    private void handleGuardar(){
        String nombre = fieldNombreEmpresa.getText();
        String contacto = fieldNombreContacto.getText();
        String telefono = fieldTelefonoProveedor.getText();
        String email = fieldEmailProveedor.getText();
        String direccion = fieldDireccion.getText();

        if (nombre.isEmpty() || contacto.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.");
            return;
        }

        if (proveedorAEditar == null) {
           Proveedor nuevoProveedor = new Proveedor();
               nuevoProveedor.setNombre(nombre);
               nuevoProveedor.setContacto(contacto);
               nuevoProveedor.setTelefono(telefono);
               nuevoProveedor.setEmail(email);
               nuevoProveedor.setDireccion(direccion);
               proveedorDAO.guardarProveedor(nuevoProveedor);
               mostrarAlertaDeExito("Proveedor Guardado", "El nuevo proveedor se ha registrado correctamente.");

        } else {
            proveedorAEditar.setNombre(nombre);
            proveedorAEditar.setContacto(contacto);
            proveedorAEditar.setTelefono(telefono);
            proveedorAEditar.setEmail(email);
            proveedorAEditar.setDireccion(direccion);
            proveedorDAO.actualizarProveedor(proveedorAEditar);
        }

        cerrarVentana();
    }

    @FXML
    private void handleCancelar(){
        cerrarVentana();
    }

    private void cerrarVentana(){
        Stage stage = (Stage) labelFormularioProveedor.getScene().getWindow();
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
