module com.menu.uimarketsolo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;


    opens com.menu.uimarketsolo to javafx.fxml;
    exports com.menu.uimarketsolo;
    exports com.menu.uimarketsolo.controller;
    opens com.menu.uimarketsolo.controller to javafx.fxml;
    opens com.menu.uimarketsolo.model to javafx.base;
}