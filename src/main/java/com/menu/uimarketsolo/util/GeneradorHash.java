package com.menu.uimarketsolo.util;

public class GeneradorHash {

    public static void main(String[] args) {
        String contrasenaAdmin = "admin";
        String hash = PasswordUtil.hashPassword(contrasenaAdmin);
        System.out.println("El hash para 'admin' es: " + hash);
    }

}
