package com.menu.uimarketsolo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // ESTA ES LA LÍNEA CRÍTICA CORREGIDA
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/menu/uimarketsolo/view/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 630);
        stage.setTitle("UI MARKET - Inicio de Sesión");
        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(750);
        stage.setMinHeight(630);
    }

    // Este es el método main con el registro de errores
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Throwable t) {
            try {
                String userHome = System.getProperty("user.home");
                File errorFile = new File(userHome + "/Desktop/error_log.txt");
                PrintWriter writer = new PrintWriter(new FileWriter(errorFile, true));

                writer.println("--------------------------------");
                writer.println("Error at: " + new Date());
                t.printStackTrace(writer);
                writer.close();

            } catch (Exception e) {
                t.printStackTrace();
                e.printStackTrace();
            }
        }
    }
}