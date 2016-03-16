package net.sharkfw.pki;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkPKVerifiyException;
import net.sharkfw.system.SharkSecurityException;
/**
 *
 * A peer can and should share certificates. Thus, whenever a peer
 * asks for certificates - it will reply.
 * 
 * Two setting are possible:
 * 
 * <ul>
 * <li>With whom will this peer shared certificates at all. 
 * Standard behaviour is - any peer with the maxTrustLevel. Use
 * setMaxTrustLevel to set this parameter. Standard is 1.
 * <li>What certificates shall be shared. Standard setting is 1. Thus,
 * any certificate with level 1 or 0 will be shared. This setting can be 
 * changed with setMaxShareLevel();
 * </ul>
 * ...
 * @author thsc
 * @author df
 */
public interface SharkPublicKeyStorage {
    
    /**
     * Sets the pki knowledge port for the storage
     * @param port The PKIKnowledgePort
     */
    public void setPKIKnowledgePort(StandardKP port);
    
    /**
     * return public key of a peer described by it's subject identifier.
     * The first matching peer ist taken and its key ist returned.
     * @param si si describing the peer
     * @return RSA public key
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public PublicKey getPublicKey(String si[]) throws SharkKBException;
    
    public PublicKey getPublicKey(PeerSemanticTag peer) throws SharkKBException;
    
    public PrivateKey getPrivateKey() throws SharkKBException;
    
    /**
     * Set private key of this peer.
     * @param privateKey
     * @throws SharkKBException 
     */
    public void setPrivateKey(PrivateKey privateKey) throws SharkKBException;
    
    /**
     * Create key pair for this peer with the given format, e.g. RSA.
     * See http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     * for information about standard algorithm names.
     * <br/><b>Note</b> This is a blocking sending method as we inform other peers of our new certificate. Call it in a thread.
     * @param format
     * @throws SharkKBException 
     */
    public void createKeyPair(String format) throws SharkKBException;
    
    /**
     * Creates a key pair with the default format.
     * Currently that's RSA.
     * @throws SharkKBException 
     */
    public void createKeyPair() throws SharkKBException;
    
    /**
     * Adds the public key of another peer to our pki store.
     * If we already have stored that public key for that peer we only update
     * the signatures.
     * @param pk The public key to store.
     * @param peer The peer the public key belongs to.
     * @param signatures Signatures for that public key. Can be null if no one has signed the public key.
     * @param validity Time this certificate will be valid
     * @return The certificate for the public key.
     * @throws SharkKBException if certificate can not be created
     * @deprecated create certificate for each signing peer
     */
    public SharkCertificate addPublicKey(PublicKey pk, PeerSemanticTag peer, List<SigningPeer> signatures, long validity) throws SharkKBException;

    /**
     * Adds the public key of another peer to our pki store.
     * If we already have stored that public key for that peer we only update
     * the signatures.
     * @param pk The public key to store.
     * @param peer The peer the public key belongs to.
     * @param signature Signature for that public key. Can be null if no one has signed the public key.
     * @param validity Time this certificate will be valid
     * @return The certificate for the public key.
     * @throws SharkKBException if certificate can not be created
     */ 
    public SharkCertificate addPublicKey(PublicKey pk, PeerSemanticTag peer, SigningPeer signature, long validity) throws SharkKBException;
    
    /**
     * This signs a public key of that peer. This method must only be used
     * by the peer who got the public key first hand from the peer.
     * @param peer The peer the public key of has to be signed.
     * @throws SharkKBException if peer has no public key
     * @throws net.sharkfw.system.SharkPKVerifiyException
     */
    public void signPublicKey(PeerSemanticTag peer) throws SharkKBException, SharkPKVerifiyException;
    
