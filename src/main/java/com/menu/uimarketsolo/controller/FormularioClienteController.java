package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.ClienteDAO;
import com.menu.uimarketsolo.model.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class FormularioClienteController {

    @FXML private TextField fieldCedula;
    @FXML private TextField fieldNombre;
    @FXML private TextField fieldApellido;
    @FXML private TextField fieldTelefono;
    @FXML private TextField fieldEmail;
    @FXML private Button btnGuardarCliente;
    @FXML private Label labelFormularioCliente;


    private ClienteDAO clienteDAO;
    private Cliente clienteAEditar;

    @FXML
    public void initialize() {
        this.clienteDAO = new ClienteDAO();



    }


    public void initData(Cliente cliente) {
        this.clienteAEditar = cliente;
        if (cliente != null) {
            this.labelFormularioCliente.setText("Editar Cliente");

            fieldCedula.setText(cliente.getCedula());
            fieldNombre.setText(cliente.getNombre());
            fieldApellido.setText(cliente.getApellido());
            fieldTelefono.setText(cliente.getTelefono());
            fieldEmail.setText(cliente.getEmail());

            fieldCedula.setEditable(false);
        } else {
            this.labelFormularioCliente.setText("Nuevo Cliente");
        }
    }

    @FXML
    private void handleGuardar() {
        String cedula = fieldCedula.getText();
        String nombre = fieldNombre.getText();
        String apellido = fieldApellido.getText();
        String telefono = fieldTelefono.getText();
        String email = fieldEmail.getText();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mostrarAlerta("Error de Validación", "Los campos Cédula, Nombre y Apellido son obligatorios.");
            return;
        }

        try {
            if (clienteAEditar == null) {
                // MODO AGREGAR
                if (clienteDAO.existeCedula(cedula)) {
                    mostrarAlerta("Error de Duplicado", "La cédula '" + cedula + "' ya está registrada para otro cliente.");
                    return;
                }

                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setCedula(cedula);
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setApellido(apellido);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(email);
                clienteDAO.guardarCliente(nuevoCliente);
                mostrarAlertaDeExito("Cliente Guardado", "El nuevo cliente se ha registrado correctamente.");
            } else {
                // MODO EDICIÓN
                clienteAEditar.setNombre(nombre);
                clienteAEditar.setApellido(apellido);
                clienteAEditar.setTelefono(telefono);
                clienteAEditar.setEmail(email);
                clienteDAO.actualizarCliente(clienteAEditar);
                mostrarAlertaDeExito("Cliente Actualizado", "Los datos se han actualizado correctamente.");
            }
            cerrarVentana();
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudo guardar el cliente. Por favor, revise la conexión o los datos ingresados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }



    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardarCliente.getScene().getWindow();
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