package net.sharkfw.security;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
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
 * Created by j4rvis on 2/7/17.
 */
public class ASIPSpaceSharkCertificate implements SharkCertificate {

    private PeerSemanticTag signer;
    private SharkKB kb;
    private ASIPSpace space;

    public ASIPSpaceSharkCertificate(SharkKB kb, ASIPSpace space, PeerSemanticTag signer) {
        this.kb = kb;
        this.space = space;
        this.signer = signer;
    }

    public ASIPSpaceSharkCertificate(
            SharkKB kb, PeerSemanticTag owner, PublicKey publicKey, long validity,
            PeerSemanticTag signer, byte[] signature, long signingDate) {

        this.kb = kb;
        try {
            this.space = this.kb.createASIPSpace(
                    null,
                    SharkCertificate.CERTIFICATE_TAG,
                    signer,
                    owner,
                    null,
                    null,
                    null,
                    ASIPSpace.DIRECTION_INOUT);

            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_OWNER_PUBLIC_KEY, publicKey.getEncoded());
            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_VALIDITY, validity);
            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_SIGNATURE + "_" + signer.getName(), signature);
            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_SIGNING_DATE + "_" + signer.getName(), signingDate);
            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_RECEIVE_DATE + "_" + signer.getName(), System.currentTimeMillis());
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public long getValidity() {
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
    public byte[] getFingerprint() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String concatenatedDataSet = this.getOwner().getName() + this.signer.getName() + this.getOwnerPublicKey().toString() + this.getValidity();
        return messageDigest.digest(concatenatedDataSet.getBytes());
//        byte[] kBytes = getOwnerPublicKey().getEncoded();
//
//        MessageDigest digest = null;
//        try {
//            digest = MessageDigest.getInstance("SHA1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        digest.update((byte) 0x99);
//        digest.update((byte) (kBytes.length >> 8));
//        digest.update((byte) kBytes.length);
//        digest.update(kBytes);
//
//        return digest.digest();
    }

    @Override
    public long receiveDate() {
        try {
            return KnowledgeUtils.getInfoAsLong(this.kb, this.space, SharkPublicKey.INFO_RECEIVE_DATE + "_" + signer.getName());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public PeerSemanticTag getSigner() {
        return this.signer;
    }

    @Override
    public byte[] getSignature() {
        String signatureName = SharkCertificate.INFO_SIGNATURE + "_" + signer.getName();
        ASIPInformation information = null;
        try {
            information = KnowledgeUtils.getInfoByName(this.kb, this.space, signatureName);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        if (information != null) {
            return information.getContentAsByte();
        }
        return null;
    }

    @Override
    public long signingDate() {
        try {
            return KnowledgeUtils.getInfoAsLong(this.kb, this.space, INFO_SIGNING_DATE + "_" + signer.getName());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete() {
        PeerSTSet approvers = this.space.getApprovers();

        // Remove signature
        // TODO not yet implemented
        try {
            kb.removeInformation(KnowledgeUtils.getInfoByName(this.kb, this.space, INFO_SIGNATURE + "_" + signer.getName()), this.space);
            kb.removeInformation(KnowledgeUtils.getInfoByName(this.kb, this.space, INFO_SIGNING_DATE + "_" + signer.getName()), this.space);
            kb.removeInformation(KnowledgeUtils.getInfoByName(this.kb, this.space, INFO_RECEIVE_DATE + "_" + signer.getName()), this.space);
            // Remove all information if signer is last approver
            // TODO not yet implemented
            if (approvers.size() == 1) {
                kb.removeInformation(this.space);
            }

            // remove approver from space
            approvers.removeSemanticTag(signer);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

    }
}