    /**
     * Defines how long certificates are valid. 
     * 
     * The maximal number is one year.
     * 
     * @param duration 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public void setValidity(long duration) throws SharkKBException;
    
    /**
     * Returns the certificate for the given peer.
     * 
     * @param peer
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public SharkCertificate getCertificate(PeerSemanticTag peer) throws SharkKBException;

    /**
     * Returns certificate trust level.
     * 
     * The path of trust is defined as this:
     * 
     * <ul>
     * <li> Each certificate that was directly received by this peer and signed
     * by this peer has - per definition - a path length of zero. Zero means: 
     * The peer itself is convinced that the public key is from a peer. It has 
     * no doubts. Those peers are called "direct" peers in context of Shark PKI.
     * <li> Direct peers can also get and sign public keys. A certificate that
     * comes from a direct peer and is issued by a direct peer has has a
     * path of trust length of one. There is just one peer between this peer and 
     * the certifite peer. We also speek of level one peers.
     * <li>Direct peer are also called <i>level 0</i> peers. This peer got the
     * public key directly. Level 1 certificates have one indirection. This 
     * definition can generalized: A certificate that is issued by a level <code>n</code>
     * peer is a level <code>n+1</code> certicate and so forth.
     * </ul>
     * 
     * As a rule of thumb, a trust level higher than four shouldn't be accepted.
     *
     * But this is just a guess. There might be applications that only accept
     * direct peers. There can also be applications who actually don't care about
     * the level at all and are satisfied if at least one path of trust can be
     * found.
     * 
     * @param cert
     * @return Level of trust - 0 is best. -1 is returned of no path of trust can
     * be found at all.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public int trustLevel(SharkCertificate cert) throws SharkKBException;
    
    /**
     * Returns the trustlevel for the given peer in relation to the owner.
     * @param peer The peer to calculate the trustlevel for.
     * @return The trustlevel as integer.
     * @throws SharkKBException 
     */
    public int trustLevel(PeerSemanticTag peer) throws SharkKBException;
    
    /**
     * Returns peers which we want to send certificates to defined by our filter rules.
     * This does not mean they store the certificates we send them, that depends on incoming
     * filter rules on receivers side.
     * @return An iterator over the peers that matches the filter rules.
     * @throws SharkKBException if filter rule is unknown
     */
    public Iterator<PeerSemanticTag> getPeersToSendCertificatesTo() throws SharkKBException;
    
    /**
     * Returns peers we want to receive certificates from.
     * @return 
     * @throws SharkKBException if there are no peers
     */
    public Iterator<PeerSemanticTag> getPeersToReceiveCertFrom()  throws SharkKBException;
    
    /**
     * Set maximum of certificate trust level which is shared
     * Default is 1.
     * @param maxLevel 
     */
    public void setMaxCertSharingLevel(int maxLevel);

    public int getCertSharingLevel();
    
    /**
     * That maximum trust level of peer with whom this peer shares certificates
     * Default is 1.
     * @param maxLevel 
     */
    public void setMaxTrustedPeersLevel(int maxLevel);
    public int getMaxTrustedPeersLevel();

    /**
     * Switch pki communication on or off.
     * @param on 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public void setSharing(boolean on) throws SharkKBException;
    
    /**
     * Sets the mode to determin if we are interested in sending certificates to
     * a peer.
     * @param mode The mode that should be used.
     */
    public void setCertificateSendMode(int mode);
    
    /**
     * Gets the mode to determin if we are interested in sending certificates to
     * a peer.
     * @return the mode
     */
    public int getCertificateSendMode();
    
    /**
     * Sets the mode to determin if we are interested in receiving certificates 
     * from a peer.
     * @param mode The mode that should be used.
     */
    public void setCertificateReceiveMode(int mode);
    
    /**
     * Gets the mode to determin if we are interested in receiving certificates 
     * from a peer.
     * @return the mode
     */
    public int getCertificateReceiveMode();
    
    /**
     * Issues an interest to submit (trusted) certificates to all
     * trusted peers. Peer can send in incomming interest as reply
     * which will cause this peer to send those certificates.
     * 
     * @throws SharkKBException 
     */
    public void publishTrusted() throws SharkKBException;
    
    /**
     * Sends all certificates that shall be shared to the remote peer.
     * @param remotePeer
     * @throws SharkKBException
     * @throws SharkSecurityException 
     */
    public void sendCertificates(PeerSemanticTag remotePeer) throws SharkKBException, SharkSecurityException;
    
    /**
     * Returns the cp for certificates
     * @return 
     */
    public Enumeration<ContextPoint> getCertificateCPs();
    
    public Iterator<SharkCertificate> certificates() throws SharkKBException;

    /**
     * @return Owner of this PKI store - it also owner of the only
     * stored private key
     */
    public PeerSemanticTag getOwner();
    
    /**
     * Checks if a given peer has a certificate stored in our kb.
     * @param peer The peer to check.
     * @return true if certificate is found, false otherwise
     */
    public boolean hasCertificate(PeerSemanticTag peer);
    
