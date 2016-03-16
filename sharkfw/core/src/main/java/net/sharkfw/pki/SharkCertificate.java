package net.sharkfw.pki;

import java.security.PublicKey;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkPKVerifiyException;

/**
 *
 * @author thsc
 * @author df
 */
public interface SharkCertificate {    
    
    public static final String SHARK_CERTIFICATE_SI = "http://www.sharkfw.net/vocabulary/certificate";
    
    /**
     * Return the public key itself.
     * @return 
     */
    public PublicKey getPublicKey() throws SharkKBException;
    
    /**
     * Get the peer to which this public key belongs.
     * @return 
     */
    public PeerSemanticTag getCertifiedPeer();
    
    /**
     * Peers that (promised to have) got this public key first hand 
     * from peer and signed it.
     * @return 
     */
    public List<SigningPeer> getSigningPeers();
    
    /**
     * Peer set has sent this certificate. It can be certified peer, 
     * signing peer but also another peer.
     * 
     * Note: Shark implementations of this interface will only add 
     * certificates from sender which can be verified.
     * 
     * Thus, no Shark system will accept a certificate from an unknown
     * peer. There must be a network of trust.
     * 
     * @return 
     */
    public PeerSemanticTag getSender();
    
    /**
     * Trustlevel this certificate has right now. Will be Gold (0) if we got this first hand
     * via secure channel (NFC, Wifi-Direct). Will be Silver (1) at least if another protocol was used.
     * Will raise 1 per peer who got this certificate and sent it further.
     * @return The trust level as int value
     * @throws SharkKBException 
     */
    public int trustLevel() throws SharkKBException;
    
    /**
     * Define whether this certificate shall be shared with other peers
     * or not. Background: Peers can ask others peer for certificates. 
     * A peer will send any certificate that it is willing to share.
     * 
     * This method overwrites any standard setting made by the PKI store.
     * @param share 
     */
    public void share(boolean share);
    
    /**
     * Adds a signature to the certificate after this signature has
     * been verified. 
     * @param signingPeer The peer that signed the public key.
     * @param signature The signature for that public key.
     * @throws SharkKBException if the peer already signed this certificate.
     * @throws SharkPKVerifiyException if signature of signing peer could not be verified.
     */
    public void addSignatureToPublicKey(PeerSemanticTag signingPeer, byte[] signature) throws SharkKBException, SharkPKVerifiyException;
    
    /**
     * Removes a signature from the certificate.
     * @param peer The peer of which the signature has to be removed.
     * @throws SharkKBException if there is no signature by the signing peer, meaning we already removed it
     */
    public void removeSignatureFromPublicKey(PeerSemanticTag peer) throws SharkKBException;
    
    /**
     * Checks if there is a signature for this certificate by the given peer.
     * <br>Note:</br> This does not say anything about the validity of that signature.
     * Check that with <code>isVerifiedBy(PeerSemanticTag peer)</code>
     * @param peer The peer that might have signed this certificate.
     * @return true if there is a signature by that peer for this certificate, false otherwise
     */
    public boolean isSignedBy(PeerSemanticTag peer);
    
    /**
     * Checks if there is a valid signature for this certificate by the given peer.
     * @param peer The peer that might have signed this certificate.
     * @return true if there is a valid signature by that peer for this certificate, false otherwise
     */
    public boolean isVerifiedBy(PeerSemanticTag peer);
    
    /**
     * Returns if the certificate is still valid regarding the date.
     * @return true if certificate is still valid, false otherwise
     */
    public boolean isStillValid();
    
    /**
     * Returns the date to what this certificate will be valid.
     * @return the date
     */
    public long getValidity();
    
    /**
     * Returns the context coordinates of die certificate
     * @return 
     */
    public ContextCoordinates getContextCoordinates();

}
