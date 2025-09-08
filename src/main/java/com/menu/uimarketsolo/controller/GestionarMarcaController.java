package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.ProveedorDAO;
import com.menu.uimarketsolo.dao.ProveedorMarcaDAO;
import com.menu.uimarketsolo.model.Marca;
import com.menu.uimarketsolo.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class GestionarMarcaController {
    @FXML
    private TextField fieldMarca;
    @FXML
    private Button btnGuardarMarca;
    @FXML
    private Button btnEliminarMarca;
    @FXML
    private Button btnNuevaMarca;
    @FXML
    private ListView<Marca> listVIewMarcas;
    @FXML
    private ComboBox<Proveedor> comboBoxProveedorAsignado;

    private MarcaDAO marcaDAO;
    private ProveedorDAO proveedorDAO;
    private ProveedorMarcaDAO proveedorMarcaDAO = new ProveedorMarcaDAO();



    @FXML
    private void initialize() {
        this.marcaDAO = new MarcaDAO();
        this.proveedorDAO = new ProveedorDAO();
        cargarMarcas();
        cargarProveedores();

        //cargamos la lista de marcas, activamos y desactivamos botones con la seleccion.
        listVIewMarcas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    // Si se selecciona una marca, se muestra en el campo de texto y se habilita el botón de eliminar
                    if (newValue != null) {
                        fieldMarca.setText(newValue.getNombre());
                        btnEliminarMarca.setDisable(false); // Activa el botón de eliminar


                        // Buscar y seleccionar el proveedor asignado a esta marca
                        Proveedor proveedorAsignado =
                                proveedorDAO.getProveedorPorMarca(newValue.getId());
                        if (proveedorAsignado != null) {
                            comboBoxProveedorAsignado.setValue(proveedorAsignado);
                        } else {
                            comboBoxProveedorAsignado.getSelectionModel().clearSelection();
                        }
                    } else {
                        btnEliminarMarca.setDisable(true); // Desactiva si no hay selección
                    }
                }
        );

        btnEliminarMarca.setDisable(true);

    }


    private void cargarMarcas(){
        List<Marca> marcas = marcaDAO.getAllMarcas();
        listVIewMarcas.setItems(FXCollections.observableArrayList(marcas)); // Ahora esto es correcto
    }

    @FXML
    private void handleNuevaMarca() {
        limpiarFormulario();
    }

    @FXML
    private void handleGuardarMarca() {
        String nombreMarca = fieldMarca.getText().trim();
        Proveedor proveedorSeleccionado = comboBoxProveedorAsignado.getSelectionModel().getSelectedItem();
        if (nombreMarca.isEmpty() || proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Debes ingresar un nombre para la marca y seleccionar un proveedor.");
            return;
        }

        Marca marcaSeleccionada = listVIewMarcas.getSelectionModel().getSelectedItem();


        if (marcaSeleccionada == null) {
            // MODO NUEVO: Validar si el nombre ya existe.
            if (marcaDAO.existeMarcaPorNombre(nombreMarca)) {
                mostrarAlerta("Error de Duplicado", "Ya existe una marca con el nombre '" + nombreMarca + "'.");
                return;
            }
            Marca nuevaMarca = new Marca();
            nuevaMarca.setNombre(nombreMarca);

            Marca marcaGuardada = marcaDAO.guardarMarcaYDevolver(nuevaMarca);
            if (marcaGuardada != null) {
                proveedorMarcaDAO.asignarMarcaAProveedor(proveedorSeleccionado.getId(), marcaGuardada.getId());
                mostrarAlertaDeExito("Éxito", "Nueva marca guardada y asignada correctamente.");
            }
        } else {
            // MODO EDICIÓN: Validar solo si el nombre ha cambiado y el nuevo nombre ya existe.
            if (!nombreMarca.equalsIgnoreCase(marcaSeleccionada.getNombre()) && marcaDAO.existeMarcaPorNombre(nombreMarca)) {
                mostrarAlerta("Error de Duplicado", "Ya existe otra marca con el nombre '" + nombreMarca + "'.");
                return;
            }
            marcaSeleccionada.setNombre(nombreMarca);
            marcaDAO.actualizarMarca(marcaSeleccionada);

            proveedorMarcaDAO.actualizarAsignacionProveedor(proveedorSeleccionado.getId(), marcaSeleccionada.getId());
            mostrarAlertaDeExito("Exito", "Marca actualizada correctamente");
        }

        cargarMarcas();
        limpiarFormulario();
    }

    @FXML
    private void handleEliminarMarca() {
        Marca marcaSeleccionada = listVIewMarcas.getSelectionModel().getSelectedItem();
        if(marcaSeleccionada == null){
            mostrarAlerta("Error", "Debes seleccionar una marca para eliminar");
            return;
        }

        if(marcaSeleccionada.getId() == 1){
            mostrarAlerta("Error", "No se puede eliminar 'Marca Generica'");
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminacion");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar la marca '" + marcaSeleccionada.getNombre() + "'?");
        alertaConfirmacion.setContentText("Todos los productos asociados a esta marca serán reasignados a 'Marca Genérica'.");

        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            proveedorMarcaDAO.eliminarAsignacionPorMarca(marcaSeleccionada.getId());
            marcaDAO.eliminarMarca(marcaSeleccionada.getId());
            cargarMarcas();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        listVIewMarcas.getSelectionModel().clearSelection();
        fieldMarca.clear();
        comboBoxProveedorAsignado.getSelectionModel().clearSelection();
        btnEliminarMarca.setDisable(true);
    }

    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorDAO.getAllProveedores();
        comboBoxProveedorAsignado.setItems(FXCollections.observableArrayList(proveedores));
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
