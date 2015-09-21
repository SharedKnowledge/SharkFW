package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import static net.sharkfw.security.utility.SharkCertificateHelper.getByteArrayFromLinkedList;
import static net.sharkfw.security.utility.SharkCertificateHelper.getLinkedListFromByteArray;

/**
 * @author ac
 */
public class SharkPkiKP extends KnowledgePort {

    public final static String KP_CERTIFICATE_VALIDATION_TAG_NAME = "validation";
    public final static String KP_CERTIFICATE_VALIDATION_TAG_SI = "validate:certificate";
    public final static SemanticTag KP_CERTIFICATE_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(KP_CERTIFICATE_VALIDATION_TAG_NAME, new String[]{KP_CERTIFICATE_VALIDATION_TAG_SI});
    private SharkPkiStorage sharkPkiStorage;
    private Certificate.TrustLevel lowestTrustLevel;
    private PeerSTSet peerSTSet;

    public SharkPkiKP(SharkEngine se, SharkPkiStorage sharkPkiStorage, Certificate.TrustLevel lowestTrustLevel, PeerSTSet trustedIssuer) {
        super(se);
        this.sharkPkiStorage = sharkPkiStorage;
        this.lowestTrustLevel = lowestTrustLevel;
        this.peerSTSet = trustedIssuer;
    }

    @Override //incoming knowledge
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            try {
                if (SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), SharkPkiStorage.PKI_CONTEXT_COORDINATE) &&
                        Certificate.TrustLevel.valueOf(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL).next().getContentAsString()).ordinal() <= lowestTrustLevel.ordinal() &&
                        Collections.list(peerSTSet.peerTags()).contains(cp.getContextCoordinates().getRemotePeer())) {

                        //Remove old trustlevel and replace it whit the new calculated
                        cp.removeInformation(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL).next());
                        Information trustLevel = cp.addInformation();
                        trustLevel.setName(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL);
                        trustLevel.setContent(evaluateTrustLevelByIssuer(cp).name());

                        //Attach sender to the transmitterList
                        LinkedList<PeerSemanticTag> pstList = getLinkedListFromByteArray(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME).next().getContentAsByte());
                        pstList.add(kepConnection.getSender());
                        cp.removeInformation(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME).next());
                        Information transmitterList = cp.addInformation();
                        transmitterList.setName(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME);
                        transmitterList.setContent(getByteArrayFromLinkedList(pstList));

                        if (sharkPkiStorage.addSharkCertificate(cp)) {
                            this.notifyKnowledgeAssimilated(this, cp);
                        } else {
                            L.d("Certificate already in SharkPkiStorage.");
                        }

                }

            } catch (SharkKBException e) {
                L.e(e.getMessage());
            } catch (InvalidKeySpecException e) {
                L.e(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                L.e(e.getMessage());
            }

            if (SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), Certificate.FINGERPRINT_COORDINATE) &&
                    Collections.list(peerSTSet.peerTags()).contains(cp.getContextCoordinates().getRemotePeer())) {
                System.out.println("Received fingerprint: " + cp.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte());
                this.notifyKnowledgeAssimilated(this, cp);
            }
        }
    }

    @Override //outgoing knowledge
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        try {
            ArrayList<SemanticTag> listOfTopics = Collections.list(interest.getTopics().tags());
            for (int i = 0; i < listOfTopics.size(); i++) {
                //Certificate validation
                if (SharkCSAlgebra.identical(listOfTopics.get(i), KP_CERTIFICATE_COORDINATE)) {
                    if (Collections.list(interest.getPeers().peerTags()).size() == Collections.list(interest.getRemotePeers().tags()).size()) {
                        for (int j = 0; j < Collections.list(interest.getPeers().peerTags()).size(); j++) {
                            SharkCertificate sc = sharkPkiStorage.getSharkCertificate((PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(j), (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(j));
                            if (sc != null) {
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
                if (SharkCSAlgebra.identical(listOfTopics.get(i), Certificate.CERTIFICATE_COORDINATE)) {
                    if (Collections.list(interest.getPeers().peerTags()).size() == Collections.list(interest.getRemotePeers().tags()).size()) {
                        for (int j = 0; j < Collections.list(interest.getPeers().peerTags()).size(); j++) {
                            SharkCertificate sc = sharkPkiStorage.getSharkCertificate((PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(j), (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(j));
                            if (sc != null) {
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
            L.e(e.getMessage());
        } catch (InvalidKeySpecException e) {
            L.e(e.getMessage());
        } catch (SharkSecurityException e) {
            L.e(e.getMessage());
        } catch (IOException e) {
            L.e(e.getMessage());
        }
    }

    private Certificate.TrustLevel evaluateTrustLevelByIssuer(ContextPoint contextPoint) throws NoSuchAlgorithmException, InvalidKeySpecException, SharkKBException {
        int trustValue = 0;
        if(sharkPkiStorage.getSharkCertificateList() != null) {
            for (SharkCertificate sc : sharkPkiStorage.getSharkCertificateList()) {
                if (contextPoint.getContextCoordinates().getRemotePeer().equals(sc.getSubject())) {
                    switch (sc.getTrustLevel()) {
                        case FULL:
                            trustValue += 1;
                            break;
                        case MARGINAL:
                            trustValue += 0.5;
                            break;
                        case NONE:
                            trustValue += -0.7;
                            break;
                        case UNKNOWN:
                            trustValue += -0.3;
                            break;
                    }
                }
            }

            if (trustValue == 0) {
                return Certificate.TrustLevel.UNKNOWN;
            }

            if (trustValue > 0) {
                return Certificate.TrustLevel.MARGINAL;
            } else {
                return Certificate.TrustLevel.NONE;
            }
        } else {
            return Certificate.TrustLevel.UNKNOWN;
        }
    }
}
