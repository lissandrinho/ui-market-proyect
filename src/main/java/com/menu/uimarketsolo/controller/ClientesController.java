package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.ClienteDAO;
import com.menu.uimarketsolo.model.Cliente;
import com.menu.uimarketsolo.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image; // Importación correcta de Image
import javafx.scene.image.ImageView; // ERROR 1: Importación correcta de ImageView
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class ClientesController {

    @FXML
    private TableView<Cliente> clientesTable;
    @FXML
    private TableColumn<Cliente, String> cedulaCol; // ERROR 2: Tipo de columna corregido a String
    @FXML
    private TableColumn<Cliente, String> nombreCol;
    @FXML
    private TableColumn<Cliente, String> apellidoCol;
    @FXML
    private TableColumn<Cliente, Void> colAcciones;


    @FXML
    private Label cedulaLabel;
    @FXML
    private Label nombreLabel;
    @FXML
    private Label apellidoLabel;
    @FXML
    private Label telefonoLabel;
    @FXML
    private Label emailLabel;

    @FXML
    private TextField buscarClienteField;
    @FXML
    private Button agregarClienteButton;
    @FXML
    private Button editarClienteButton;

    @FXML
    private HBox hboxBotonesAdmin;

    private ClienteDAO clienteDAO; // ERROR 3: Se eliminó la declaración duplicada

    @FXML
    public void initialize() {
        this.clienteDAO = new ClienteDAO();

        Usuario usuarioLogueado = SessionManager.getInstance().getUsuarioLogueado();
        if (usuarioLogueado != null && !usuarioLogueado.getRol().equalsIgnoreCase("admin")){
            hboxBotonesAdmin.setVisible(false);
            hboxBotonesAdmin.setManaged(false);

            colAcciones.setVisible(false);

        }

        configurarColumnas();
        configurarColumnaAcciones();
        configurarListeners();

        buscarClienteField.textProperty().addListener((observable, oldValue, newValue) -> {;
            filtrarClientes(newValue);
        });

        cargarClientes();

    }
    private void configurarColumnas() {
        cedulaCol.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoCol.setCellValueFactory(new PropertyValueFactory<>("apellido"));
    }
    private void configurarListeners() {
        editarClienteButton.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
        clientesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesCliente(newSelection)
        );
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.getAllClientes();
        clientesTable.setItems(FXCollections.observableArrayList(clientes));
    }

    private void mostrarDetallesCliente(Cliente cliente) {
        if (cliente != null) {
            cedulaLabel.setText(cliente.getCedula());
            nombreLabel.setText(cliente.getNombre());
            apellidoLabel.setText(cliente.getApellido());
            telefonoLabel.setText(cliente.getTelefono());
            emailLabel.setText(cliente.getEmail());
        } else {
            cedulaLabel.setText("-");
            nombreLabel.setText("-");
            apellidoLabel.setText("-");
            telefonoLabel.setText("-");
            emailLabel.setText("-");
        }
    }


    //LOGICA PARA LOS BOTONES
    @FXML
    private void handleAgregarCliente(){
        abrirDialogoCliente(null);
    }

    @FXML
    private void handleEditarCliente() {
        Cliente clienteSeleccionado = clientesTable.getSelectionModel().getSelectedItem();
        abrirDialogoCliente(clienteSeleccionado);
    }

    private void abrirDialogoCliente(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioCliente.fxml"));
            Parent root = loader.load();

            FormularioClienteController controller = loader.getController();

            controller.initData(cliente);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            cargarClientes();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Cliente, Void>, TableCell<Cliente, Void>> cellFactory = param -> {
            return new TableCell<>() {
                private final ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
                private final Button btnEliminar = new Button("", iconEliminar);

                {
                    btnEliminar.setStyle("-fx-background-color: transparent;");
                    btnEliminar.setCursor(Cursor.HAND);
                    iconEliminar.setFitHeight(20);
                    iconEliminar.setFitWidth(20);

                    btnEliminar.setOnAction(event -> {
                        Cliente cliente = getTableView().getItems().get(getIndex());
                        handleEliminarCliente(cliente);
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnEliminar);
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        };
        colAcciones.setCellFactory(cellFactory);
    }




    private void handleEliminarCliente(Cliente cliente) {
        if (cliente == null) return;
        if (cliente.getCedula().equals("00000000")) {
            mostrarAlerta("Acción no permitida", "No se puede eliminar al 'Consumidor Final'.");
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar a este cliente?");
        alertaConfirmacion.setContentText(cliente.getNombre() + " " + cliente.getApellido());

        alertaConfirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                clienteDAO.eliminarCliente(cliente.getCedula());
                cargarClientes();
            }
        });
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

    private void filtrarClientes(String textoBusqueda){
        if(textoBusqueda == null || textoBusqueda.isEmpty()){
            cargarClientes();
        }else {
            List<Cliente> clientesFiltrados = clienteDAO.buscarClientes(textoBusqueda);

            clientesTable.setItems(FXCollections.observableArrayList(clientesFiltrados));
        }
    }


}
