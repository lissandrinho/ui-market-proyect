package com.menu.uimarketsolo.util;

// 1. Se cambia el import de Spring por el de jBCrypt
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Ya no se necesita una instancia de 'encoder', jBCrypt usa métodos estáticos.

    /**
     * Genera un hash seguro de una contraseña en texto plano.
     * @param plainTextPassword La contraseña sin encriptar.
     * @return El hash de la contraseña.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        // 2. Se usa el método estático de jBCrypt para hashear.
        // BCrypt.gensalt() genera el "salt" aleatorio automáticamente.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Compara una contraseña en texto plano con un hash para ver si coinciden.
     * @param plainTextPassword La contraseña ingresada por el usuario.
     * @param hashedPassword El hash almacenado en la base de datos.
     * @return true si las contraseñas coinciden, false en caso contrario.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            // 3. Se usa el método estático de jBCrypt para verificar.
            // Este método es 100% COMPATIBLE con los hashes que ya creaste con Spring.
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Esto previene un crash si el hash en la BD no tiene el formato correcto.
            return false;
        }
    }
}