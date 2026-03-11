package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility để generate SHA-256 hash password
 * Dùng để setup password mặc định cho admin account
 */
public class PasswordHashGenerator {
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    public static void main(String[] args) {
        // Password mặc định: admin@123
        String password = "admin@123";
        String hash = hashPassword(password);
        
        System.out.println("=== PASSWORD HASH GENERATOR ===");
        System.out.println("Password: " + password);
        System.out.println("SHA-256 Hash:");
        System.out.println(hash);
        System.out.println("\nDùng hash này trong SQL:");
        System.out.println("UPDATE Employees SET PasswordHash = '" + hash + "' WHERE Email = 'admin.manager@bookstore.vn';");
    }
}
