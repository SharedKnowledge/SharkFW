package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

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

    public SharkPkiKP(SharkEngine se, SharkPkiStorage sharkPkiStorage) {
        super(se);
        this.sharkPkiStorage = sharkPkiStorage;
    }

    @Override //incoming knowledge
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (SharkCSAlgebra.identical(cp.getContextCoordinates().getTopic(), SharkPkiStorage.PKI_CONTEXT_COORDINATE)) {
                //SharkPkiStorage needs and method to add directly a contextpoint to keep this slim
            }
        }
    }

    @Override //outgoing knowledge
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        //TODO if certificate ....
    }

    private SharkCertificate extractSharkCertificate(ContextPoint contextPoint) {

        return null;
    }
}
