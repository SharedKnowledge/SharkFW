package net.sharkfw.security;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.KnowledgeUtils;
import net.sharkfw.system.SharkException;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by j4rvis on 2/8/17.
 */
public class ASIPSpaceSharkPublicKey implements SharkPublicKey {
    protected SharkKB kb;
    protected ASIPSpace space = null;

    /**
     * Is used to retrieve the already existing spaces
     *
     * @param kb
     * @param space
     */
    public ASIPSpaceSharkPublicKey(SharkKB kb, ASIPSpace space) {
        this.kb = kb;
        this.space = space;
    }

    /**
     * Is used to create a new infoSpace with key and owner
     *
     * @param kb
     * @param owner
     * @param publicKey
     * @param validity
     */
    public ASIPSpaceSharkPublicKey(SharkKB kb, PeerSemanticTag owner, PublicKey publicKey, long validity) {
        this.kb = kb;

        // check if space is already available
        try {
            this.space = kb.createASIPSpace(
                    null,
                    SharkCertificate.CERTIFICATE_TAG,
                    null,
                    owner,
                    null,
                    null,
                    null,
                    ASIPSpace.DIRECTION_NOTHING);

            KnowledgeUtils.setInfoWithName(
                    this.kb,
                    this.space,
                    SharkPublicKey.INFO_OWNER_PUBLIC_KEY,
                    publicKey.getEncoded());

            KnowledgeUtils.setInfoWithName(
                    this.kb,
                    this.space,
                    SharkPublicKey.INFO_RECEIVE_DATE,
                    System.currentTimeMillis());

            KnowledgeUtils.setInfoWithName(
                    this.kb,
                    this.space,
                    SharkPublicKey.INFO_VALIDITY,
                    validity);

        } catch (SharkKBException | IOException e) {
            e.printStackTrace();
        }
    }

    public ASIPSpaceSharkPublicKey() {
    }

    @Override
    public PublicKey getOwnerPublicKey() {
        try {
            ASIPInformation information = KnowledgeUtils.getInfoByName(
                    this.kb,
                    this.space,
                    SharkPublicKey.INFO_OWNER_PUBLIC_KEY);
            if (information != null) {
                byte[] informationContentAsByte = information.getContentAsByte();
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePublic(new X509EncodedKeySpec(informationContentAsByte));
            }
        } catch (SharkKBException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.space.getSender();
    }

    /**
     * TODO where will it be set??
     *
     * @return
     */
    @Override
    public long getValidity(){
        try {
            return KnowledgeUtils.getInfoAsLong(this.kb, this.space, SharkPublicKey.INFO_VALIDITY);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * TODO is this correct?
     *
     * @return
     * @throws SharkException
     */
    @Override
    public byte[] getFingerprint()  {
        byte[] kBytes = getOwnerPublicKey().getEncoded();

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        digest.update((byte) 0x99);
        digest.update((byte) (kBytes.length >> 8));
        digest.update((byte) kBytes.length);
        digest.update(kBytes);

        return digest.digest();
    }

    @Override
    public long receiveDate(){
        try {
            return KnowledgeUtils.getInfoAsLong(this.kb, this.space, SharkPublicKey.INFO_RECEIVE_DATE);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete(){

        // Remove signature
        // TODO not yet implemented
        try {
            kb.removeInformation(KnowledgeUtils.getInfoByName(this.kb, this.space, INFO_OWNER_PUBLIC_KEY), this.space);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

//        kb.removeSpace()
    }
}
