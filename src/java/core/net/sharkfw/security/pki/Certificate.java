package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

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

    public final static String CERTIFICATE_SEMANTIC_TAG_NAME = "certificate";
    public final static String CERTIFICATE_SEMANTIC_TAG_SI = "st:certificate";
    public final static SemanticTag CERTIFICATE_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(CERTIFICATE_SEMANTIC_TAG_NAME, new String[]{CERTIFICATE_SEMANTIC_TAG_SI});

    public final static String FINGERPRINT_INFORMATION_NAME = "fingerprint";
    public final static String FINGERPRINT_SEMANTIC_TAG_NAME = "fingerprint";
    public final static String FINGERPRINT_SEMANTIC_TAG_SI = "st:fingerprint";
    public final static SemanticTag FINGERPRINT_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(FINGERPRINT_SEMANTIC_TAG_NAME, new String[]{FINGERPRINT_SEMANTIC_TAG_SI});

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
    byte[] getFingerprint();
}
