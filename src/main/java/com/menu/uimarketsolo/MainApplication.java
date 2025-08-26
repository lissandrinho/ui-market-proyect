package com.menu.uimarketsolo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/menu/uimarketsolo/view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1320, 740);
        stage.setTitle("UI MARKET");
        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(1280);
        stage.setMinHeight(720);

    }

    public static void main(String[] args) {
        launch();
    }

}