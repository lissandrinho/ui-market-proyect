package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.ProveedorDAO;
import com.menu.uimarketsolo.model.Cliente;
import com.menu.uimarketsolo.model.Marca;
import com.menu.uimarketsolo.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;


public class ProveedorController {

    @FXML
    private Button agregarProveedorButton;
    @FXML
    private Button editarProveedorButton;

    @FXML
    private TextField buscarProveedorField;
    @FXML
    private Label nombreProveedorLabel;
    @FXML
    private Label contactoProveedorLabel;
    @FXML
    private Label telefonoProveedorLabel;
    @FXML
    private Label direccionProveedorLabel;
    @FXML
    private Label emailProveedorLabel;

    @FXML
    private TableView<Proveedor> proveedorTable;
    @FXML
    private TableColumn<Proveedor, String> proveedorNombreColumn;
    @FXML
    private TableColumn<Proveedor, String> proveedorContactoColumn;
    @FXML
    private TableColumn<Proveedor, Void> accionesColumn;
    @FXML
    private ListView<Marca> listViewMarcasAsignadas;

    private ProveedorDAO proveedorDAO;
    private MarcaDAO marcaDAO;


    @FXML
    private void initialize() {
        this.proveedorDAO = new ProveedorDAO();
        this.marcaDAO = new MarcaDAO();

        configurarColumnas();
        configurarColumnaAcciones();
        configurarListeners();

        cargarProveedores();

        buscarProveedorField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarProveedores(newValue);
        });

    }

    private void configurarColumnas() {
        proveedorNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        proveedorContactoColumn.setCellValueFactory(new PropertyValueFactory<>("contacto"));
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Proveedor, Void>, TableCell<Proveedor, Void>> cellFactory = param -> new TableCell<>() {
            private final ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
            private final Button btnEliminar = new Button("", iconEliminar);

            {
                btnEliminar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setCursor(Cursor.HAND);
                iconEliminar.setFitHeight(20);
                iconEliminar.setFitWidth(20);
                btnEliminar.setOnAction(event -> {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    handleEliminarProveedor(proveedor);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
                if (!empty) setAlignment(Pos.CENTER);
            }
        };
        accionesColumn.setCellFactory(cellFactory);
    }

    private void configurarListeners() {
        proveedorTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    mostrarDetallesProveedor(newSelection);

                    if (newSelection != null) {
                        cargarMarcasAsignadas(newSelection.getId());
                    } else {
                        listViewMarcasAsignadas.getItems().clear();
                    }
                }
        );

        editarProveedorButton.disableProperty().bind(
                proveedorTable.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorDAO.getAllProveedores();
        proveedorTable.setItems(FXCollections.observableArrayList(proveedores));
    }

    private void mostrarDetallesProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            nombreProveedorLabel.setText(proveedor.getNombre());
            contactoProveedorLabel.setText(proveedor.getContacto());
            telefonoProveedorLabel.setText(proveedor.getTelefono());
            direccionProveedorLabel.setText(proveedor.getDireccion());
            emailProveedorLabel.setText(proveedor.getEmail());
        } else {
            nombreProveedorLabel.setText("");
            contactoProveedorLabel.setText("");
            telefonoProveedorLabel.setText("");
            direccionProveedorLabel.setText("");
            emailProveedorLabel.setText("");
        }
    }


    @FXML
    private void handleAgregarProveedor() {
        abrirDialogoProveedor(null);
    }

    @FXML
    private void handleEditarProveedor() {
        Proveedor proveedorSeleccionado = proveedorTable.getSelectionModel().getSelectedItem();
        if (proveedorSeleccionado != null) {
            abrirDialogoProveedor(proveedorSeleccionado);
        }
    }

    @FXML
    private void handleAsignarMarca() {

    }

    private void abrirDialogoProveedor(Proveedor proveedor) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioProveedorView.fxml"));
            Parent root = loader.load();

            FormularioProveedorController controller = loader.getController();
            controller.initData(proveedor, proveedor == null ? "Nuevo Proveedor" : "Editar " +
                    "Proveedor");

            Stage dialogStage = new Stage();
            dialogStage.setTitle(proveedor == null ? "Agregar Proveedor" : "Editar Proveedor");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            cargarProveedores();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminarProveedor(Proveedor proveedor) {

        if (proveedor == null) return;
        if (proveedor.getId() == 1) { // Asumiendo que el ID 1 es el proveedor genérico
            mostrarAlerta("Acción no permitida", "No se puede eliminar al proveedor genérico.");
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar a este proveedor?");
        alertaConfirmacion.setContentText(proveedor.getNombre());

        alertaConfirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                proveedorDAO.eliminarProveedor(proveedor.getId());
                cargarProveedores();
            }
        });
    }



    private void filtrarProveedores(String textoBusqueda){
        if(textoBusqueda == null || textoBusqueda.isEmpty()){
            cargarProveedores();

        }
        else {
            List<Proveedor> proveedoresFiltrados = proveedorDAO.buscarProveedores(textoBusqueda);
            proveedorTable.setItems(FXCollections.observableArrayList(proveedoresFiltrados));
        }
    }
    private void cargarMarcasAsignadas(int proveedorId) {
        List<Marca> marcas = marcaDAO.getMarcasPorProveedor(proveedorId);
        listViewMarcasAsignadas.setItems(FXCollections.observableArrayList(marcas));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}