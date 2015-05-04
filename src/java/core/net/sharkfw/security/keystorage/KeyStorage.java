package net.sharkfw.security.keystorage;

import net.sharkfw.security.SharkKeyPairAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author ac
 */
public interface KeyStorage  {

    /**
     *
     * @return PublicKey
     */
    public PublicKey getPublicKey();

    /**
     * Stores PublicKeys.
     * @param publicKey
     */
    public void setPublicKey(PublicKey publicKey);

    /**
     *
     * @return PrivateKey
     */
    public PrivateKey getPrivateKey();

    /**
     * Stores PrivateKey.
     * @param privateKey
     */
    public void setPrivateKey(PrivateKey privateKey);

    /**
     *
     * @return SharkKeyPairAlgorithm
     */
    public SharkKeyPairAlgorithm getSharkKeyPairAlgorithm();

    /**
     * Stores SharkKeyPairAlgorithm.
     * @param sharkKeyPairAlgorithm
     */
    public void setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm sharkKeyPairAlgorithm);
}