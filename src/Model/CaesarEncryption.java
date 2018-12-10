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
        int key = switchSign ? -keyIn : keyIn;
        String encrypted = "";
        for (int i = 0; i < message.length(); i++) {
            if (Character.isLetter(message.charAt(i))) {
                //Code point for capital A if the current letter is uppercase or of lower case a otherwise.
                int asciiShift = Character.isUpperCase(message.charAt(i)) ? (int) 'A' : (int) 'a';
                //This will be a 0-25 value representing how many code points from 'a' or 'A' this character is.
                int codePoint = (int) message.charAt(i) - asciiShift;
                codePoint = ((codePoint + key + keySize) % keySize);
                //Shift back the value to the characters in question.
                codePoint += asciiShift;

                encrypted += (char) codePoint;
            }
        }

        return encrypted;
    }

}

