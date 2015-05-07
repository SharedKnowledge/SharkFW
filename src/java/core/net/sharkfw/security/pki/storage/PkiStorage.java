package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.security.pki.SharkCertificate;

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
    SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag);

    /**
     *
     * @param sharkCertificate
     */
    void addSharkCertificate(SharkCertificate sharkCertificate);

    /**
     *
     * @return All stored SharkCertificates
     */
    List<SharkCertificate> getSharkCertificateList();
}
