package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

/**
 * @author ac
 */
public interface PkiStorage {

    /**
     * @return PrivateKey of the KB Owner
     * @throws SharkKBException
     */
    PrivateKey getOwnerPrivateKey() throws SharkKBException;

    /**
     * @param newPrivateKey
     * @throws SharkKBException
     */
    void replaceOwnerPrivateKey(PrivateKey newPrivateKey) throws SharkKBException;

    /**
     * @param peerSemanticTag
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag subject, PublicKey publicKey) throws SharkKBException;

    /**
     * @param peerSemanticTag
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag issuer, PeerSemanticTag subject) throws  SharkKBException, InvalidKeySpecException;

    /**
     * @param sharkCertificate
     * @throws SharkKBException
     */
    boolean addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException, InvalidKeySpecException;

    /**
     * @param sharkCertificate
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    boolean addSharkCertificate(ContextPoint sharkCertificate) throws SharkKBException;

    /**
     * @param sharkCertificateHashSet
     * @throws SharkKBException
     */
    boolean addSharkCertificate(HashSet<SharkCertificate> sharkCertificateHashSet) throws SharkKBException, InvalidKeySpecException;

    /**
     * @return All stored SharkCertificates
     */
    HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException;

    /***
     *
     * @param sharkCertificate
     * @param trustLevel
     * @return
     */
    boolean updateSharkCertificateTrustLevel(SharkCertificate sharkCertificate, Certificate.TrustLevel trustLevel) throws SharkKBException;

    /***
     *
     * @param sharkCertificate
     * @return
     * @throws SharkKBException
     */
    boolean deleteSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException;

    /**
     * @return Knowledgebase containing the SharkPkiStorage
     */
    SharkKB getSharkPkiStorageKB();
}
