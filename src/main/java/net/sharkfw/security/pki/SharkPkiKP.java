package net.sharkfw.security.pki;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import static net.sharkfw.security.utility.SharkCertificateHelper.getByteArrayFromLinkedList;
import static net.sharkfw.security.utility.SharkCertificateHelper.getLinkedListFromByteArray;

/**
 * TODO Rework to ASIP
 * @author ac
 */
public class SharkPkiKP extends KnowledgePort {

    public final static String KP_CERTIFICATE_VALIDATION_TAG_NAME = "validation";
    public final static String KP_CERTIFICATE_VALIDATION_TAG_SI = "validate:certificate";
    public final static SemanticTag KP_CERTIFICATE_VALIDATION_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(KP_CERTIFICATE_VALIDATION_TAG_NAME, new String[]{KP_CERTIFICATE_VALIDATION_TAG_SI});
    private SharkPkiStorage sharkPkiStorage;
    private Certificate.TrustLevel lowestTrustLevel;
    private PeerSTSet peerSTSet;

    public SharkPkiKP(SharkEngine se, SharkPkiStorage sharkPkiStorage, Certificate.TrustLevel lowestTrustLevel, PeerSTSet trustedIssuer) {
        super(se);
        this.sharkPkiStorage = sharkPkiStorage;
        this.lowestTrustLevel = lowestTrustLevel;
        this.peerSTSet = trustedIssuer;
    }

//    @Override //incoming knowledge
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            try {
                if (isValidPKIContextCoordinateAndTrustLevel(cp) && isFromTrustedIssuerIfAny(cp)){
                    updateRecalculatedTrustLevel(cp);
                    attachSenderToTransmitterList(cp, kepConnection.getSender());

                    if (sharkPkiStorage.addSharkCertificate(cp)) {
                        this.notifyKnowledgeAssimilated(this, cp);
                    } else {
                        L.d("Certificate already in SharkPkiStorage.");
                    }
                }
            } catch (SharkKBException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                L.e(e.getMessage());
            }

            boolean isValidFingerprint = SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), Certificate.FINGERPRINT_COORDINATE);
            if (isValidFingerprint && isFromTrustedIssuerIfAny(cp)) {
                System.out.println("Received fingerprint: " + Arrays.toString(cp.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte()));
                this.notifyKnowledgeAssimilated(this, cp);
            }
        }
    }

    private void updateRecalculatedTrustLevel(ContextPoint cp) throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        cp.removeInformation(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL).next());
        Information trustLevel = cp.addInformation();
        trustLevel.setName(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL);
        trustLevel.setContent(evaluateTrustLevelByIssuer(cp).name());
    }

    private void attachSenderToTransmitterList(ContextPoint cp, PeerSemanticTag sender) throws SharkKBException {
        LinkedList<PeerSemanticTag> pstList = getLinkedListFromByteArray(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME).next().getContentAsByte());

        pstList.add(sender);
        cp.removeInformation(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME).next());
        Information transmitterList = cp.addInformation();
        transmitterList.setName(SharkPkiStorage.PKI_INFORMATION_TRANSMITTER_LIST_NAME);
        transmitterList.setContent(getByteArrayFromLinkedList(pstList));
    }

    private boolean isValidPKIContextCoordinateAndTrustLevel(ContextPoint cp) throws SharkKBException {
        boolean isTopicIdentical = SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), SharkPkiStorage.PKI_CONTEXT_COORDINATE);
        if(!isTopicIdentical)  {
            return false;
        }

        Certificate.TrustLevel cpTrustLevel = Certificate.TrustLevel.valueOf(cp.getInformation(SharkPkiStorage.PKI_INFORMATION_TRUST_LEVEL).next().getContentAsString());
        boolean isTrustLevelValid = cpTrustLevel.ordinal() <= lowestTrustLevel.ordinal();
        return isTrustLevelValid;
    }


    private boolean isFromTrustedIssuerIfAny(ContextPoint cp) {
        return peerSTSet == null || Collections.list(peerSTSet.peerTags()).contains(cp.getContextCoordinates().getRemotePeer());
    }

//    @Override //outgoing knowledge
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
        try {
            ArrayList<SemanticTag> listOfTopics = Collections.list(interest.getTopics().tags());
            for (SemanticTag topic : listOfTopics) {
                if(isValidTopicAndSameSize(interest, topic, KP_CERTIFICATE_VALIDATION_COORDINATE)) {
                    validateCertificateByFingerprintAndNotify(interest);
                }

                if(isValidTopicAndSameSize(interest, topic, Certificate.CERTIFICATE_COORDINATE)) {
                    lookForCertificateAndNotifyIfFound(interest);
                }
            }
        } catch (IOException | SharkException e) {
            L.e(e.getMessage());
        }
    }

    private boolean isValidTopicAndSameSize(SharkCS interest, SemanticTag listOfTopic, SemanticTag certificateCoordinate) throws SharkKBException {
        if (!SharkCSAlgebra.identical(listOfTopic, certificateCoordinate)) {
            return false;
        }

        int numberOfPeerTags = Collections.list(interest.getPeers().peerTags()).size();
        if (numberOfPeerTags != Collections.list(interest.getRemotePeers().tags()).size()) {
            L.e("Certificate extraction: Number of issuer and subjects are not equal.");
            return false;
        }

        return true;
    }

    private void validateCertificateByFingerprintAndNotify(SharkCS interest) throws SharkException, IOException {
        int numberOfPeerTags = Collections.list(interest.getPeers().peerTags()).size();
        for (int i = 0; i < numberOfPeerTags; i++) {
            PeerSemanticTag issuer = (PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(i);
            PeerSemanticTag subject = (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(i);

            SharkCertificate sc = sharkPkiStorage.getSharkCertificate(issuer, subject);
            if (sc == null) {
                continue;
            }

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

    private void lookForCertificateAndNotifyIfFound(SharkCS interest) throws SharkKBException, SharkSecurityException, IOException {
        int numberOfPeerTags = Collections.list(interest.getPeers().peerTags()).size();
        for (int i = 0; i < numberOfPeerTags; i++) {
            PeerSemanticTag issuer = (PeerSemanticTag) Collections.list(interest.getRemotePeers().tags()).get(i);
            PeerSemanticTag subject = (PeerSemanticTag) Collections.list(interest.getPeers().tags()).get(i);

            SharkCertificate sc = sharkPkiStorage.getSharkCertificate(issuer, subject);
            if (sc == null) {
                continue;
            }

            ContextCoordinates contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                    SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                    null,
                    sc.getSubject(),
                    sc.getIssuer(),
                    null,
                    null,
                    SharkCS.DIRECTION_INOUT);

            Knowledge extractedCertificate = SharkCSAlgebra.extract(sharkPkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);
            this.sendKnowledge(extractedCertificate, interest.getOriginator());
            this.notifyExposeSent(this, interest);
        }
    }

    private Certificate.TrustLevel evaluateTrustLevelByIssuer(ContextPoint contextPoint) throws NoSuchAlgorithmException, InvalidKeySpecException, SharkKBException {
        if (sharkPkiStorage.getSharkCertificateList() == null) {
            return Certificate.TrustLevel.UNKNOWN;
        }

        int trustValue = 0;
        for (SharkCertificate sc : sharkPkiStorage.getSharkCertificateList()) {
            boolean isCpPeerIdenticalToCertSubject = SharkCSAlgebra.identical(sc.getSubject(), contextPoint.getContextCoordinates().getPeer());
            if (isCpPeerIdenticalToCertSubject) {
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
        }

        return Certificate.TrustLevel.NONE;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
