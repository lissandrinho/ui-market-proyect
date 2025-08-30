package com.menu.uimarketsolo.controller;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.net.URL;
import java.util.List;

public class ProductosController {

    //espacio para la table view

    @FXML
    private Button agregarProductoButton;

    @FXML
    private Button editarProductoButton;

    @FXML
    private Button gestionarMarcasButton;

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
    @FXML
    private TableColumn<Producto, Void> accionesColumn;

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
    private Label marcaLabel;

    @FXML
    private Button ajustarStockButton;


    @FXML
    private void initialize() {
        this.productoDAO = new ProductoDAO();

        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        //Mostramos el boton de papelera en la TableView
        Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Producto, Void> call(final TableColumn<Producto, Void> param) {
                final TableCell<Producto, Void> cell = new TableCell<>() {
                    private final ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
                    private final Button btnEliminar = new Button("", iconEliminar);

                    {
                        btnEliminar.setStyle("-fx-background-color: transparent;");
                        btnEliminar.setCursor(Cursor.HAND);
                        iconEliminar.setFitHeight(20);
                        iconEliminar.setFitWidth(20);

                        btnEliminar.setOnAction(event -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            handleEliminarProducto(producto);
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
                return cell;
            }
        };

        accionesColumn.setCellFactory(cellFactory);


        //MOSTRAMOS LOS DETALLES DE NUESTROS PRODUCTOS SELECCIONADOS DESDE EL TABLEVIEW

        productosTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    mostrarDetallesProducto(newValue);

                    boolean productoSeleccionado = (newValue != null);
                    editarProductoButton.setDisable(!productoSeleccionado);
                    ajustarStockButton.setDisable(!productoSeleccionado);
                }
        );

        editarProductoButton.setDisable(true);
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
            marcaLabel.setText(producto.getNombreMarca() != null ? producto.getNombreMarca() : "Marca Genérica");

            if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()) {
                try {
                    String rutaImagen = "/com/menu/uimarketsolo/images/productos/" + producto.getImagenPath();
                    Image image = new Image(getClass().getResourceAsStream(rutaImagen));
                    productoImageView.setImage(image.isError() ? null : image);
                } catch (Exception e) {
                    productoImageView.setImage(null);
                }
            } else {
                productoImageView.setImage(null);
            }
        } else {

            nombreProductoLabel.setText("-");
            skuLabel.setText("-");
            stockLabel.setText("-");
            marcaLabel.setText("-");
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
            cargarProductos();
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

    //Le da accion al boton de gestionar marcas, abriendo la ventana.
    @FXML
    private void handleGestionarMarcas() {
        try {
            // La ruta debe ser perfecta, incluyendo el nombre del archivo
            URL url = getClass().getResource("/com/menu/uimarketsolo/view/GestionarMarcaView.fxml");


            if (url == null) {
                System.err.println("Error: No se encontró el archivo FXML para gestionar marcas.");
                return;
            }

            Parent root = FXMLLoader.load(url);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Marcas");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(gestionarMarcasButton.getScene().getWindow());

            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    @FXML
    private void handleEliminarProducto(Producto productoSeleccionado) {
        if (productoSeleccionado == null) return;

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar este producto?");
        alertaConfirmacion.setContentText(productoSeleccionado.getNombre() + " (SKU: " + productoSeleccionado.getSku() + ")");

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
