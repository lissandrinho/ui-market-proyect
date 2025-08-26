package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class VentasController {

    // --- Búsqueda Cliente y Producto ---
    @FXML
    private TextField field_buscarCliente;

    @FXML
    private TextField field_cliente;

    @FXML
    private TextField field_buscarProducto;

    @FXML
    private Spinner<?> spiner_cantidadProducto;

    // --- Tabla de Venta ---
    @FXML
    private TableView<Producto> table_listaProductos; // Usar un modelo específico para la venta es mejor

    // --- Panel de Totales ---
    @FXML
    private Label label_subtotal;
    @FXML
    private Label label_iva;
    @FXML
    private Label label_total;

    // --- Botones de Acción ---
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_eliminarProducto;
    @FXML
    private Button btn_finalizarVenta;
}
