package net.sharkfw.security.key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * @author ac
 */
public class SharkKeyGenerator {

    private String cipherAlgorithm;
    private final int keySize;
    private KeyPair keyPair;

    /**
     * Constructor.
     * @param sharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
     * @param keySize {@link Integer}
     */
    public SharkKeyGenerator(final SharkKeyPairAlgorithm sharkKeyPairAlgorithm, final int keySize) {
        this.cipherAlgorithm = sharkKeyPairAlgorithm.name();
        this.keySize = keySize;
        this.keyPair = generateKeyPair();
    }

    /**
     * Returns {@link PublicKey}
     * @return PublicKey {@link PublicKey}
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * Returns {@link PrivateKey}
     * @return PrivateKey {@link PrivateKey}
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /**
     * Method overloading.
     * @param sharkKeyAlgorithm {@link SharkKeyAlgorithm}
     * @return SessionKey byte-array
     */
    public byte[] getRandomSessionKey(SharkKeyAlgorithm sharkKeyAlgorithm) {
        return generateRandomSessionKey(sharkKeyAlgorithm);
    }

    /**
     * Generates a random session key using the given algorithm and keysize.
     * @param sharkKeyAlgorithm {@link SharkKeyAlgorithm}
     * @return SessionKey byte-array
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
     * Generates an new public- and private-key pair.
     * @return KeyPair {@link KeyPair}
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