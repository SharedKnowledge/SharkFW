package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PublicKey;
import java.util.Date;

/**
 * @author ac
 */
public class SharkPkiCertificate implements Certificate {

    private PeerSemanticTag subject;
    private PeerSemanticTag issuer;
    private PublicKey subjectPublicKey;
    private Date validity;

    /**
     * Constructor.
     * @param subject
     * @param issuer
     * @param subjectPublicKey
     * @param validity
     */
    public SharkPkiCertificate(PeerSemanticTag subject, PeerSemanticTag issuer, PublicKey subjectPublicKey, Date validity) {
        this.subject = subject;
        this.issuer = issuer;
        this.subjectPublicKey = subjectPublicKey;
        this.validity = validity;
    }

    @Override
    public PublicKey getSubjectPublicKey() {
        return subjectPublicKey;
    }

    @Override
    public PeerSemanticTag getSubject() {
        return subject;
    }

    @Override
    public PeerSemanticTag getIssuer() {
        return issuer;
    }

    @Override
    public Date getValidity() {
        return validity;
    }
}
