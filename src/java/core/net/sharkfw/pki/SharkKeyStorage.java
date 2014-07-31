/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Sascha Saunus, Daniel Rockenstein
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
     * Creates a certificate from key for peer. Adds this certificate to its list of certificates.
     * @param key
     * @param peer
     */
    void addKey(PublicKey key, PeerSemanticTag peer);

    /**
     * Creates a certificate from key for peer. Adds this certificate to its list of certificates.
     * @param key
     * @param peer
     * @param validity
     */
    void addKey(PublicKey key, PeerSemanticTag peer, long validity);

    /**
     * Returns the certificate for the peer. Returns null if no certificate for peer exists
     * @param peer
     * @return
     */
    SharkCertificate getCertificate(PeerSemanticTag peer);

    /**
     * Adds a certificate to the certificate list.
     * @param certificate
     */
    void addCertificate(SharkCertificate certificate);

    /**
     * Get the public key for a peer
     * @param certifiedPeer
     * @return
     */
    PublicKey getPublicKey(PeerSemanticTag certifiedPeer);

    /**
     * Returns true if a certificate for peer exists
     * @param peer
     * @return
     */
    boolean hasCertificate(PeerSemanticTag peer);

    /**
     * Removes a certificate specified by the peer
     * @param peer
     */
    void removeCertificate(PeerSemanticTag peer);

    /**
     * Signs a certificate with signingPeer, signature is created from private key
     * @param certifiedPeer
     * @param signingPeer
     */
    void signCertificate(PeerSemanticTag certifiedPeer, PeerSemanticTag signingPeer);

    /**
     * Removes the signature of signingPeer from the certificate of certifiedPeer
     * @param certifiedPeer
     * @param signingPeer
     */
    void removeSignature(PeerSemanticTag certifiedPeer, PeerSemanticTag signingPeer);

    /**
     * Returns the Trustlevel for certifiedPeer
     * @param certifiedPeer
     * @return
     */
    int getTrustLevel(PeerSemanticTag certifiedPeer);
}
