package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PublicKey;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */
public interface Certificate {

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
}
