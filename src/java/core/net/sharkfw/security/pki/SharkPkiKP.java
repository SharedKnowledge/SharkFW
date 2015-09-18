package net.sharkfw.security.pki;

import com.sun.deploy.security.CertType;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;

/*
    TODO: Incoming knowledge
        - check if incoming knowledge contains one or several certificate
            - compare semantic tag with the attached information to the context point
        - extract one or several certificate
        - add certificate to the sharkPkiStorage (this assumed the filtering**)
            - question: should the storage or the kp uses filter like trust level or issuer lists?
            - question: should the filtering be fixed or adjustable

            **progress
                - check if owner and issuer already exist in the sharkPkiStorage
                - check if the trust level matches (after it has been updated)
                - check if the issuer list matches (limited or open for all peers)
                - check validity of the certificate
    TODO: Outgoing knowledge
        - process interest for a certificate to the given peer or peers
 */

/**
 * @author ac
 */
public class SharkPkiKP extends KnowledgePort {

    private SharkPkiStorage sharkPkiStorage;
    private Interest interest;
    private Certificate.TrustLevel trustLevel;
    private TrustedIssuer trustedIssuer;
    private PeerSTSet peerSTSet;

    public final static String KP_CERTIFICATE_VALIDATION_TAG_NAME = "validation";
    public final static String KP_CERTIFICATE_VALIDATION_TAG_SI = "validate:certificate";
    public final static SemanticTag KP_CERTIFICATE_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(KP_CERTIFICATE_VALIDATION_TAG_NAME, new String[]{KP_CERTIFICATE_VALIDATION_TAG_SI});

    public SharkPkiKP(SharkEngine se, SharkPkiStorage sharkPkiStorage, Certificate.TrustLevel trustLevel, PeerSTSet trustedIssuer) {
        super(se);
        this.sharkPkiStorage = sharkPkiStorage;
        this.trustLevel = trustLevel;
        this.peerSTSet = trustedIssuer;
    }

    @Override //incoming knowledge
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), SharkPkiStorage.PKI_CONTEXT_COORDINATE) &&
                    Collections.list(peerSTSet.peerTags()).contains(cp.getContextCoordinates().getRemotePeer())) {
                try {
                    if(sharkPkiStorage.addSharkCertificate(cp)) {
                        this.notifyKnowledgeAssimilated(this, cp);
                    } else {
                        L.d("Certificate already in SharkPkiStorage.");
                    }
                } catch (SharkKBException e) {
                   new SharkKBException(e.getMessage());
                }
            }

            if(SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), Certificate.FINGERPRINT_COORDINATE) &&
                    Collections.list(peerSTSet.peerTags()).contains(cp.getContextCoordinates().getRemotePeer())) {
                System.out.println("Received fingerprint: " + cp.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte());
                //TODO: Compare fingerprint
            }
        }
    }

    @Override //outgoing knowledge
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        try {
            ArrayList<SemanticTag> listOfTopics = Collections.list(interest.getTopics().tags());
            for(int i = 0; i < listOfTopics.size(); i++) {
                //Certificate validation
                if(SharkCSAlgebra.identical(listOfTopics.get(i), KP_CERTIFICATE_COORDINATE)) {
                    if(Collections.list(interest.getPeers().peerTags()).size() == Collections.list(interest.getRemotePeers().tags()).size()) {
                        for(int j = 0; j < Collections.list(interest.getPeers().peerTags()).size(); j++) {
                            SharkCertificate sc = sharkPkiStorage.getSharkCertificate((PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(j), (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(j));
                            if(sc != null) {
                                ContextCoordinates contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                                        Certificate.FINGERPRINT_COORDINATE,
                                        null,
                                        sc.getSubject(),
                                        sc.getIssuer(),
                                        null,
                                        null,
                                        SharkCS.DIRECTION_INOUT);

                                Knowledge knowledge = InMemoSharkKB.createInMemoKnowledge();
                                ContextPoint contextPoint = InMemoSharkKB.createInMemoContextPoint(contextCoordinatesFilter);

                                Information publicKey = contextPoint.addInformation();
                                publicKey.setName(Certificate.FINGERPRINT_INFORMATION_NAME);
                                publicKey.setContent(sc.getFingerprint());

                                knowledge.addContextPoint(contextPoint);

                                this.sendKnowledge(knowledge, interest.getOriginator());
                                this.notifyExposeSent(this, interest);
                            }
                        }
                    } else {
                        L.e("Certificate validation: Number of issuer and subjects are not equal.");
                    }
                }

                //Certificate extraction
                if(SharkCSAlgebra.identical(listOfTopics.get(i), Certificate.CERTIFICATE_COORDINATE)) {
                    if(Collections.list(interest.getPeers().peerTags()).size() == Collections.list(interest.getRemotePeers().tags()).size()) {
                        for(int j = 0; j < Collections.list(interest.getPeers().peerTags()).size(); j++) {
                            SharkCertificate sc = sharkPkiStorage.getSharkCertificate((PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(j), (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(j));
                            if(sc != null) {
                                ContextCoordinates contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                                        SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                                        null,
                                        sc.getSubject(),
                                        sc.getIssuer(),
                                        null,
                                        null,
                                        SharkCS.DIRECTION_INOUT);
                                this.sendKnowledge(SharkCSAlgebra.extract(sharkPkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter), interest.getOriginator());
                                this.notifyExposeSent(this, interest);
                            }
                        }
                    } else {
                        L.e("Certificate extraction: Number of issuer and subjects are not equal.");
                    }
                }
            }
        } catch (SharkKBException e) {
            new SharkKBException(e.getMessage());
        } catch (InvalidKeySpecException e) {
            new SharkKBException(e.getMessage());
        } catch (SharkSecurityException e) {
            new SharkKBException(e.getMessage());
        } catch (IOException e) {
            new SharkKBException(e.getMessage());
        }
    }

    public enum TrustedIssuer {
        ALL,
        KNOWN
    }
}
