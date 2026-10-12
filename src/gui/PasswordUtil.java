/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;



import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static String hash(String password) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return "PBKDF2$"
                + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(
            String password, String storedPassword) throws Exception {

        if (storedPassword == null) {
            return false;
        }

        // Support existing plain-text passwords during migration.
        // New passwords will be stored as hashes.
        if (!storedPassword.startsWith("PBKDF2$")) {
            return password.equals(storedPassword);
        }

        String[] parts = storedPassword.split("\\$");

        if (parts.length != 4) {
            return false;
        }

        int iterations = Integer.parseInt(parts[1]);

        byte[] salt =
                Base64.getDecoder().decode(parts[2]);

        byte[] expectedHash =
                Base64.getDecoder().decode(parts[3]);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                expectedHash.length * 8
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] actualHash = factory.generateSecret(spec).getEncoded();

        return MessageDigest.isEqual(expectedHash, actualHash);
    }
}
