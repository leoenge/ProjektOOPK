package Model;

import java.util.Random;

public class CaesarEncryption {
    private static final int keySize = 26;
    private int localKey;
    private int remoteKey;

    public String encrypt(String message) {
        return encrypt(message, false);
    }

    public int getLocalKey() {
        return localKey;
    }
    public int getRemoteKey() { return remoteKey; }

    public String decrypt(String message) {
        return encrypt(message, true);
    }

    public void setKey(int keyIn) {
        //Makes the key a size that we support.
        keyIn %= keySize;
        this.remoteKey = keyIn;
    }

    void generateRandomKey() {
        Random RNG = new Random();
        localKey = RNG.nextInt() % keySize;
    }

    private String encrypt(String message, boolean switchSign) {
        int key = switchSign ? localKey : -localKey;
        String encrypted = "";
        for (int i = 0; i < message.length(); i++) {
            if (Character.isLetter(message.charAt(i))) {
                //Code point for capital A if the current letter is uppercase or of lower case a otherwise.
                int asciiShift = Character.isUpperCase(message.charAt(i)) ? (int) 'a' : (int) 'A';
                //This will be a 0-25 value representing how many code points from 'a' or 'A' this character is.
                int codePoint = (int) message.charAt(i) - asciiShift;
                //Shift by the key
                codePoint += key;
                //Make sure the value is in range
                codePoint %= keySize;
                //Java modulus is the remainder, not the mathematical mod.
                //We want the mathematical modulus, so no negative numbers.
                if (codePoint < 0) {
                    codePoint += keySize;
                }
                //Shift back the value to the characters in question.
                codePoint += asciiShift;

                encrypted += (char) codePoint;
            }
        }

        return encrypted;
    }

}

