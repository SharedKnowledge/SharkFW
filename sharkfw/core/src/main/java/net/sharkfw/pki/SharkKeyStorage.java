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
     * Removes the public key belonging to a peer from the storage
     * @param peer
     */
    void deletePublicKey(PeerSemanticTag peer);

    /**
     * Get the public key for a peer
     * @param peer
     * @return
     */
    PublicKey getPublicKey(PeerSemanticTag peer);

    /**
     * Creates and saves a certificate with a signature from local peer.
     * @param certifiedPeer
     */
    void signPublicKey(PeerSemanticTag certifiedPeer, PublicKey key);

    /**
     * Creates and saves a certificate with a signature from local peer with a validity duration.
     * @param certifiedPeer
     * @param validity
     */
    void signPublicKey(PeerSemanticTag certifiedPeer, long validity);

    /**
     * Adds a certificate to the certificate list.
     * @param certificate
     */
    void addCertificate(SharkCertificate certificate);

    /**
     * Returns all certificates
     * @return
     */
    Iterator<SharkCertificate> getCertificates();

    /**
     * Returns all certificates signed by Peer certifyingPeer
     * @param certifyingPeer
     * @return
     */
    Iterator<SharkCertificate> getCertificatesBy( PeerSemanticTag certifyingPeer);

    /**
     * Returns all certificates signed by Peer certifyingPeer
     * @param certifiedPeer
     * @return
     */
    Iterator<SharkCertificate> getCertificates( PeerSemanticTag certifiedPeer);

    /**
     * Returns a certificate specified by certified peer and certifying peer. Returns null if specified certificate does not exist
     * @param certifiedPeer
     * @param certifyingPeer
     * @return
     */
    SharkCertificate getCertificate(PeerSemanticTag certifiedPeer, PeerSemanticTag certifyingPeer);

    /**
     * Get the certificate with the highest trustlevel for certified peer, given it is below the default TrustLevel
     * @param certifiedPeer
     * @return
     */
    SharkCertificate getBestVerifiedCertificate(PeerSemanticTag certifiedPeer);

    /**
     * Get the certificate with the highest trustlevel for certified peer, given it is below maxTrustLevel
     * @param certifiedPeer
     * @param maxTrustLevel
     * @return
     */
    SharkCertificate getBestVerifiedCertificate(PeerSemanticTag certifiedPeer, int maxTrustLevel);

    /**
     * Returns all certificates for certified peer below the default TrustLevel
     * @param certifiedPeer
     * @return
     */
    Iterator<SharkCertificate> getVerifiedCertificates(PeerSemanticTag certifiedPeer);

    /**
     * Returns all certificates for certified peer below maxTrustLevel
     * @param certifiedPeer
     * @param maxTrustLevel
     * @return
     */
    Iterator<SharkCertificate> getVerifiedCertificates(PeerSemanticTag certifiedPeer, int maxTrustLevel);

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
     * Returns the Trustlevel for a certificate. 0 = certified by local peer
     * @param certificate
     * @return
     */
    int getTrustLevel(SharkCertificate certificate);

    /**
     * returns true if the TrustLevel of certificate is lower or equal than the default TrustLevel
     * @param certificate
     * @return
     */
    boolean verify(SharkCertificate certificate);

    /**
     * returns true if the TrustLevel of certificate is lower or equal than maxTrustLevel
     * @param certificate
     * @param maxTrustLevel
     * @return
     */
    boolean verify(SharkCertificate certificate, int maxTrustLevel);

    /**
     * Returns the default TrustLevel
     * @return
     */
    int getDefaultTrustLevel();

    /**
     * Sets the default TrustLevel
     * @param level
     */
    void setDefaultTrustLevel(int level);

    /**
     * Deletes all certificates with an expired validity
     */
    void removeInvalidCertificates();
}
