package net.sharkfw.security;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * PkiStorage interface.
 *
 * @author ac
 */
public interface PkiStorage {

    String OWNERSPACE_TAG_NAME = "ownerspace";
    String OWNERSPACE_TAG_SI = "st:ownerspace";
    SemanticTag OWNERSPACE_TAG = InMemoSharkKB.createInMemoSemanticTag(OWNERSPACE_TAG_NAME, OWNERSPACE_TAG_SI);

    List<SharkPublicKey> getUnsignedPublicKeys() throws SharkKBException;

    /**
     * remove pubkey from Infospace
     *
     * @param sharkPublicKey
     */
    SharkCertificate sign(SharkPublicKey sharkPublicKey) throws SharkKBException;

    /**
     * Returns the stored {@link PrivateKey} of the owner.
     *
     * @return PrivateKey of the {@link SharkKB} Owner
     * @throws SharkKBException
     */
    PrivateKey getOwnerPrivateKey() throws SharkKBException, InvalidKeySpecException;

    /**
     * Replaces the stored {@link PrivateKey} of the owner.
     *
     * @param newPrivateKey {@link PrivateKey}
     * @throws SharkKBException {@link SharkKBException}
     */
    void setOwnerPrivateKey(PrivateKey newPrivateKey) throws SharkKBException;

    PrivateKey getOldOwnerPrivateKey() throws SharkKBException;

    PublicKey getOwnerPublicKey() throws SharkKBException;

    PublicKey getOldOwnerPublicKey() throws SharkKBException;

    void generateNewKeyPair();


    List<SharkCertificate> getSharkCertificates(PeerSemanticTag owner) throws SharkKBException;

    /**
     * Returns a {@link SharkCertificate} via {@link PeerSemanticTag} for subject and issuer.
     *
     * @param issuer  {@link PeerSemanticTag}
     * @param subject {@link PeerSemanticTag}
     * @return SharkCertificate
     * @throws SharkKBException
     */
    SharkCertificate getSharkCertificate(PeerSemanticTag issuer, PeerSemanticTag subject) throws SharkKBException;

    List<SharkCertificate> getSignedSharkCertificates(PeerSemanticTag signer) throws SharkKBException;

    /**
     * Add a {@link net.sharkfw.security.SharkCertificate} to the {@link PkiStorage}
     *
     * @param certificate
     * @return
     * @throws SharkKBException
     */
    boolean addSharkCertificate(SharkCertificate certificate) throws SharkKBException;

    SharkCertificate addSharkCertificate(PeerSemanticTag owner, PublicKey ownerKey, long validity, PeerSemanticTag signer, byte[] signature, long dateSigned);

    List<SharkCertificate> getAllSharkCertificates() throws SharkKBException;

    /***
     * Deletes a {@link net.sharkfw.security.SharkCertificate} from the {@link PkiStorage}.
     * @param certificate
     * @return True or false
     * @throws SharkKBException
     */
    boolean deleteSharkCertificate(SharkCertificate certificate) throws SharkKBException;

    boolean verifySharkCertificate(SharkCertificate certificate, PeerSemanticTag signer);
}