package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.dao.CategoriaDAO;
import com.menu.uimarketsolo.dao.MarcaDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.dao.ProveedorDAO;
import com.menu.uimarketsolo.model.Categoria;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;

public class FormularioProductoController {

    @FXML private Label formularioProductoLabel;
    @FXML private TextField fieldSku;
    @FXML private TextField fieldNombreProducto;
    @FXML private ComboBox<Categoria> comboBoxCategoria;
    @FXML private ComboBox<Marca> comboBoxMarca;
    @FXML private ComboBox<Proveedor> comboBoxProveedor;
    @FXML private TextField fieldPrecioUnitario;
    @FXML private TextField fieldStockInicial;
    @FXML private TextArea textAreaDescripcion;
    @FXML private ImageView imagenViewProducto;
    @FXML private Button seleccionarImagenButton;
    @FXML private Button nuevaCategoriaButton;
    @FXML private Button btnCancelarOpe;
    @FXML private Button btnGuardar;

    @FXML
    private ComboBox<Marca> marcasComboBox;

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private MarcaDAO marcaDAO;
    private ProveedorDAO proveedorDAO;
    private Producto productoAEditar;
    private File archivoImagenSeleccionada;

    public void initialize(){
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.marcaDAO = new MarcaDAO();
        this.proveedorDAO = new ProveedorDAO();


        cargarComboBoxes();
    }

    private void cargarComboBoxes(){
        List<Categoria> categorias = categoriaDAO.getAllCategorias();
        comboBoxCategoria.setItems(FXCollections.observableArrayList(categorias));

        List<Marca> marcas = marcaDAO.getAllMarcas();
        comboBoxMarca.setItems(FXCollections.observableArrayList(marcas));

        List<Proveedor> proveedores = proveedorDAO.getAllProveedores();
        comboBoxProveedor.setItems(FXCollections.observableArrayList(proveedores));
    }

