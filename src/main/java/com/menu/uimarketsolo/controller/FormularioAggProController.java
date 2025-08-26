package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    private ProductoDAO productosDAO;
    private File archivoImagenSeleccionada;

    public void initialize(){
        this.productosDAO = new ProductoDAO();
    }

    @FXML
    private void handleGuardar() {
        String sku = fieldSKU.getText();
        String nombre = fieldNombrePro.getText();
        String descripcion = areaDescripcion.getText();
        String precioTexto = fieldPrecioUni.getText();
        String cantidadTexto = fieldCantidadPro.getText();

        if (sku.isEmpty() || nombre.isEmpty() || precioTexto.isEmpty() || cantidadTexto.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
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
                Path destino = Paths.get("src/main/resources/com/menu/uimarketsolo/images/productos");
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
