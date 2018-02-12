package Model;

public abstract class Encryption {
    public abstract String encrypt(String message);
    public abstract String getPublicKey();
    public abstract String decrypt(String message);
}
