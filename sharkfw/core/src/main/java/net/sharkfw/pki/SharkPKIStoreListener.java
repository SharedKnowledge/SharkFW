package net.sharkfw.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 *
 * @author thsc
 * @author df
 */
public interface SharkPKIStoreListener {
    
    /**
     * Informs the listeners that a certificate became invalid.
     * @param cert The certificate that became invalid.
     */
    public void onCertificateBecameInvalid(SharkCertificate cert);
    
    /**
     * Informs the listeners that a new certificate has been stored in the kb.
     * @param cert The new stored certificate.
     */
    public void onCertificateReached(SharkCertificate cert);
    
    /**
     * Informs the listeners that there has been a new invitation to exchange keys.
     * @param peer The peer that invited the owner for key exchange.
     */
    public void onInviteKeyExchange(PeerSemanticTag peer);
    
    /**
     * Informs the listeners that another peer has revoked the signature for a certificate.
     * @param revokedPeer The peer of which the signature was removed.
     * @param revokingPeer The peer that removed the signature.
     */
    public void onRevokeKeySignature(PeerSemanticTag revokedPeer, PeerSemanticTag revokingPeer);
    
    /**
     * Informs the listeners that the owner certificate will run out shortly.
     */
    public void onCertificateRunsOut();
    
}
