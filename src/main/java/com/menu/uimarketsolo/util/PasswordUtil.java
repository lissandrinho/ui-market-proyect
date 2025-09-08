package com.menu.uimarketsolo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    // Usamos una única instancia para ser más eficientes.
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Genera un hash seguro de una contraseña en texto plano.
     * @param plainTextPassword La contraseña sin encriptar.
     * @return El hash de la contraseña.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        return encoder.encode(plainTextPassword);
    }

    /**
     * Compara una contraseña en texto plano con un hash para ver si coinciden.
     * @param plainTextPassword La contraseña ingresada por el usuario.
     * @param hashedPassword El hash almacenado en la base de datos.
     * @return true si las contraseñas coinciden, false en caso contrario.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        return encoder.matches(plainTextPassword, hashedPassword);
    }
}
