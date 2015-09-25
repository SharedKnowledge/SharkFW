package net.sharkfw.security.key.storage;

import net.sharkfw.security.key.SharkKeyPairAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author ac
 */
public interface KeyStorage  {

    /**
     * Returns {@link PublicKey}
     * @return {@link PublicKey}
     */
    PublicKey getPublicKey();

    /**
     * Stores {@link PublicKey}.
     * @param publicKey {@link PublicKey}
     */
    void setPublicKey(PublicKey publicKey);

    /**
     * Returns {@link PrivateKey}
     * @return PrivateKey {@link PrivateKey}
     */
    PrivateKey getPrivateKey();

    /**
     * Stores {@link PrivateKey}.
     * @param privateKey {@link PrivateKey}
     */
    void setPrivateKey(PrivateKey privateKey);

    /**
     * Returns {@link SharkKeyPairAlgorithm}
     * @return SharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
     */
    SharkKeyPairAlgorithm getSharkKeyPairAlgorithm();

    /**
     * Stores {@link SharkKeyPairAlgorithm}.
     * @param sharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
     */
    void setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm sharkKeyPairAlgorithm);
}