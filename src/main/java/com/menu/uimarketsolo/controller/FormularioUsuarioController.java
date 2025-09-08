package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.UsuarioDAO;
import com.menu.uimarketsolo.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioUsuarioController {


    @FXML private TextField fieldNombreUsuario;
    @FXML private TextField fieldNombreCompleto;
    @FXML private PasswordField fieldContrasena;
    @FXML private PasswordField fieldConfirmarContrasena;
    @FXML private ComboBox<String> comboBoxRol;
    @FXML private Button btnGuardar;


    private UsuarioDAO usuarioDAO;
    private Usuario usuarioAEditar;

    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDAO();


        comboBoxRol.setItems(FXCollections.observableArrayList("Administrador", "Cajero"));
    }

    public void initData(Usuario usuario) {
        this.usuarioAEditar = usuario;
        if (usuario != null) {
            fieldNombreUsuario.setText(usuario.getNombreUsuario());
            fieldNombreCompleto.setText(usuario.getNombreCompleto());
            comboBoxRol.setValue(usuario.getRol());
        }
    }


    @FXML
    private void handleGuardar() {

        String nombreUsuario = fieldNombreUsuario.getText();
        String nombreCompleto = fieldNombreCompleto.getText();
        String contrasena = fieldContrasena.getText();
        String confirmarContrasena = fieldConfirmarContrasena.getText();
        String rol = comboBoxRol.getValue();


        if (nombreUsuario.isEmpty() || nombreCompleto.isEmpty() || contrasena.isEmpty() || rol == null) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.");
            return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            mostrarAlerta("Error de Validación", "Las contraseñas no coinciden.");
            return;
        }

        if (usuarioAEditar == null) {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setContrasena(contrasena);
            nuevoUsuario.setRol(rol);
            usuarioDAO.guardarUsuario(nuevoUsuario);
            mostrarAlertaDeExito("Usuario Creado", "El nuevo usuario se ha registrado.");
        } else {

            usuarioAEditar.setNombreUsuario(nombreUsuario);
            usuarioAEditar.setNombreCompleto(nombreCompleto);
            usuarioAEditar.setRol(rol);
            if (!contrasena.isEmpty()) {
                usuarioAEditar.setContrasena(contrasena);
            }
            usuarioDAO.actualizarUsuario(usuarioAEditar);
            mostrarAlertaDeExito("Usuario Actualizado", "Los datos se han actualizado.");
        }

        cerrarVentana();
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
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


