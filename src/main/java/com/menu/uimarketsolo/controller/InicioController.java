package com.menu.uimarketsolo.controller;

import com.menu.uimarketsolo.SessionManager;
import com.menu.uimarketsolo.dao.ProductoDAO;
import com.menu.uimarketsolo.dao.VentaDAO;
import com.menu.uimarketsolo.model.Producto;
import com.menu.uimarketsolo.model.Usuario;
import com.menu.uimarketsolo.model.Venta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class InicioController {
    @FXML private Label bienvenidoUsuarioLabel;
    @FXML private Label totalIngresadoLabel;
    @FXML private Label productosVendidosLabel;
    @FXML private Label fechaLabel;

    //TableView para mostrar los 10 productos con Stock Bajo
    @FXML private TableView<Producto> tableViewStockBajo;
    @FXML private TableColumn<Producto, String> productoColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;

    //TableView para mostrar las ultimas 10 ventas del dia
    @FXML private TableView<Venta> tableViewUltimasVentas;
    @FXML private TableColumn<Venta, LocalDateTime> horaColumn;
    @FXML private TableColumn<Venta, String> clienteColumn;
    @FXML private  TableColumn<Venta, BigDecimal> totalColumn;

    //BartChart para mostrar las ventas de los ultimos 7 dias
    @FXML private BarChart<String, Number> barChartVentasSemales;
    @FXML private CategoryAxis xAxis; //eje X
    @FXML private NumberAxis yAxis; //Eje Y para el monto

    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;

    @FXML
    private void initialize(){
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();

        cargarEncabezado();
        cargarTarjetasResumen();
        configurarYcargarTablaStockBajo();
        configurarYcargarTablaUltimasVentas();
        cargarDatosGrafico();

    }

    private void cargarEncabezado(){
        Usuario usuario = SessionManager.getInstance().getUsuarioLogueado();
        if (usuario != null){
            bienvenidoUsuarioLabel.setText("Bienvenido de vuelta, " + usuario.getNombreCompleto() + "!");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Hoy es' EEEE, dd 'de' MMMM 'de' yyyy.");
        fechaLabel.setText(formatter.format(LocalDateTime.now()));
    }

    private void cargarTarjetasResumen(){
        LocalDate hoy = LocalDate.now();
        BigDecimal ventasHoy = ventaDAO.getTotalVentasHoy(hoy);
        int productosVendidosHoy = productoDAO.getTotalProductosVendidosHoy(hoy);

        totalIngresadoLabel.setText(String.format("$ %.2f", ventasHoy));
        productosVendidosLabel.setText(String.valueOf(productosVendidosHoy));
    }

    private void configurarYcargarTablaStockBajo(){
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        List<Producto> productosBajoStock = productoDAO.getProductosConStockBajo(10);
        tableViewStockBajo.setItems(FXCollections.observableArrayList(productosBajoStock));
    }

    private void configurarYcargarTablaUltimasVentas() {
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("nombreCliente")); // Se enlaza al nombre del cliente
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("ventaTotal"));

        List<Venta> ultimasVentas = ventaDAO.getUltimasVentas(10);
        tableViewUltimasVentas.setItems(FXCollections.observableArrayList(ultimasVentas));
    }

    private void cargarDatosGrafico() {

        Map<String, BigDecimal> ventasSemanales = ventaDAO.getVentasUltimos7Dias();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas de la Semana");

        for (Map.Entry<String, BigDecimal> entry : ventasSemanales.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartVentasSemales.getData().add(series);
    }
}
