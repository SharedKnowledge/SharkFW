package net.sharkfw.security;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 * @author ac
 */
public interface SharkCertificate extends SharkPublicKey {

    String INFO_SIGNER = "INFO_SIGNER";
    String INFO_SIGNATURE = "INFO_SIGNATURE";
    String INFO_SIGNING_DATE = "INFO_SIGNING_DATE";

    String CERTIFICATE_TAG_NAME = "certificate";
    String CERTIFICATE_TAG_SI = "st:certificate";
    SemanticTag CERTIFICATE_TAG = InMemoSharkKB.createInMemoSemanticTag(CERTIFICATE_TAG_NAME, new String[]{CERTIFICATE_TAG_SI});

    /**
     * @return a single Signer
     */
    PeerSemanticTag getSigner();

    /**
     * @return
     */
    byte[] getSignature();

    long signingDate();

}
