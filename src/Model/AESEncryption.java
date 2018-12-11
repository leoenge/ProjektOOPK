package Model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.setDatatypeConverter;

public class AESEncryption {
    private SecretKeySpec localKey = null;
    private SecretKeySpec remoteKey = null;
    Cipher cipher;

    AESEncryption() {
    }

    /**
     * Generates a new random 128-bit AES key, and stores it in the localKey attribute.
     */
    public void generateKey() {
        KeyGenerator AESKeyGen;
        try {
            AESKeyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            return;
        }

        AESKeyGen.init(128);
        localKey = (SecretKeySpec) AESKeyGen.generateKey();
    }

    /**
     * Sets the remote key to a new value
     * @param rawKey The raw byte value of the new key
     * @throws IllegalBlockSizeException If the key size is not 128-bit or 192-bit or 256-bit.
     */
    void setKey(byte[] rawKey) throws IllegalBlockSizeException{
        //AES keys need to be 128/192/256 bits long.
        if (!((rawKey.length == 16) || (rawKey.length == 24) || (rawKey.length == 32))) {
            throw new IllegalBlockSizeException("Key size not supported");
        }

        remoteKey = new SecretKeySpec(rawKey, "AES");
    }

    public SecretKeySpec getRemoteKey() { return remoteKey; }
    public SecretKeySpec getLocalKey() { return localKey; }

    /**
     * Gets a hex string representation of the local 128-bit AES key.
     * @return A hex string of the key
     * @throws KeyException When there is no local key initialized.
     */
    String getLocalKeyHex() throws KeyException{
        if (localKey == null) {
            throw new KeyException("No local key initialized");
        }
        byte[] rawKey = localKey.getEncoded();
        return DatatypeConverter.printHexBinary(rawKey);
    }

    /**
     * Encrypts the message using AES, and the localKey attribute as key.
     * @param message Message to encrypt.
     * @return Encrypted message as hex string.
     * @throws KeyException If the local key has not been initialized.
     */

    public String encrypt(String message) throws KeyException {
        if (localKey == null) {
            throw new KeyException("No key initialized for AES.");
        }

        //Create an AES cipher instance
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getStackTrace());
            return null;
        } catch (NoSuchPaddingException nspe) {
            System.err.println(nspe.getStackTrace());
            return null;
        }
        //Initialize the cipher with key into encryption mode.
        cipher.init(Cipher.ENCRYPT_MODE, localKey);
        byte[] encryptedData;
        try {
            //Encrypts the string encoded in UTF-8
            encryptedData = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException ibse) {
            System.err.println(ibse.getStackTrace());
            return null;
        } catch (BadPaddingException bpe) {
            System.err.println(bpe.getStackTrace());
            return null;
        }

        //Returns a byte-by-byte hex representation of the data.
        return DatatypeConverter.printHexBinary(encryptedData);
    }

    /**
     * Encrypts some raw byte data using AES with localKey attribute as key.
     * @param rawData The raw byte array to encrypt
     * @return The encrypted data as byte array.
     * @throws KeyException If there is no local key initialized
     * @throws IllegalArgumentException
     */

    public byte[] encrypt(byte[] rawData) throws KeyException, IllegalArgumentException {
        if (localKey == null) {
            throw new KeyException("No local key set.");
        }

        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when initializing cipher.");
        } catch (NoSuchPaddingException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when making padding for AES.");
        }

        cipher.init(Cipher.ENCRYPT_MODE, localKey);
        byte[] encrypted;
        try {
            encrypted = cipher.doFinal(rawData);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid block size");
        } catch (BadPaddingException e) {
            throw new IllegalArgumentException("Padding error in AES");
        }

        return encrypted;
    }

    /**
     * Decrypts a hex string using AES with remoteKey as key and returns the decrypted data encoded in UTF-8 as a String.
     * @param message
     * @return String containing UTF-8 decoding of the decrypted data
     * @throws KeyException If the remote key is not initialized.
     * @throws IllegalArgumentException
     */
    public String decrypt(String message) throws KeyException, IllegalArgumentException {
        if (remoteKey == null) {
            throw new KeyException("No remote key set");
        }

        byte[] encryptedData = DatatypeConverter.parseHexBinary(message);
        byte[] decryptedData;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when initializing cipher.");
        } catch (NoSuchPaddingException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when making padding for AES.");
        }

        cipher.init(Cipher.DECRYPT_MODE, remoteKey);

        try {
            decryptedData = cipher.doFinal(encryptedData);
        } catch (IllegalBlockSizeException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Block size not allowed in AES.");
        } catch (BadPaddingException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Bad padding in AES.");
        }

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts the data in a raw byte array using AES with remoteKey as key.
     * @param rawData The byte array to decrypt.
     * @return The decrypted data as a byte array.
     * @throws KeyException If the remote key is not initialized.
     * @throws IllegalArgumentException
     */
    public byte[] decrypt(byte[] rawData) throws KeyException, IllegalArgumentException {
        if (remoteKey == null) {
            throw new KeyException("No remote key set.");
        }

        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when initializing cipher.");
        } catch (NoSuchPaddingException e) {
            System.err.println(e.getStackTrace());
            throw new IllegalArgumentException("Something went wrong when making padding for AES.");
        }

        cipher.init(Cipher.DECRYPT_MODE, remoteKey);
        byte[] decrypted;
        try {
            decrypted = cipher.doFinal(rawData);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid block size");
        } catch (BadPaddingException e) {
            throw new IllegalArgumentException("Padding error in AES");
        }

        return decrypted;
    }
}
