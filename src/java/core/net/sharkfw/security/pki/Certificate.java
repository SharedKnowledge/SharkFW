package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */
public interface Certificate {

    public enum TrustLevel {
        FULL,
        MARGINAL,
        NONE,
        UNKNOWN
    }

    /**
     * @return PublicKey of the subject.
     */
    PublicKey getSubjectPublicKey();

    /**
     * @return PeerSemanticTag of the subject.
     */
    PeerSemanticTag getSubject();

    /**
     * @return PeerSemanticTag of the issuer.
     */
    PeerSemanticTag getIssuer();

    /**
     * @param trustLevel
     */
    void setTrustLevel(TrustLevel trustLevel);

    /**
     * @return TrustLevel
     */
    TrustLevel getTrustLevel();

    /**
     *
     */
    void addTransmitter(PeerSemanticTag peerSemanticTag);

    /**
     * @return List of originator
     */
    LinkedList<PeerSemanticTag> getTransmitterList();

    /**
     * @return Validity of the certificate.
     */
    Date getValidity();

    /**
     *
     * @return Fingerprint byte array
     */
    byte[] getFingerprint() throws NoSuchAlgorithmException;
}
