package com.menu.uimarketsolo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private void handleVentasClick(){
        System.out.println("Boton de ventas presionado. Cargando vista...");
        cargarVista("VentasView.fxml");
    }

    private void cargarVista(String nombreFXML){
        try {

            URL url = getClass().getResource("/com/menu/uimarketsolo/view/" + nombreFXML);
            if (url == null) {
                System.out.println("No se pudo encontrar el archivo FXML: " + nombreFXML);
                return;
            }

            Parent vista = FXMLLoader.load(url);
            mainPane.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProductosClick(){
        System.out.println("Boton de Stock presionado. Cargando vista...");
        cargarVista("ProductosView.fxml");
    }

    @FXML
    private void handleClientesClick(){
        System.out.println("Boton de Clientes presionado. Cargando vista...");
        cargarVista("ClientesView.fxml");
    }

    @FXML
    private void handleReportesClick(){
        System.out.println("Boton de Clientes presionado. Cargando vista...");
        cargarVista("ReportesView.fxml");
    }

    @FXML
    private void handleProveedoresClick(){
        System.out.println("Boton de Proveedores presionado. Cargando vista...");
        cargarVista("ProveedorView.fxml");
    }
}