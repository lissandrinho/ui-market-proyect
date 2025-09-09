package com.menu.uimarketsolo.controller;
import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.model.Producto;

import com.menu.uimarketsolo.model.Usuario;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    private Label categoriaLabel;

    @FXML
    private Button ajustarStockButton;
    @FXML
    private HBox hboxBotonesAdmin;


    @FXML
    private void initialize() {
        this.productoDAO = new ProductoDAO();

        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        Usuario usuarioLogueado = SessionManager.getInstance().getUsuarioLogueado();
        if (usuarioLogueado != null && !usuarioLogueado.getRol().equalsIgnoreCase("Administrador")){
            hboxBotonesAdmin.setVisible(false);
            hboxBotonesAdmin.setManaged(false);

            accionesColumn.setVisible(false);

            ajustarStockButton.setVisible(false);
            ajustarStockButton.setManaged(false);
        }


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
        if (producto != null){
            nombreProductoLabel.setText(producto.getNombre());
            skuLabel.setText(producto.getSku());
            stockLabel.setText(String.valueOf(producto.getStock()));
            descripcionTextArea.setText(producto.getDescripcion());
            marcaLabel.setText(producto.getNombreMarca() != null ? producto.getNombreMarca() : "Marca Genérica");
            categoriaLabel.setText(producto.getNombreCategoria() != null ? producto.getNombreCategoria() : "Sin Categoría");

            if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()) {
                try {
                    String rutaImagen = "/images/productos/" + producto.getImagenPath();
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
            categoriaLabel.setText("-");
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
        abrirDialogoProducto(null);
    }
    private void abrirDialogoProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menu/uimarketsolo/view/FormularioProductoView.fxml"));
            Parent root = loader.load();

            // Obtenemos el controlador del formulario para pasarle los datos
            FormularioProductoController controller = loader.getController();
            controller.initData(producto);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(producto == null ? "Agregar Producto" : "Editar Producto");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Refrescamos la tabla después de cerrar
            cargarProductos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Logica para mostrar el producto seleccionado dentro de nuestro Formulario para editar producto
    @FXML
    private void handleEditarProducto() {

        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();
        abrirDialogoProducto(productoSeleccionado);
    }

    //Le da accion al boton de gestionar marcas, abriendo la ventana.
    @FXML
    private void handleGestionarMarcas() {
        try {
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

            cargarProductos();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private void handleEliminarProducto(Producto productoSeleccionado) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se ha seleccionado ningún producto para eliminar.");
            return;
        }

        // Creamos una alerta de confirmación
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que querés eliminar este producto?");
        alertaConfirmacion.setContentText(productoSeleccionado.getNombre() + " (SKU: " + productoSeleccionado.getSku() + ")");

        // Mostramos la alerta y esperamos la respuesta del usuario
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Si el usuario presiona "OK", llamamos al DAO para borrar
                productoDAO.eliminarProducto(productoSeleccionado.getId());

                // Refrescamos la tabla para que el cambio se vea al instante
                cargarProductos();

            } catch (Exception e) {
                // Si el DAO falla (por ejemplo, por un problema de base de datos), le mostramos un error al usuario
                mostrarAlerta("Error de Base de Datos", "No se pudo eliminar el producto. Revise la conexión.");
                e.printStackTrace();
            }
        }
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
