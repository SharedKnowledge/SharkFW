package net.sharkfw.security;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.KnowledgeUtils;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Created by j4rvis on 2/7/17.
 */
public class ASIPSpaceSharkCertificate extends ASIPSpaceSharkPublicKey implements SharkCertificate {

    private PeerSemanticTag signer;

    public ASIPSpaceSharkCertificate(SharkKB kb, ASIPSpace space, PeerSemanticTag signer) throws SharkKBException {
        super(kb, space);
        this.signer = signer;

        SharkCSAlgebra.isIn(this.space.getApprovers(), signer);
    }

    public ASIPSpaceSharkCertificate(SharkKB kb, PeerSemanticTag owner, PublicKey publicKey, long validity,
                                     PeerSemanticTag signer, byte[] signature, long signingDate) {

        super(kb, owner, publicKey, validity);
        PeerSTSet approvers = this.space.getApprovers();

        try {
            if (approvers == null) {
                PeerSTSet peerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
                peerSTSet.merge(signer);
                ASIPInterest interest = SharkCSAlgebra.Space2Interest(this.space);
                // TODO Will the approver really be set??
                interest.setApprovers(peerSTSet);
            } else {
                approvers.merge(signer);
            }

            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_SIGNATURE, signature);
            KnowledgeUtils.setInfoWithName(this.kb, this.space, INFO_SIGNING_DATE, signingDate);

        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PeerSemanticTag getSigner() {
        return this.signer;
    }

    @Override
    public byte[] getSignature() throws SharkKBException {
        String signatureName = SharkCertificate.INFO_SIGNATURE + "_" + signer.getSI()[0];
        ASIPInformation information = KnowledgeUtils.getInfoByName(this.kb, this.space, signatureName);
        if (information != null) {
            return information.getContentAsByte();
        }
        return null;
    }

    @Override
    public long signingDate() throws SharkKBException {
        return KnowledgeUtils.getInfoAsLong(this.kb, this.space, INFO_SIGNING_DATE);
    }

    @Override
    public void delete() throws SharkKBException {
        PeerSTSet approvers = this.space.getApprovers();

        // Remove signature
        // TODO not yet implemented
        kb.removeInformation((Information) KnowledgeUtils.getInfoByName(this.kb, this.space, INFO_SIGNATURE), this.space);

        // Remove all information if signer is last approver
        // TODO not yet implemented
        if (approvers.size() == 1) {
            kb.removeInformation(this.space);
        }

        // remove approver from space
        approvers.removeSemanticTag(signer);
    }
}
