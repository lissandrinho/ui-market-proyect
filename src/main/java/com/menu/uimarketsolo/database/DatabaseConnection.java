package com.menu.uimarketsolo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

private static final String URL = "jdbc:mysql://localhost:3306/uimarket";
private static final String USER = "root";
private static final String PASSWORD = "";

public static Connection getConnection(){
    Connection connection = null;
    try {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (SQLException e) {
        System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        e.printStackTrace();
    }
    return connection;
}

public static void main(String[] args){
    Connection conn = null;
    try {
        conn = getConnection();
        if (conn != null) {
            System.out.println("Conexión exitosa a la base de datos");
        }
        else {
            System.out.println("No se pudo establecer la conexión a la base de datos");

        }
    }finally {
        if (conn != null) {
            try{
                conn.close();
                System.out.println("Conexion cerrada...");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

}
