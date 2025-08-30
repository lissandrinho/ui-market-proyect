package com.menu.uimarketsolo.controller;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ProductosController {

    //espacio para la table view

    @FXML
    private Button agregarProductoButton;

    @FXML
    private Button editarProductoButton;

    @FXML
    private Button eliminarProductoButton;
    @FXML
    private TableView<Producto> productosTable;

    @FXML
    private TableColumn<Producto, String> skuColumn;
    @FXML
    private TableColumn<Producto, String> nombreColumn;
    @FXML
    private TableColumn<Producto, Double> precioColumn;
    @FXML
    private TableColumn<Producto, Integer> stockColumn;

    private ProductoDAO productoDAO;

    @FXML
    private ImageView productoImageView;
    @FXML
    private Label nombreProductoLabel;
    @FXML
    private Label skuLabel;
    @FXML
    private Label stockLabel;
    @FXML
    private TextArea descripcionTextArea;
    @FXML
    private TextField buscarProductoField;

    @FXML
    private Button ajustarStockButton;


    @FXML
    private void initialize() {
        this.productoDAO = new ProductoDAO();

        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        //MOSTRAMOS LOS DETALLES DE NUESTROS PRODUCTOS SELECCIONADOS DESDE EL TABLEVIEW

        productosTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    mostrarDetallesProducto(newValue);

                    boolean productoSeleccionado = (newValue != null);
                    editarProductoButton.setDisable(!productoSeleccionado);
                    eliminarProductoButton.setDisable(!productoSeleccionado);
                    ajustarStockButton.setDisable(!productoSeleccionado);
                }
        );

        editarProductoButton.setDisable(true);
        eliminarProductoButton.setDisable(true);
        ajustarStockButton.setDisable(true);

        buscarProductoField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarTabla(newValue);
        });

        cargarProductos();

    }

    private void mostrarDetallesProducto(Producto producto) {
        if (producto != null) {
            nombreProductoLabel.setText(producto.getNombre());
            skuLabel.setText(producto.getSku());
            stockLabel.setText(String.valueOf(producto.getStock()));
            descripcionTextArea.setText(producto.getDescripcion());

            if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()) {

                try {
                    String rutaImagen = "/products/" + producto.getImagenPath();
                    Image image = new Image(getClass().getResourceAsStream(rutaImagen));

                    if(image.isError()) {
                        System.err.println("Error al decodificar la imagen: " + producto.getImagenPath());
                        productoImageView.setImage(null);
                    }
                    else{

                        productoImageView.setImage(image);
                    }

                } catch (Exception e) {
                    System.err.println("No se pudo cargar la imagen: " + producto.getImagenPath());
                }
            } else {
                productoImageView.setImage(null);
            }
        } else {
            nombreProductoLabel.setText("-");
            skuLabel.setText("-");
            stockLabel.setText("-");
            descripcionTextArea.setText("");
            productoImageView.setImage(null);
        }
    }

    private void cargarProductos() {
        List<Producto> productosDesdeDB = productoDAO.getAllProductos();
        ObservableList<Producto> productoObservables = FXCollections.observableArrayList(productosDesdeDB);
        productosTable.setItems(productoObservables);
    }


    //Agregamos funcion a nuestro Agregar productos
    @FXML
    private void handleAgregarProducto() {
        try {
            URL url = getClass().getResource("/com/menu/uimarketsolo/view/FormularioAggProducto.fxml");

            if (url == null) {
                System.err.println("Error Crítico: No se pudo encontrar el archivo FXML.");
                return;
            }


            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Agregar Nuevo Producto");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(agregarProductoButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana del formulario:");
            e.printStackTrace();
        }
    }

    //Logica para mostrar el producto seleccionado dentro de nuestro Formulario para editar producto
    @FXML
    private void handleEditarProducto() {
        System.out.println("Botón 'Editar Producto' presionado.");
        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormEditProducto.fxml"));
            Parent root = loader.load();

            FormEditProductoController formEditProductoController = loader.getController();
            formEditProductoController.initData(productoSeleccionado);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Producto");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            cargarProductos();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleEliminarProducto() {
        System.out.println("Botón 'Eliminar Producto' presionado.");

        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Ninguna Seleccion");
            alerta.setHeaderText("No has seleccionado ningun producto");
            alerta.setContentText("Por favor, seleccioná un producto de la tabla para eliminarlo.");
            alerta.showAndWait();
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminacion");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar este producto?");
        alertaConfirmacion.setContentText(productoSeleccionado.getNombre() + "(SKU: " + productoSeleccionado.getSku() + ")");

        alertaConfirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                productoDAO.eliminarProducto(productoSeleccionado.getId());
                cargarProductos();
            }
        });
    }



    @FXML
    private void handleAjustarStock() {
        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/AjusteStockView.fxml"));
            Parent root = loader.load();

            AjusteStockController Controller = loader.getController();
            Controller.initData(productoSeleccionado);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajuste de Stock para " + productoSeleccionado.getNombre());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(ajustarStockButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            cargarProductos();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //Controlador para buscar en tiempo real producto
    private void filtrarTabla(String TextoBusqueda){
        if(TextoBusqueda == null || TextoBusqueda.isEmpty()){
            cargarProductos();
        }
        else{
            List<Producto> productosFiltrados = productoDAO.buscarProducto(TextoBusqueda);
            productosTable.setItems(FXCollections.observableArrayList(productosFiltrados));
        }
    }


}
