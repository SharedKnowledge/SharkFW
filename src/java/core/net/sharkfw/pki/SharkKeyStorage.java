/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;

/**
 *
 * @author Sascha Saunus (Matr.Nr.: 540070), Daniel Rockenstein (Matr.Nr.: 539748)
 */
public interface SharkKeyStorage {


    /**
     * Creates a PublicKey and a PrivateKey and stores them
     */
    public void createKeyPair();

    /**
     * Creates a PublicKey and a PrivateKey with a certain format
     * @param format
     */
    public void createKeyPair(String format);
    
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

    /**
     * Saves the pubKey for a peer.
     * @param key
     * @param peer
     */
    void addPublicKey(PublicKey key, PeerSemanticTag peer);

    /**
     * Get the public key for a peer
     * @param certifiedPeer
     * @return
     */
    PublicKey getPublicKey(PeerSemanticTag certifiedPeer);

    /**
     * Creates and saves a certificate with a signature from local peer.
     * @param key
     * @param certifiedPeer
     */
    void signKey(PublicKey key, PeerSemanticTag certifiedPeer);

    /**
     * Creates and saves a certificate with a signature from local peer with a validity duration.
     * @param key
     * @param certifiedPeer
     * @param validity
     */
    void signKey(PublicKey key, PeerSemanticTag certifiedPeer, long validity);

    /**
     * Returns the certificate for the peer. Returns null if no certificate for peer exists
     * @param certifiedPeer
     * @return
     */
    SharkCertificate getCertificate(PeerSemanticTag certifiedPeer);

    /**
     * Returns all certificates signed by Peer certifyingPeer
     * @param certifyingPeer
     * @return
     */
    Iterator<SharkCertificate> getCertificates( PeerSemanticTag certifyingPeer);

    /**
     * Adds a certificate to the certificate list.
     * @param certificate
     */
    void addCertificate(SharkCertificate certificate);

    /**
     * Returns true if a certificate for peer exists
     * @param certifiedPeer
     * @return
     */
    boolean hasCertificate(PeerSemanticTag certifiedPeer);

    /**
     * Removes a certificate specified by the peer
     * @param certifiedPeer
     */
    void removeCertificate(PeerSemanticTag certifiedPeer);

    /**
     * Signs a certificate with signingPeer, signature is created from private key
     * @param certifiedPeer
     * @param signingPeer
     */
    void signCertificate(PeerSemanticTag certifiedPeer, PeerSemanticTag signingPeer);

    /**
     * Returns the Trustlevel for certifiedPeer
     * @param certifiedPeer
     * @return
     */
    int getTrustLevel(PeerSemanticTag certifiedPeer);
}
