package com.example.libraryManagementSystem.encryption;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class CipherEncryption {
    byte[] salt = new byte[256];
    byte[] iv = new byte[16];
    int iterations = 999;
    protected String encryptMethod = "AES-256-CBC";

    public int encryptMethodLength() {
        return Math.abs(Integer.parseInt(encryptMethod.replaceAll("\\D+", "")));
    }

    public String decrypt(String encryptedString) throws Exception {
        String key="India#321";
        byte[] decodedEncryptedString = Base64.getDecoder().decode(encryptedString);
//        String[] parts = new String(decodedEncryptedString).split("\\|");

        SecretKey secretKey = generateSecretKey(key, salt, iterations);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decryptedText = cipher.doFinal(decodedEncryptedString);

        return new String(decryptedText);
    }

    public String encrypt(String string) throws Exception {
        String key="India#321";
        SecretKey secretKey = generateSecretKey(key, salt, iterations);
        IvParameterSpec Iv  = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, Iv);
        byte[] encryptedText = cipher.doFinal(string.getBytes());
        return Base64.getEncoder().encodeToString(encryptedText);
    }

    private SecretKey generateSecretKey(String password, byte[] salt, int iterations) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, encryptMethodLength()/2);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secretKey;
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] val = new byte[s.length() / 2];
        for (int i = 0; i < val.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(s.substring(index, index + 2), 16);
            val[i] = (byte) j;
        }
        return val;
    }

}