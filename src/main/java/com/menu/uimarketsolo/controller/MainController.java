package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.model.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Properties;

public class MainController {

    @FXML
    private BorderPane mainPane;
    @FXML private Button btnReportes;
    @FXML private Button btnProveedores;





    @FXML
    public void initialize() {
        Usuario usuarioLogueado = SessionManager.getInstance().getUsuarioLogueado();
        if(usuarioLogueado != null && !usuarioLogueado.getRol().equalsIgnoreCase("Administrador")){
            btnProveedores.setVisible(false);
            btnProveedores.setManaged(false);
            btnReportes.setVisible(false);
            btnReportes.setManaged(false);
        }

        cargarVista("InicioView.fxml");
        verificarActualizaciones();

    }



    @FXML
    private void handleVentasClick(){
        System.out.println("Boton de ventas presionado. Cargando vista...");
        cargarVista("VentasView.fxml");
    }

    private void cargarVista(String nombreFXML){
        try {

            URL url = getClass().getResource("/com/menu/uimarketsolo/view/" + nombreFXML);
            if (url == null) {
                System.out.println("No se pudo encontrar el archivo FXML: " + nombreFXML);
                return;
            }

            Parent vista = FXMLLoader.load(url);
            mainPane.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProductosClick(){
        System.out.println("Boton de Stock presionado. Cargando vista...");
        cargarVista("ProductosView.fxml");
    }

    @FXML
    private void handleClientesClick(){
        System.out.println("Boton de Clientes presionado. Cargando vista...");
        cargarVista("ClientesView.fxml");
    }

    @FXML
    private void handleReportesClick(){
        System.out.println("Boton de Clientes presionado. Cargando vista...");
        cargarVista("ReportesView.fxml");
    }

    @FXML
    private void handleProveedoresClick(){
        System.out.println("Boton de Proveedores presionado. Cargando vista...");
        cargarVista("ProveedorView.fxml");
    }
    @FXML
    private void handleInicioClick(){
        System.out.println("Boton de Inicio presionado. Cargando vista...");
        cargarVista("InicioView.fxml");
    }

    @FXML
    private void handleUsuarioClick(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo" +
                    "/view/MenuUsuarioView.fxml"));
            Parent root = loader.load();

            UsuariosController controller = loader.getController();
            controller.initData(SessionManager.getInstance().getUsuarioLogueado());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Menú de Usuario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());

            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.isCerrarSesionSolicitado()){
                cerrarSesionYvolverAlLogin();
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void cerrarSesionYvolverAlLogin(){
        try {
            Stage stageActual = (Stage) mainPane.getScene().getWindow();

            stageActual.close();

            SessionManager.getInstance().logOut();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("UI-Market - Inicio de Sesión");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCierreCajaClick() {
        try {
            // Carga el archivo FXML del diálogo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/CierreCajaView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador para pasarle los datos del usuario logueado
            CierreCajaController controller = loader.getController();
            controller.initData(SessionManager.getInstance().getUsuarioLogueado());

            // Crea una nueva ventana (Stage) para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cierre de Caja");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());

            // Establece la escena y muestra la ventana
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarActualizaciones() {
        // La URL Raw de  Gist
        final String urlVersion = "https://gist.githubusercontent.com/lissandrinho/53642329628d37e9e8001bc5ab3081d4/raw/0dc596eb2edd625f02f0c927be69aa374e786dbd/version.json";

        // Se ejecuta en un hilo separado para no congelar la interfaz
        new Thread(() -> {
            try {
                // Carga la versión actual desde el archivo version.properties
                Properties props = new Properties();
                InputStream is = getClass().getResourceAsStream("/version.properties");
                if (is == null) {
                    System.err.println("No se pudo encontrar el archivo version.properties.");
                    return;
                }
                props.load(is);
                final String versionActual = props.getProperty("app.version");

                // Realiza la petición a internet
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlVersion)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();

                // Extracción simple de los datos del JSON
                final String ultimaVersion = json.split("\"latestVersion\": \"")[1].split("\"")[0];
                final String urlDescarga = json.split("\"downloadUrl\": \"")[1].split("\"")[0];

                if (versionActual != null && !versionActual.equals(ultimaVersion)) {
                    // Si hay una nueva versión, muestra una alerta en el hilo de la UI
                    Platform.runLater(() -> {
                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Actualización Disponible");
                        alerta.setHeaderText("¡Nueva versión " + ultimaVersion + " disponible!");
                        alerta.setContentText("¿Te gustaría descargarla ahora?");
                        ButtonType botonDescargar = new ButtonType("Descargar");
                        alerta.getButtonTypes().setAll(botonDescargar, ButtonType.CANCEL);

                        Optional<ButtonType> resultado = alerta.showAndWait();
                        if (resultado.isPresent() && resultado.get() == botonDescargar) {
                            try {
                                Desktop.getDesktop().browse(new URI(urlDescarga));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("No se pudo verificar actualizaciones (puede ser falta de internet).");
            }
        }).start();
    }


}