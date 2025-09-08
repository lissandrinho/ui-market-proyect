package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.UsuarioDAO;
import com.menu.uimarketsolo.model.Usuario;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UsuariosController {

    @FXML private TableView<Usuario> tableViewUsuarios;
    @FXML private TableColumn<Usuario, String> usuariosColumn;
    @FXML private TableColumn<Usuario, String> nombreCompletoColumn;
    @FXML private TableColumn<Usuario, String> rolColumn;
    @FXML private Button NuevoUsuarioButton;
    @FXML private Button EditarUsuarioButton;
    @FXML private Button EliminarUsuarioButton;
    @FXML private Label nombreUsuarioLabel;
    @FXML private Label nombreCompletoLabel;
    @FXML private Label rolUsuarioLabel;
    @FXML private Label tituloLabel;
    @FXML private Button cerrarSesionButton;
    @FXML private HBox hboxOcultarBotones;
    @FXML private VBox panelDetallesUsuario;

    private Usuario usuarioActual;
    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDAO();

    }

    public void initData(Usuario usuario){
        this.usuarioActual = usuario;
        if (this.usuarioActual == null){
            System.err.println("Error: No se recibió ningún usuario en la ventana de gestión.");
            return;
        }
        configurarVistaSegunRol();
    }

    private void configurarVistaSegunRol(){
        nombreUsuarioLabel.setText(usuarioActual.getNombreUsuario());
        nombreCompletoLabel.setText(usuarioActual.getNombreCompleto());
        rolUsuarioLabel.setText(usuarioActual.getRol());

        boolean esAdmin = usuarioActual.getRol().equalsIgnoreCase("admin");
        tableViewUsuarios.setVisible(esAdmin);
        tableViewUsuarios.setManaged(esAdmin);
        hboxOcultarBotones.setVisible(esAdmin);
        hboxOcultarBotones.setManaged(esAdmin);
        panelDetallesUsuario.setVisible(!esAdmin);
        panelDetallesUsuario.setManaged(!esAdmin);

        if (esAdmin){
            tituloLabel.setText("Gestión de Usuarios - Administrador");
            configurarColumnas();
            cargarUsuarios();

            EditarUsuarioButton.disableProperty().bind(tableViewUsuarios.getSelectionModel().selectedItemProperty().isNull());
            EliminarUsuarioButton.disableProperty().bind(tableViewUsuarios.getSelectionModel().selectedItemProperty().isNull());

            Platform.runLater(() -> {
                Stage stage = (Stage) tituloLabel.getScene().getWindow();
                stage.setHeight(600);
                stage.setWidth(800);
            });

        } else {
            tituloLabel.setText("Mi Perfil");

            Platform.runLater(() -> {
                Stage stage = (Stage) tituloLabel.getScene().getWindow();
                stage.setHeight(350);
                stage.setWidth(500);
            });
        }

    }

    private void configurarColumnas(){
        usuariosColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        nombreCompletoColumn.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    private void cargarUsuarios(){
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
        tableViewUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }




    @FXML
    private void handleNuevoUsuario() {
        abrirDialogoUsuario(null);
    }

    private void abrirDialogoUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioUsuarioView.fxml"));
            Parent root = loader.load();

            FormularioUsuarioController controller = loader.getController();
            controller.initData(usuario);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            cargarUsuarios();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleEditarUsuario() {
        Usuario usuarioSeleccionado = tableViewUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            abrirDialogoUsuario(usuarioSeleccionado);
        }
        else {
            mostrarAlerta("Error", "Por favor, selecciona un usuario para editar.");
        }
    }


    @FXML
    private void handleEliminarUsuario() {
        Usuario usuarioSeleccionado = tableViewUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) return;
        if (usuarioSeleccionado.getId() == usuarioActual.getId()){
            mostrarAlerta("Error", "No puedes eliminar tu propio usuario.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar al usuario '"
                + usuarioSeleccionado.getNombreUsuario() + "'?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioDAO.eliminarUsuario(usuarioSeleccionado.getId());
            cargarUsuarios();
        }


    }



    private boolean cerrarSesionSolicitado = false;
    @FXML
    private void handleCerrarSesion() {
        cerrarSesionSolicitado = true;
        Stage stage = (Stage) cerrarSesionButton.getScene().getWindow();
        stage.close();
    }
    public boolean isCerrarSesionSolicitado(){
        return cerrarSesionSolicitado;
    }

    @FXML
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

