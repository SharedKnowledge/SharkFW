package net.sharkfw.security.keystorage;

import net.sharkfw.security.SharkKeyPairAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author ac
 */
public interface KeyStorage  {

    public PublicKey getPublicKey();

    public void setPublicKey(PublicKey publicKey);

    public PrivateKey getPrivateKey();

    public void setPrivateKey(PrivateKey privateKey);

    public SharkKeyPairAlgorithm getSharkKeyPairAlgorithm();

    public void setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm sharkKeyPairAlgorithm);
}