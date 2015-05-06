package net.sharkfw.security.key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * @author ac
 */
public class SharkKeyGenerator {

    private String cipherAlgorithm;
    private int keySize;
    private KeyPair keyPair;

    /**
     * Constructor.
     * @param sharkKeyPairAlgorithm
     * @param keySize
     */
    public SharkKeyGenerator(final SharkKeyPairAlgorithm sharkKeyPairAlgorithm, final int keySize) {
        this.cipherAlgorithm = sharkKeyPairAlgorithm.name();
        this.keySize = keySize;
        this.keyPair = generateKeyPair();
    }

    /**
     *
     * @return PublicKey
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     *
     * @return PrivateKey
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /**
     * Method overloading.
     * @param sharkKeyAlgorithm
     * @return SessionKey
     */
    public byte[] getRandomSessionKey(SharkKeyAlgorithm sharkKeyAlgorithm) {
        return generateRandomSessionKey(sharkKeyAlgorithm);
    }

    /**
     * Generates a random session key using the given algorithm and keysize.
     * @param sharkKeyAlgorithm
     * @return SessionKey
     */
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

    /**
     * Generates an new public- and privatekey pair.
     * @return KeyPair
     */
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