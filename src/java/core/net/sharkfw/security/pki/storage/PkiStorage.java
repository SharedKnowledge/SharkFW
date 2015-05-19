package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.security.pki.SharkPkiCertificate;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author ac
 */
public interface PkiStorage {

    /**
     *
     * @param peerSemanticTag
     * @return SharkPkiCertificate
     */
    SharkPkiCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag);

    /**
     *
     * @param sharkPkiCertificate
     */
    void addSharkCertificate(SharkPkiCertificate sharkPkiCertificate) throws SharkKBException;

    /**
     *
     * @return All stored SharkCertificates
     */
    List<SharkPkiCertificate> getSharkPkiCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     *
     * @return FSSharkKB
     */
    FSSharkKB getPkiStorage();
}
