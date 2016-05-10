package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.system.SharkException;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */

//TODO: Serializable only needed because PKI is not migrated to new SharkFW Version

public class SharkCertificate implements Certificate, Serializable {

    public final static long serialVersionUID = 667;

    private PeerSemanticTag subject;
    private PeerSemanticTag issuer;
    private LinkedList<PeerSemanticTag> transmitterList;
    private PublicKey subjectPublicKey;
    private TrustLevel trustLevel;
    private Date validity;

    public SharkCertificate() {

    }

    /**
     * Constructor
     *
     * @param subject {@link PeerSemanticTag}
     * @param issuer {@link PeerSemanticTag}
     * @param transmitterList {@link LinkedList}
     * @param trustLevel {@link net.sharkfw.security.pki.Certificate.TrustLevel}
     * @param subjectPublicKey {@link PublicKey}
     * @param validity {@link Date}
     */
    public SharkCertificate(PeerSemanticTag subject, PeerSemanticTag issuer, LinkedList<PeerSemanticTag> transmitterList, TrustLevel trustLevel, PublicKey subjectPublicKey, Date validity) {
        this.subject = subject;
        this.issuer = issuer;
        this.transmitterList = transmitterList;
        this.subjectPublicKey = subjectPublicKey;
        this.trustLevel = trustLevel;
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
    public void setTrustLevel(TrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    @Override
    public TrustLevel getTrustLevel() {
        return this.trustLevel;
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

    /***
     * Calculates the fingerprint of the certificate
     * @return SHA-256 fingerprint based of a string from the concatenated fields of the certificate
     * @throws SharkException
     */
    @Override
    public byte[] getFingerprint() throws SharkException {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new SharkException(e.getMessage());
        }
        String concatenatedDataSet = this.subject.getName() + this.issuer.getName() + this.subjectPublicKey.toString() + this.validity.toString();
        return messageDigest.digest(concatenatedDataSet.getBytes());
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

    public byte[] serialize() {
        try {
            final ByteArrayOutputStream b = new ByteArrayOutputStream();
            final ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(this);
            return b.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SharkCertificate deserialize(byte[] bytes) {
        try {
            final ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            final ObjectInputStream o = new ObjectInputStream(b);
            final Object obj = o.readObject();
            final SharkCertificate certificate = (SharkCertificate) obj;
            return certificate;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
