package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.model.Marca;
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

    private MarcaDAO marcaDAO;


    @FXML
    private void initialize() {
        this.marcaDAO = new MarcaDAO();
        cargarMarcas();

        //cargamos la lista de marcas, activamos y desactivamos botones con la seleccion.
        listVIewMarcas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    // Si se selecciona una marca, se muestra en el campo de texto y se habilita el botón de eliminar
                    if (newValue != null) {
                        fieldMarca.setText(newValue.getNombre());
                        btnEliminarMarca.setDisable(false); // Activa el botón de eliminar
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
        String nombreMarca = fieldMarca.getText();
        if(nombreMarca == null || nombreMarca.trim().isEmpty()){
            mostrarAlerta("Error", "Debes ingresar un nombre de marca");
            return;
        }

        Marca marcaSeleccionada = listVIewMarcas.getSelectionModel().getSelectedItem();

        if (marcaSeleccionada == null) {
            // Si no hay nada seleccionado, se crea una nueva marca
            Marca nuevaMarca = new Marca();
            nuevaMarca.setNombre(nombreMarca);
            marcaDAO.guardarMarca(nuevaMarca);
            mostrarAlertaDeExito("Exito", "Nueva marca guardada correctamente");
        } else {
            // Si hay una selección en la lista, entonces se actualiza la marca existente
            marcaSeleccionada.setNombre(nombreMarca);
            marcaDAO.actualizarMarca(marcaSeleccionada);
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
            marcaDAO.eliminarMarca(marcaSeleccionada.getId());
            cargarMarcas();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        listVIewMarcas.getSelectionModel().clearSelection();
        fieldMarca.clear();
        btnEliminarMarca.setDisable(true);
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
