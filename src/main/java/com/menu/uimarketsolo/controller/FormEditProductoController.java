package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.MovimientoStockDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Marca;
import com.menu.uimarketsolo.model.MovimientoStock;
import com.menu.uimarketsolo.model.Producto;
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
import java.time.LocalDateTime;
import java.util.List;

public class FormEditProductoController {


    @FXML
    private TextField fieldNuevoSKU;
    @FXML
    private TextField fieldNombreProNuevo;
    @FXML
    private TextField fieldPrecioUniNuevo;
    @FXML
    private TextField fieldCantidadProNuevo;
    @FXML
    private TextField fieldNuevaCantidad;
    @FXML
    private TextArea areaDescripcionNuevo;
    @FXML
    private ImageView imagenPreviewNueva;
    @FXML
    private Button btnGuardarProducto;
    @FXML
    private Button btnCancelarOpe;
    @FXML
    private ComboBox marcaComboBox;


    private ProductoDAO productoDAO;
    private Producto productoAEditar;
    private MarcaDAO marcaDAO;
    private File archivoImagenSeleccionada;
    private MovimientoStockDAO movimientoStockDAO;


    @FXML
    public void initialize(){
        this.productoDAO = new ProductoDAO();
        this.marcaDAO = new MarcaDAO();
        fieldCantidadProNuevo.setEditable(false);
        this.movimientoStockDAO = new MovimientoStockDAO();
    }



    public void initData(Producto producto) {

        this.productoAEditar = producto;
        fieldNuevoSKU.setText(producto.getSku());
        fieldNombreProNuevo.setText(producto.getNombre());
        fieldPrecioUniNuevo.setText(String.valueOf(producto.getPrecioVenta()));
        fieldCantidadProNuevo.setText(String.valueOf(producto.getStock()));
        areaDescripcionNuevo.setText(producto.getDescripcion());
        fieldNuevaCantidad.setText("0");

        if(producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()){
            try{
                String rutaImagen = "/images/productos/" + producto.getImagenPath();
                Image image = new Image(getClass().getResourceAsStream(rutaImagen));
                imagenPreviewNueva.setImage(image);
            }catch (Exception e){
                System.err.println("No se pudo cargar la imagen: " + producto.getImagenPath());
                imagenPreviewNueva.setImage(null);
            }
        }


        List<Marca> todasLasMarcas = marcaDAO.getAllMarcas();
        marcaComboBox.setItems(FXCollections.observableArrayList(todasLasMarcas));

        if (producto.getMarcaId() != 0) {
            for (Marca marca : todasLasMarcas) {
                if (marca.getId() == producto.getMarcaId()) {
                    marcaComboBox.setValue(marca);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSeleccionarImagen(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen Nueva");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen",
                "*.png", "*.jpg", "*.jpeg"));

        File archivo = fileChooser.showOpenDialog(btnGuardarProducto.getScene().getWindow());
        if(archivo != null){
            archivoImagenSeleccionada = archivo;
            Image image = new Image(archivo.toURI().toString());
            imagenPreviewNueva.setImage(image);
        }
    }

    @FXML
    private void handleGuardar(){
        if(fieldNuevoSKU.getText().isEmpty() || fieldNombreProNuevo.getText().isEmpty() || fieldPrecioUniNuevo.getText().isEmpty()){
            mostrarAlerta("Error", "Los campos SKU, Nombre y Precio son obligatorios.");
            return;
        }

        double precio;
        int cantidadAAgregar;

        try{
            precio = Double.parseDouble(fieldPrecioUniNuevo.getText());
            cantidadAAgregar = Integer.parseInt(fieldNuevaCantidad.getText());

        }catch (NumberFormatException e){
            mostrarAlerta("Error de Formato", "El precio y la cantidad a agregar deben ser números válidos.");
            return;
        }

        if(archivoImagenSeleccionada != null){
            try{

                Path destino = Paths.get("src/main/resources/images/productos");
                if(!Files.exists(destino)) Files.createDirectories(destino);

                Path archivoDestino = destino.resolve(archivoImagenSeleccionada.getName());
                Files.copy(archivoImagenSeleccionada.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                productoAEditar.setImagenPath(archivoImagenSeleccionada.getName());
            }catch (IOException e){
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo guardar la nueva imagen.");
            }
        }

        productoAEditar.setSku(fieldNuevoSKU.getText());
        productoAEditar.setNombre(fieldNombreProNuevo.getText());
        productoAEditar.setPrecioVenta(precio);
        productoAEditar.setDescripcion(areaDescripcionNuevo.getText());


        Marca marcaSeleccionada = (Marca) marcaComboBox.getSelectionModel().getSelectedItem();
        if (marcaSeleccionada != null) {
            productoAEditar.setMarcaId(marcaSeleccionada.getId());
        }

        if(cantidadAAgregar > 0){
            productoAEditar.setStock(productoAEditar.getStock() + cantidadAAgregar);
            MovimientoStock movimiento = new MovimientoStock();
            movimiento.setProductoId(productoAEditar.getId());
            movimiento.setTipoMovimiento("INGRESO_MANUAL");
            movimiento.setCantidad(cantidadAAgregar);
            movimiento.setMotivo("Ajuste desde formulario de edicion.");
            movimiento.setFechaMovimiento(LocalDateTime.now());

            movimientoStockDAO.guardarMovimiento(movimiento);
        }

        productoDAO.actualizarProducto(productoAEditar);

        mostrarAlertaDeExito("Producto Actualizado", "Los cambios se guardaron correctamente.");
        handleCancelar();


    }

    @FXML
    private void handleCancelar(){
        Stage stage = (Stage) btnCancelarOpe.getScene().getWindow();
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
