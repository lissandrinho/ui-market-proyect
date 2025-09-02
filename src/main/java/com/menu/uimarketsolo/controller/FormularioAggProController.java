package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Marca;
import com.menu.uimarketsolo.model.Producto;
import com.menu.uimarketsolo.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FormularioAggProController {

    @FXML
    private TextField fieldSKU;
    @FXML
    private TextField fieldNombrePro;
    @FXML
    private TextField fieldPrecioUni;
    @FXML
    private TextField fieldCantidadPro;
    @FXML
    private TextArea areaDescripcion;
    @FXML
    private ImageView imagenPreview;
    @FXML
    private Button btnGuardarProducto;
    @FXML
    private Button btnCancelarOpe;

    @FXML
    private ComboBox<Marca> marcasComboBox;

    private ProductoDAO productosDAO;
    private MarcaDAO marcaDAO;
    private File archivoImagenSeleccionada;

    public void initialize(){
        this.productosDAO = new ProductoDAO();
        this.marcaDAO = new MarcaDAO();
        cargarMarcas();
    }

    @FXML
    private void handleGuardar() {
        String sku = fieldSKU.getText();
        String nombre = fieldNombrePro.getText();
        String descripcion = areaDescripcion.getText();
        String precioTexto = fieldPrecioUni.getText();
        String cantidadTexto = fieldCantidadPro.getText();
        Marca marcaSeleccionada = marcasComboBox.getSelectionModel().getSelectedItem();
        //Proveedor proveedorSeleccionado = proveedorComboBox.getSelectionModel().getSelectedItem();

        if (sku.isEmpty() || nombre.isEmpty() || precioTexto.isEmpty() || cantidadTexto.isEmpty() || marcaSeleccionada == null) {
            mostrarAlerta("Error", "Todos los campos, incluyendo marca y proveedor, son obligatorios.");
            return;
        }

        marcaSeleccionada = marcasComboBox.getSelectionModel().getSelectedItem();
        if(marcaSeleccionada == null){
            mostrarAlerta("Error", "Debes seleccionar una marca.");
            return;
        }

        double precioVenta;
        int stock;

        try {
            precioVenta = Double.parseDouble(precioTexto);
            stock = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El precio y la cantidad deben ser números válidos.");
            return;
        }

        String nombreImagen = null;
        if(archivoImagenSeleccionada != null){
            try{
                Path destino = Paths.get("src/main/resources/images/productos");
                if (!Files.exists(destino)){
                    Files.createDirectories(destino);
                }

                Path archivoDestino = destino.resolve(archivoImagenSeleccionada.getName());
                Files.copy(archivoImagenSeleccionada.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                nombreImagen = archivoImagenSeleccionada.getName();
            }catch (IOException e){
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo guardar la imagen del producto.");
            }
        }

        Producto nuevoProducto = new Producto();
        nuevoProducto.setSku(sku);
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setPrecioVenta(precioVenta);
        nuevoProducto.setStock(stock);
        nuevoProducto.setImagenPath(nombreImagen);
        nuevoProducto.setMarcaId(marcaSeleccionada.getId());
        //nuevoProducto.setProveedorId(proveedorSeleccionado.getId());

        productosDAO.guardarProducto(nuevoProducto);
        mostrarAlertaDeExito("Producto Guardado", "El nuevo producto se ha agregado al inventario correctamente.");

        Stage stage = (Stage) btnGuardarProducto.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handleSeleccionarImagen(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );
        archivoImagenSeleccionada = fileChooser.showOpenDialog(imagenPreview.getScene().getWindow());
        if (archivoImagenSeleccionada != null){
            Image image = new Image(archivoImagenSeleccionada.toURI().toString());
            imagenPreview.setImage(image);
        }
    }

    private void cargarMarcas(){
        List<Marca> marcas =  marcaDAO.getAllMarcas();
        marcasComboBox.setItems(FXCollections.observableArrayList(marcas));
        for(Marca marca : marcas){
            if(marca.getId() == 1){
                marcasComboBox.setValue(marca);
                break;
            }
        }
    }




    @FXML
    private void handleCancelar(){
        Stage stage = (Stage) btnCancelarOpe.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlertaDeExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

}
