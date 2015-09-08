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
     * @param peerSemanticTag
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag) throws  SharkKBException, InvalidKeySpecException;

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

    /**
     * @return Knowledgebase containing the SharkPkiStorage
     */
    SharkKB getSharkPkiStorageKB();
}
