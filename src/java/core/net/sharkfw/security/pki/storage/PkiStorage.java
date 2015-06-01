package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

/**
 * @author ac
 */
public interface PkiStorage {

    /**
     * @param peerSemanticTag
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag, PublicKey publicKey) throws SharkKBException;

    /**
     * @param sharkCertificate
     * @throws SharkKBException
     */
    void addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException;

    /**
     * @param sharkCertificate
     */
    void addSharkCertificate(ContextPoint sharkCertificate);

    /**
     * @param sharkCertificateHashSet
     * @throws SharkKBException
     */
    void addSharkCertificate(HashSet<SharkCertificate> sharkCertificateHashSet) throws SharkKBException;

    /**
     * @return All stored SharkCertificates
     */
    HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * @return Knowledgebase containing the SharkPkiStorage
     */
    SharkKB getSharkPkiStorageKB();
}
