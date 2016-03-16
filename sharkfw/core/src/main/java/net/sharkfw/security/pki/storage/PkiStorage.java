package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;

/**
 * PkiStorage interface.
 * @author ac
 */
public interface PkiStorage {

    /**
     * Returns the stored {@link PrivateKey} of the owner.
     * @return PrivateKey of the {@link SharkKB} Owner
     * @throws SharkKBException
     */
    PrivateKey getOwnerPrivateKey() throws SharkKBException;

    /**
     * Replaces the stored {@link PrivateKey} of the owner.
     * @param newPrivateKey {@link PrivateKey}
     * @throws SharkKBException {@link SharkKBException}
     */
    void replaceOwnerPrivateKey(PrivateKey newPrivateKey) throws SharkKBException;

    /**
     * Returns a {@link SharkCertificate} via the {@link PeerSemanticTag} of the subject.
     * @param subject {@link PeerSemanticTag}
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag subject) throws SharkKBException;

    /**
     * Returns a {@link SharkCertificate} via the subjectIdentifier of the subject.
     * @param subjectIdentifier
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(String[] subjectIdentifier) throws SharkKBException;

    /**
     * Returns a {@link SharkCertificate} via the {@link PeerSemanticTag} of the subject and his {@link PublicKey}.
     * @param subject {@link PeerSemanticTag}
     * @param publicKey {@link PeerSemanticTag}
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag subject, PublicKey publicKey) throws SharkKBException;

    /**
     * Returns a {@link SharkCertificate} via {@link PeerSemanticTag} for subject and issuer.
     * @param issuer {@link PeerSemanticTag}
     * @param subject {@link PeerSemanticTag}
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag issuer, PeerSemanticTag subject) throws  SharkKBException;

    /**
     * Adds a SharkCertificate to the {@link PkiStorage}.
     * @param sharkCertificate {@link SharkCertificate}
     * @throws SharkKBException
     */
    boolean addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException;

    /**
     * Adds a {@link ContextPoint} containing the certificate information's to the {@link PkiStorage}.
     * @param sharkCertificate {@link SharkCertificate}
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    boolean addSharkCertificate(ContextPoint sharkCertificate) throws SharkKBException;

    /**
     * Adds a HashSet of SharkCertificates to the {@link PkiStorage}.
     * @param sharkCertificateHashSet {@link SharkCertificate}
     * @throws SharkKBException
     */
    boolean addSharkCertificate(HashSet<SharkCertificate> sharkCertificateHashSet) throws SharkKBException;

    /**
     * Returns a {@link HashSet} of all stored SharkCertificates.
     * @return HashSet of all stored SharkCertificates
     */
    HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException;

    /***
     * Changes a ShakCertificate TrustLevel.
     * @param sharkCertificate {@link SharkCertificate}
     * @param trustLevel {@link net.sharkfw.security.pki.Certificate.TrustLevel}
     * @return True or false
     */
    boolean updateSharkCertificateTrustLevel(SharkCertificate sharkCertificate, Certificate.TrustLevel trustLevel) throws SharkKBException;

    /***
     * Deletes a SharkCertificate from the {@link PkiStorage}.
     * @param sharkCertificate {@link SharkCertificate}
     * @return True or false
     * @throws SharkKBException
     */
    boolean deleteSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException;

    /**
     * Returns the used {@link SharkKB} reflecting the {@link PkiStorage}
     * @return {@link SharkKB} reflecting the S{@link SharkPkiStorage}
     */
    SharkKB getSharkPkiStorageKB();
}