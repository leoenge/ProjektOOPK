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

    void setKey(byte[] rawKey) throws IllegalBlockSizeException{
        //AES keys need to be 128/192/256 bits long.
        if (!((rawKey.length == 16) || (rawKey.length == 24) || (rawKey.length == 32))) {
            throw new IllegalBlockSizeException("Key size not supported");
        }

        remoteKey = new SecretKeySpec(rawKey, "AES");
    }

    public SecretKeySpec getRemoteKey() { return remoteKey; }
    public SecretKeySpec getLocalKey() { return localKey; }
    String getLocalKeyHex() throws KeyException{
        if (localKey == null) {
            throw new KeyException("No local key initialized");
        }
        byte[] rawKey = localKey.getEncoded();
        return DatatypeConverter.printHexBinary(rawKey);
    }

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
