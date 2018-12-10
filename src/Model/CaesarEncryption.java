package Model;

import java.util.Random;

public class CaesarEncryption {
    private static final int keySize = 26;

    public String encrypt(String message, int key) {
        return encrypt(message, key, false);
    }

    public String decrypt(String message, int key) {
        return encrypt(message, key,true);
    }

    int generateRandomKey() {
        Random RNG = new Random();
        return RNG.nextInt(keySize - 1);
    }

    private String encrypt(String message, int keyIn, boolean switchSign) {
        System.err.println(keyIn);
        int key = switchSign ? -keyIn : keyIn;
        String encrypted = "";
        for (int i = 0; i < message.length(); i++) {
            if (Character.isLetter(message.charAt(i))) {
                //Code point for capital A if the current letter is uppercase or of lower case a otherwise.
                char asciiShift = Character.isUpperCase(message.charAt(i)) ? 'A' : 'a';
                //This will be a 0-25 value representing how many code points from 'a' or 'A' this character is.
                char codePoint = (char) (message.charAt(i) - asciiShift);
                System.err.println("code point: " + (int) codePoint);
                codePoint = (char) ((codePoint + key + keySize) % keySize);
                System.err.println("code point post shift: " + (int) codePoint);
                //Shift back the value to the characters in question.
                System.err.println("Code point post shifting back: " + (int) (codePoint + asciiShift));
                encrypted += (char) (codePoint + asciiShift);
            }
        }

        return encrypted;
    }

}

