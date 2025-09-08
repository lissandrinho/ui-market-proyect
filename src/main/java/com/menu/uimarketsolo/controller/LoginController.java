package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.UsuarioDAO;
import com.menu.uimarketsolo.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usuarioField;
    @FXML private TextField passwordField;
    @FXML private Button ingresarButton;
    @FXML private Label mensajeErrorLabel;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDAO();
        mensajeErrorLabel.setVisible(false);
    }


    @FXML
    private void handleIngresar() {
        String nombreUsuario = usuarioField.getText();
        String contrasena = passwordField.getText();

        if(nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            mensajeErrorLabel.setText("Por favor, complete todos los campos.");
            mensajeErrorLabel.setVisible(true);
            return;
        }

        Usuario usuario = usuarioDAO.verificarCredenciales(nombreUsuario, contrasena);
        if(usuario != null) {
            SessionManager.getInstance().login(usuario);
            abrirVentanaPrincipal();
        } else {
            mensajeErrorLabel.setText("Usuario o contraseña incorrectos.");
            mensajeErrorLabel.setVisible(true);
        }
    }

    private void abrirVentanaPrincipal(){
        try{
            Stage stage = (Stage) ingresarButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo" +
                    "/view/MainView.fxml"));
            Parent root = loader.load();
            Stage stagePrincipal = new Stage();
            stagePrincipal.setTitle("Gestion UI Market");
            stagePrincipal.setScene(new Scene(root, 1420, 780));
            stagePrincipal.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        mensajeErrorLabel.setText(mensaje);
        mensajeErrorLabel.setVisible(true);
    }
}
