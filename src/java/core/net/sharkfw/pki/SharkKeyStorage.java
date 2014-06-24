/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.pki;

import java.security.PrivateKey;

/**
 *
 * @author s0539748
 */
public interface SharkKeyStorage {
    
    /**
     * Creates a PublicKey and a PrivateKey and stores them
     */
    public void createKeyPair();
    
    /**
     * Returns the private key.
     * @return 
     */
    PrivateKey getPrivateKey();
    
    /**
     * Returns the public key of the local peer.
     * @return 
     */
    PrivateKey getPublicKey();
    
}
