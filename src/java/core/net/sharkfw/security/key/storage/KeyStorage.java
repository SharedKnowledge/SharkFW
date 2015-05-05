package net.sharkfw.security.key.storage;

import net.sharkfw.security.key.SharkKeyPairAlgorithm;

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
    PublicKey getPublicKey();

    /**
     * Stores PublicKeys.
     * @param publicKey
     */
    void setPublicKey(PublicKey publicKey);

    /**
     *
     * @return PrivateKey
     */
    PrivateKey getPrivateKey();

    /**
     * Stores PrivateKey.
     * @param privateKey
     */
    void setPrivateKey(PrivateKey privateKey);

    /**
     *
     * @return SharkKeyPairAlgorithm
     */
    SharkKeyPairAlgorithm getSharkKeyPairAlgorithm();

    /**
     * Stores SharkKeyPairAlgorithm.
     * @param sharkKeyPairAlgorithm
     */
    void setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm sharkKeyPairAlgorithm);
}