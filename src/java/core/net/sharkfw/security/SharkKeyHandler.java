package net.sharkfw.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * Created by Alexander on 28.04.2015.
 */
public class SharkKeyHandler {

    private String cipherAlgorithm;
    private int keySize;
    private KeyPair keyPair;
    private byte[] sessionKey;

    public SharkKeyHandler(SharkKeyPairAlgorithm sharkKeyPairAlgorithm, int keySize) {
        this.cipherAlgorithm = sharkKeyPairAlgorithm.name();
        this.keySize = keySize;
        this.keyPair = generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public byte[] getRandomSessionKey(SharkKeyAlgorithm sharkKeyAlgorithm) {
        return generateRandomSessionKey(sharkKeyAlgorithm);
    }

    private byte[] generateRandomSessionKey(SharkKeyAlgorithm sharkKeyAlgorithm) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(sharkKeyAlgorithm.name());
            keyGenerator.init(keySize/8);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(cipherAlgorithm);
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