    public void initData(Producto producto){
        this.productoAEditar = producto;
        if(producto != null){
            formularioProductoLabel.setText("Editar Producto");
            fieldSku.setText(producto.getSku());
            fieldNombreProducto.setText(producto.getNombre());
            fieldPrecioUnitario.setText(String.valueOf(producto.getPrecioVenta()));
            fieldStockInicial.setText(String.valueOf(producto.getStock()));
            textAreaDescripcion.setText(producto.getDescripcion());

            for (Categoria cat : comboBoxCategoria.getItems()){
                if (cat.getId() == producto.getCategoriaId()){
                    comboBoxCategoria.setValue(cat);
                    break;
                }
            }

            for (Marca marca : comboBoxMarca.getItems()){
                if (marca.getId() == producto.getMarcaId()){
                    comboBoxMarca.setValue(marca);
                    break;
                }
            }

            for (Proveedor prov : comboBoxProveedor.getItems()){
                if (prov.getId() == producto.getProveedorId()){
                    comboBoxProveedor.setValue(prov);
                    break;
                }
            }


            if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()){
                File archivoImagen = new File(producto.getImagenPath());
                if (archivoImagen.exists()) {
                    try {
                        // Se carga la imagen desde el archivo del sistema de ficheros.
                        imagenViewProducto.setImage(new Image(new FileInputStream(archivoImagen)));
                    } catch (IOException e) {
                        System.err.println("Error al cargar la imagen: " + producto.getImagenPath());
                    }
                }
            }

        }else {
            formularioProductoLabel.setText("Agregar Nuevo Producto");
        }
    }

    @FXML
    private void handleSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de " +
                "Imagen", "*.png", "*.jpg", "*.jpeg"));
        archivoImagenSeleccionada = fileChooser.showOpenDialog(btnGuardar.getScene().getWindow());
        if (archivoImagenSeleccionada != null) {
            imagenViewProducto.setImage(new Image(archivoImagenSeleccionada.toURI().toString()));
        }
    }

    @FXML
    private void handleNuevaCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Categoría");
        dialog.setHeaderText("Crear una nueva categoría de producto");
        dialog.setContentText("Nombre:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nombre -> {
            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setNombre(nombre);
            Categoria categoriaGuardada = categoriaDAO.guardarCategoria(nuevaCategoria);
            if (categoriaGuardada != null) {
                cargarComboBoxes();
                comboBoxCategoria.setValue(categoriaGuardada);
            }
        });
    }

    @FXML
    private void handleGuardar(){
        String sku = fieldSku.getText();

        // Se comprueba si el SKU ya existe y, si estamos editando, que no pertenezca a otro producto.
        if (productoDAO.existeSku(sku) && (productoAEditar == null || !productoAEditar.getSku().equals(sku))) {
            mostrarAlerta("Error de SKU", "El SKU '" + sku + "' ya existe para otro producto. Por favor, ingrese uno único.");
            return;
        }

        String nombre = fieldNombreProducto.getText();
        String descripcion = textAreaDescripcion.getText();
        String precioTexto = fieldPrecioUnitario.getText();
        String stockTexto = fieldStockInicial.getText();
        Categoria categoria = comboBoxCategoria.getValue();
        Marca marca = comboBoxMarca.getValue();
        Proveedor proveedor = comboBoxProveedor.getValue();

        if (sku.isEmpty() || nombre.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty() || categoria == null || marca == null || proveedor == null) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.");
            return;
        }

        double precioVenta;
        int stock;
        try{
            precioVenta = Double.parseDouble(precioTexto);
            stock = Integer.parseInt(stockTexto);
        }catch (NumberFormatException e){
            mostrarAlerta("Error de Formato", "El precio y el stock deben ser números válidos.");
            return;
        }

        String nombreImagen = null;
        if (archivoImagenSeleccionada != null) {
            try {

                Path directorioImagenes = Paths.get(System.getProperty("user.home"), "MarketSOLO_Images", "productos");

                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }
                

                Path archivoDestino = directorioImagenes.resolve(archivoImagenSeleccionada.getName());
                Files.copy(archivoImagenSeleccionada.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                nombreImagen = archivoDestino.toAbsolutePath().toString();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo guardar la imagen del producto.");
            }
        }

        try {
            if (productoAEditar == null){
                Producto nuevoProducto = new Producto();
                nuevoProducto.setSku(sku);
                nuevoProducto.setNombre(nombre);
                nuevoProducto.setDescripcion(descripcion);
                nuevoProducto.setPrecioVenta(precioVenta);
                nuevoProducto.setStock(stock);
                nuevoProducto.setCategoriaId(categoria.getId());
                nuevoProducto.setMarcaId(marca.getId());
                nuevoProducto.setProveedorId(proveedor.getId());
                nuevoProducto.setImagenPath(nombreImagen);

                productoDAO.guardarProducto(nuevoProducto);
                mostrarAlertaDeExito("Producto Guardado", "El nuevo producto se ha registrado correctamente.");
            }else {
                productoAEditar.setSku(sku);
                productoAEditar.setNombre(nombre);
                productoAEditar.setDescripcion(descripcion);
                productoAEditar.setPrecioVenta(precioVenta);
                productoAEditar.setStock(stock);
                productoAEditar.setCategoriaId(categoria.getId());
                productoAEditar.setMarcaId(marca.getId());
                productoAEditar.setProveedorId(proveedor.getId());

                if(nombreImagen != null){
                    productoAEditar.setImagenPath(nombreImagen);
                }
                productoDAO.actualizarProducto(productoAEditar);
                mostrarAlertaDeExito("Producto Actualizado", "Los datos se han actualizado correctamente.");
            }
            cerrarVentana();
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudo guardar el producto. Por favor, revise la conexión o los datos ingresados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(){
        Stage stage = (Stage) btnCancelarOpe.getScene().getWindow();
        stage.close();
    }



    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
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
