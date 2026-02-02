package org.example;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Pedir mensaje al usuario
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce mensaje: ");
        String mensaje = scanner.nextLine();

        // 2. Generar la clave simétrica (AES)
        KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
        keyGenAES.init(128);
        SecretKey claveAES = keyGenAES.generateKey();

        // 3. Cifrar el mensaje usando AES
        Cipher cipherAES = Cipher.getInstance("AES");
        cipherAES.init(Cipher.ENCRYPT_MODE, claveAES);
        byte[] mensajeCifrado = cipherAES.doFinal(mensaje.getBytes());

        // 4. Generar el par de claves asimétricas (RSA)
        KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
        keyGenRSA.initialize(2048);
        KeyPair parRSA = keyGenRSA.generateKeyPair();

        // 5. Cifrar la clave AES usando la clave PÚBLICA RSA
        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, parRSA.getPublic());
        byte[] claveAESCifrada = cipherRSA.doFinal(claveAES.getEncoded());

        // Mostrar lo cifrado por pantalla
        System.out.println("Mensaje cifrado: " + Base64.getEncoder().encodeToString(mensajeCifrado));
        System.out.println("Clave AES cifrada: " + Base64.getEncoder().encodeToString(claveAESCifrada));

        // 6. Descifrar la clave AES usando la clave PRIVADA RSA
        cipherRSA.init(Cipher.DECRYPT_MODE, parRSA.getPrivate());
        byte[] claveAESDescifradaBytes = cipherRSA.doFinal(claveAESCifrada);
        // Reconstruir la clave AES original desde los bytes
        SecretKey claveAESRecuperada = new SecretKeySpec(claveAESDescifradaBytes, "AES");

        // 7. Descifrar el mensaje usando la clave AES recuperada
        cipherAES.init(Cipher.DECRYPT_MODE, claveAESRecuperada);
        String mensajeDescifrado = new String(cipherAES.doFinal(mensajeCifrado));

        // 8. Mostrar el resultado final
        System.out.println("Mensaje descifrado: " + mensajeDescifrado);
    }
}