package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.ClienteDAO;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.dao.VentaDAO;
import com.menu.uimarketsolo.model.Cliente;
import com.menu.uimarketsolo.model.Producto;
import com.menu.uimarketsolo.model.ProductoVenta;
import com.menu.uimarketsolo.model.Venta;
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
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class VentasController {

    @FXML
    private TextField fieldBuscarCliente;
    @FXML
    private TextField fieldMontoRecibido;
    @FXML
    private TextField fieldDescuento;
    @FXML
    private TextField fieldBuscarProducto;
    @FXML
    private ComboBox<String> comboBoxMetodoPago;
    @FXML
    private Spinner<Integer> spinnerCantidad;
    @FXML
    private DatePicker fechaVentaPicker;



    @FXML
    private TableView<ProductoVenta> productosTable;
    @FXML
    private TableColumn<ProductoVenta, String> skuColumn;
    @FXML
    private TableColumn<ProductoVenta, String> productoColumn;
    @FXML
    private TableColumn<ProductoVenta, Integer> cantidadColumn;
    @FXML
    private TableColumn<ProductoVenta, Double> precioUnitarioColumn;
    @FXML
    private TableColumn<ProductoVenta, Void> colAcciones;

    @FXML
    private ImageView imageViewProductoPreview;
    @FXML
    private ImageView imgLupaButton;


    @FXML
    private Label labelTotal;
    @FXML
    private Label labelVuelto;
    @FXML
    private Label labelDescuento;
    @FXML
    private Label labelIva;
    @FXML
    private Label labelNumeroFactura;

    @FXML
    private Label labelSubtotal;

    private VentaDAO ventaDAO;
    private Producto producto;
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    private ObservableList<ProductoVenta> listaVenta;
    private Cliente clienteSeleccionado;
    private Producto productoSeleccionadoParaVenta;
    private static final double TASA_IVA = 0.22;

    @FXML
    private void initialize(){
        this.ventaDAO = new VentaDAO();
        this.producto = new Producto();
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        this.listaVenta = FXCollections.observableArrayList();
        productosTable.setItems(listaVenta);

        configurarTabla();
        configurarComponentes();
        configurarAutocompletado();
        configurarColumnaAcciones();

        // Carga segura del cliente por defecto
        this.clienteSeleccionado = clienteDAO.getClientePorCedula("00000000");
        if (this.clienteSeleccionado == null) {
            mostrarAlerta("Error Crítico", "No se pudo encontrar al cliente por defecto (Consumidor Final).");
        }
        fieldBuscarCliente.setText(this.clienteSeleccionado != null ? this.clienteSeleccionado.toString() : "");
        fechaVentaPicker.setValue(LocalDate.now());

        //Numero de factura
        int ultimoId = ventaDAO.getUltimoIdVenta();
        int proximaFactura = ultimoId + 1;
        labelNumeroFactura.setText(String.format("%08d", proximaFactura));

    }

    private void configurarTabla(){
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        precioUnitarioColumn.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }

    private void configurarComponentes(){
        comboBoxMetodoPago.getItems().addAll("Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito");
        comboBoxMetodoPago.setValue("Efectivo");
        comboBoxMetodoPago.valueProperty().addListener((o, oldVal, newVal) -> actualizarTotales());


        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinnerCantidad.setValueFactory(valueFactory);

        fieldDescuento.textProperty().addListener((o, oldVal, newVal) -> actualizarTotales());
        fieldMontoRecibido.textProperty().addListener((o, oldVal, newVal) -> actualizarTotales());

        //Limpiar la selección si el campo de búsqueda de producto se vacía
        fieldBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                productoSeleccionadoParaVenta = null;
                imageViewProductoPreview.setImage(null);
            }
        });


    }




    private void configurarAutocompletado() {

        List<Cliente> todosLosClientes = clienteDAO.getAllClientes();
        TextFields.bindAutoCompletion(fieldBuscarCliente, suggestionRequest -> {
            String textoIngresado = suggestionRequest.getUserText().toLowerCase();


            return todosLosClientes.stream().filter(cliente ->
                    (cliente.getNombre() + " " + cliente.getApellido()).toLowerCase().contains(textoIngresado) ||
                            cliente.getCedula().toLowerCase().contains(textoIngresado)
            )       .limit(10)
                    .toList();

        }).setOnAutoCompleted(event -> {
            // Cuando el usuario elige un cliente, lo guardamos en la variable
            clienteSeleccionado = event.getCompletion();
            // Y actualizamos el texto del campo para mostrar la selección
            fieldBuscarCliente.setText(clienteSeleccionado.toString());
        });


        TextFields.bindAutoCompletion(fieldBuscarProducto, suggestionRequest -> {
            String textoIngresado = suggestionRequest.getUserText();

            return productoDAO.buscarProducto(textoIngresado);
        }).setOnAutoCompleted(event -> {


            productoSeleccionadoParaVenta = event.getCompletion();
            String rutaImagen = productoSeleccionadoParaVenta.getImagenPath();

            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                java.io.File archivoImagen = new java.io.File(rutaImagen);
                if (archivoImagen.exists()) {

                    javafx.scene.image.Image imagen = new javafx.scene.image.Image(archivoImagen.toURI().toString());
                    imageViewProductoPreview.setImage(imagen);
                } else {
                    System.err.println("Error: No se encontró la imagen en la ruta: " + rutaImagen);
                    imageViewProductoPreview.setImage(null); // Limpia la imagen si el archivo no existe
                }
            } else {
                imageViewProductoPreview.setImage(null); // Limpia la imagen si no hay ruta
            }
        });
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<ProductoVenta, Void>, TableCell<ProductoVenta, Void>> cellFactory = param -> {
            return new TableCell<>() {
                private final ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/papelera.png")));
                private final Button btnEliminar = new Button("", iconEliminar);

                {
                    btnEliminar.setStyle("-fx-background-color: transparent;");
                    btnEliminar.setCursor(Cursor.HAND);
                    iconEliminar.setFitHeight(20);
                    iconEliminar.setFitWidth(20);

                    btnEliminar.setOnAction(event -> {
                        // Obtenemos el item de la fila actual
                        ProductoVenta item = getTableView().getItems().get(getIndex());
                        // Lo eliminamos de la lista de la venta
                        listaVenta.remove(item);
                        // Actualizamos los totales
                        actualizarTotales();
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnEliminar);
                    if (!empty) setAlignment(Pos.CENTER);
                }
            };
        };

        colAcciones.setCellFactory(cellFactory);
    }





    @FXML
    private void handleAgregarProducto() {
        if(productoSeleccionadoParaVenta == null){
            mostrarAlerta("Error", "No se ha seleccionado ningún producto.");
            return;
        }
        int cantidad = spinnerCantidad.getValue();
        if(cantidad <= 0) {
            mostrarAlerta("Error", "La cantidad debe ser mayor que cero.");
            return;
        }

        if(cantidad > productoSeleccionadoParaVenta.getStock()){
            mostrarAlerta("Error", "No hay suficiente stock para el producto seleccionado.");
            return;
        }


        boolean productoYaEnLista = false;
        for (ProductoVenta item : listaVenta) {
            if (item.getProducto().getId() == productoSeleccionadoParaVenta.getId()) {

                // Se valida que la cantidad existente + la nueva no supere el stock total.
                int nuevaCantidadTotal = item.getCantidad() + cantidad;
                if (nuevaCantidadTotal > productoSeleccionadoParaVenta.getStock()) {
                    mostrarAlerta("Error de Stock", "No puedes agregar " + cantidad + " unidades. " +
                            "El stock total es " + productoSeleccionadoParaVenta.getStock() + " y ya tienes " + item.getCantidad() + " en el carrito.");
                    return;
                }
                // Si el producto ya está, actualiza la cantidad
                item.setCantidad(item.getCantidad() + cantidad);
                productosTable.refresh();
                // UX Mejora: Seleccionar y hacer scroll a la fila actualizada
                productosTable.getSelectionModel().select(item);
                productosTable.scrollTo(item);
                productoYaEnLista = true;
                break;
            }
        }

        if (!productoYaEnLista) {
            listaVenta.add(new ProductoVenta(productoSeleccionadoParaVenta, cantidad));
        }

        fieldBuscarProducto.clear();
        productoSeleccionadoParaVenta = null;
        imageViewProductoPreview.setImage(null);
        spinnerCantidad.getValueFactory().setValue(1);
        actualizarTotales();
    }

    public Producto getProducto(){
        return producto;
    }

    private void actualizarTotales(){
        double subtotal = 0;
        for (ProductoVenta item : listaVenta) {
            subtotal += item.getSubtotal();
        }

        double descuento = 0;
        try {
            if (!fieldDescuento.getText().isEmpty()) {
                descuento = Double.parseDouble(fieldDescuento.getText());
            }
        } catch (NumberFormatException e) {
            descuento = 0;
        }

        double baseImponible = subtotal - descuento;
        double iva = baseImponible * TASA_IVA;
        double total = baseImponible + iva;

        double montoRecibido = 0;
        try {
            if (!fieldMontoRecibido.getText().isEmpty()){
                montoRecibido = Double.parseDouble(fieldMontoRecibido.getText());
            }
        }catch (NumberFormatException e){
            montoRecibido = 0;
        }

        double vuelto = (comboBoxMetodoPago.getValue().equals("Efectivo") && montoRecibido > total) ? montoRecibido - total : 0;
        labelSubtotal.setText(String.format("$ %.2f", subtotal));
        labelDescuento.setText(String.format("-$ %.2f", descuento));
        labelIva.setText(String.format("$ %.2f", iva));
        labelTotal.setText(String.format("$ %.2f", total));
        labelVuelto.setText(String.format("$ %.2f", vuelto));

    }

    @FXML
    private void handleFinalizarVenta() {
        if (listaVenta.isEmpty()) {
            mostrarAlerta("Error", "No hay productos en la venta.");
            return;
        }
        if(clienteSeleccionado == null) {
            mostrarAlerta("Error", "No se ha seleccionado ningún cliente.");
            return;
        }

        double subtotal = listaVenta.stream().mapToDouble(ProductoVenta::getSubtotal).sum();
        double descuento = 0;
        try {
            if (!fieldDescuento.getText().isEmpty()) {
                descuento = Double.parseDouble(fieldDescuento.getText());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double totalFinal = (subtotal - descuento) * (1 + TASA_IVA);

        Venta nuevaVenta = new Venta();
        nuevaVenta.setUsuarioId(SessionManager.getInstance().getUsuarioLogueado().getId());
        nuevaVenta.setClienteCedula(clienteSeleccionado.getCedula());
        nuevaVenta.setVentaTotal(BigDecimal.valueOf(totalFinal));
        LocalDate fechaSeleccionada = fechaVentaPicker.getValue();
        nuevaVenta.setFechaVenta(fechaSeleccionada.atStartOfDay());

        nuevaVenta.setUsuarioId(SessionManager.getInstance().getUsuarioLogueado().getId());

        boolean exito = ventaDAO.guardarVenta(nuevaVenta, listaVenta, comboBoxMetodoPago.getValue());

        if (exito) {
            mostrarAlertaDeExito("Venta Exitosa", "La venta se ha registrado correctamente.");
            limpiarFormularioVenta();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la venta.");
        }
    }

    @FXML
    private void handleCancelarVenta() {
        if (listaVenta.isEmpty()) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Venta");
        confirmacion.setHeaderText("¿Estás seguro de que deseas cancelar la venta actual?");
        confirmacion.setContentText("Se perderán todos los productos agregados.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarFormularioVenta();
        }
    }

    @FXML
    private void handleMostrarTodosLosClientes() {
        
        fieldBuscarCliente.setEditable(true);
        fieldBuscarCliente.clear();
        fieldBuscarCliente.requestFocus();
    }


    private void limpiarFormularioVenta() {
        listaVenta.clear();
        fieldBuscarProducto.clear();
        fieldDescuento.clear();
        fieldMontoRecibido.clear();
        imageViewProductoPreview.setImage(null);
        spinnerCantidad.getValueFactory().setValue(1);


        this.clienteSeleccionado = clienteDAO.getClientePorCedula("00000000");
        fieldBuscarCliente.setText(this.clienteSeleccionado != null ? this.clienteSeleccionado.toString() : "");

        // Actualizar el número de factura para la siguiente venta
        int ultimoId = ventaDAO.getUltimoIdVenta();
        int proximaFactura = ultimoId + 1;
        labelNumeroFactura.setText(String.format("%08d", proximaFactura));

        actualizarTotales();
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