    /**
     * Removes the certificate for the given peer. Also removes all signatures.
     * <br/><b>Note</b> This is a blocking sending method as we inform other peers of revoking
     * our certificate or signaure. Call it in a thread.
     * @param peer The peer the public key of shall be removed.
     * @throws SharkKBException if there is no certificate to remove
     */
    public void removeCertificate(PeerSemanticTag peer) throws SharkKBException;
    
    /**
     * Checks if we are currently interested in receiving certificates by the
     * given peer.
     * @param sender The peer that sent us the certificate
     * @param puk The public key of the peer
     * @param signature The signature the sender signed the certificate.
     * @return true if we care and should store it, false otherwise
     */
    public boolean doICareAboutCertificate(PeerSemanticTag sender, byte[] puk, byte[] signature);

    /**
     * Stores received knowledge in the temporary pki storage for later validation.
     * @param data The data to be stored
     * @return the unique id of the stored information or null if information could not be stored
     * @throws SharkKBException if knowledge cannot be stored
     */
    public String insertTemporaryKnowledge(byte[] data) throws SharkKBException;
    
    /**
     * Assimilates temporary stored knowledge and inserts the certificate into
     * the pki storage.
     * @param uniqueID The unique id of the information.
     * @param salt The salt for this message that was used to create the fingerprint.
     * @return true if knowledge was assimilated, false otherwise
     * @throws SharkKBException if knowledge with uniqueID cannot be found
     */
    public boolean assimilateTemporaryKnowledge(String uniqueID, String salt) throws SharkKBException;
    
    /**
     * Returns an iterator over all unique ids of the stored but not yet assimilated knowledge.
     * @return The iterator over all id for the knowledge
     */
    public Iterator<String> getTemporaryKnowledge();
    
    /**
     * Returns the peer the certificate is for.
     * @param id The id of the message.
     * @return The peer the certificate is for.
     * @throws SharkKBException  if knowledge with uniqueID cannot be found
     */
    public PeerSemanticTag getPeerByUniqueId(String id) throws SharkKBException;
    
    /**
     * Returns the sending peer tag of a peer that sent this knowledge.
     * @param id The id of the message.
     * @return The peer that sent us the knowledge.
     * @throws SharkKBException  if knowledge with uniqueID cannot be found
     */
    public PeerSemanticTag getSendingPeerByUniqueId(String id) throws SharkKBException;
    
    /**
     * Returns the list of signing peers. Only the peers that can be verified will be returned.
     * @param id The id of the message.
     * @return A list of signing peers, an empty list if there is none
     * @throws SharkKBException if knowledge with unique id was not found
     */
    public List<SigningPeer> getSigningPeersByUniqueId(String id) throws SharkKBException;
    
    /**
     * Returns a byte array of the PKIMSPeerPackage class with information about the owner
     * to send those data to another peer.
     * @param aboutPeer The peer we send the certificate of.
     * @param salt The salt that should be used for calculating the finger print.
     * @return The byte array
     * @throws SharkKBException  if owner has no certificate
     */
    public byte[] getPKIMSPeerPackage(PeerSemanticTag aboutPeer, String salt) throws SharkKBException;
    
    /**
     * Adds the given peer to the list of peers we accept certificates from.
     * @param peer The peer to add.
     * @return true if the peer was added or is already on the list, falso otherwise
     */
    public boolean addPeerToAcceptFromList(PeerSemanticTag peer);
    
    /**
     * Adds the given peer to the list of peers we send certificates to.
     * @param peer The peer to add.
     * @return true if the peer was added or is already on the list, falso otherwise
     */
    public boolean addPeerToSendToList(PeerSemanticTag peer);
    
    /**
     * Removes a peer from the list of peer we accept certificates from.
     * @param peer The peer to remove from list.
     * @return true if the peer is removed or wasn't on the list, false otherwise
     */
    public boolean removePeerFromAcceptFromList(PeerSemanticTag peer);
    
    /**
     * Removes a peer from the list of peer we send certificates to.
     * @param peer The peer to remove from list.
     * @return true if the peer is removed or wasn't on the list, false otherwise
     */
    public boolean removePeerFromSendToList(PeerSemanticTag peer);
    
    /**
     * Adds a listener of the class SharkPKIStoreListener.
     * @param listener The listener.
     */
    public void addSharkPKIStoreListener(SharkPKIStoreListener listener);
    
    /**
     * Removes the given listener.
     * @param listener The listener to remove.
     */
    public void removeSharkPKIStoreListener(SharkPKIStoreListener listener);
}
