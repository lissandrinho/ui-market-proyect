package com.menu.uimarketsolo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/menu/uimarketsolo/view/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 630);
        stage.setTitle("UI MARKET - Inicio de Sesión");
        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(750);
        stage.setMinHeight(630);

    }

    public static void main(String[] args) {
        launch();
    }

}