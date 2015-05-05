package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PublicKey;
import java.util.Date;

/**
 * @author ac
 */
public interface Certificate {

    /**
     *
     * @return PublicKey of the subject.
     */
    PublicKey getSubjectPublicKey();

    /**
     *
     * @return PeerSemanticTag of the subject.
     */
    PeerSemanticTag getSubject();

    /**
     *
     * @return PeerSemanticTag of the issuer.
     */
    PeerSemanticTag getIssuer();

    /**
     *
     * @return Validity of the certificate.
     */
    Date getValidity();
}
