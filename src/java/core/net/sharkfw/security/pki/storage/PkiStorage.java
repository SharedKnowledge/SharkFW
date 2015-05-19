package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author ac
 */
public interface PkiStorage {

    /**
     *
     * @param peerSemanticTag
     * @return SharkCertificate
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag, PublicKey publicKey) throws SharkKBException;

    /**
     *
     * @param sharkCertificate
     */
    void addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException;

    /**
     *
     * @return All stored SharkCertificates
     */
    List<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     *
     * @return FSSharkKB
     */
    FSSharkKB getPkiStorage();
}
