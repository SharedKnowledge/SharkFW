package net.sharkfw.security.key.storage;

import net.sharkfw.security.key.SharkKeyPairAlgorithm;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author ac
 */
public class SharkKeyStorage implements KeyStorage, Serializable {

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SharkKeyPairAlgorithm sharkKeyPairAlgorithm;

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public SharkKeyPairAlgorithm getSharkKeyPairAlgorithm() {
        return sharkKeyPairAlgorithm;
    }

    @Override
    public void setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        this.sharkKeyPairAlgorithm = sharkKeyPairAlgorithm;
    }
}
