package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PublicKey;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */
public class SharkCertificate implements Certificate {

    private PeerSemanticTag subject;
    private PeerSemanticTag issuer;
    private LinkedList<PeerSemanticTag> transmitterList;
    private PublicKey subjectPublicKey;
    private Date validity;

    /**
     * Constructor.
     *
     * @param subject
     * @param issuer
     * @param subjectPublicKey
     * @param validity
     */
    public SharkCertificate(PeerSemanticTag subject, PeerSemanticTag issuer, LinkedList<PeerSemanticTag> transmitterList, PublicKey subjectPublicKey, Date validity) {
        this.subject = subject;
        this.issuer = issuer;
        this.transmitterList = transmitterList;
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
    public void addTransmitter(PeerSemanticTag peerSemanticTag) {
        transmitterList.addFirst(peerSemanticTag);
    }

    @Override
    public LinkedList<PeerSemanticTag> getTransmitterList() {
        return transmitterList;
    }

    @Override
    public Date getValidity() {
        return validity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharkCertificate that = (SharkCertificate) o;

        if (!subject.equals(that.subject)) return false;
        if (!issuer.equals(that.issuer)) return false;
        if (!transmitterList.equals(that.transmitterList)) return false;
        if (!subjectPublicKey.equals(that.subjectPublicKey)) return false;
        return validity.equals(that.validity);

    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + issuer.hashCode();
        result = 31 * result + transmitterList.hashCode();
        result = 31 * result + subjectPublicKey.hashCode();
        result = 31 * result + validity.hashCode();
        return result;
    }
}
