package com.quang.smart_recipe.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";

    public static String encrypt(String plainText, String secretKey) {
        if (plainText == null) return null;
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(secretKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa AES: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String cipherText, String secretKey) {
        if (cipherText == null) return null;
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(secretKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi giải mã AES: " + e.getMessage(), e);
        }
    }

    private static SecretKeySpec getSecretKeySpec(String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16); // 128-bit key size for compatibility and safety
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
