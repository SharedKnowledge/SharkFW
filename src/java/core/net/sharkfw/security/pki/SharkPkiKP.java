package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ac
 */
public class SharkPkiKP extends KnowledgePort {

    public static final String SHARK_CERTIFICATE_NAME = "certificate";
    public static final String SHARK_CERTIFICATE_SI = "shark:certificate";

    public static final String SHARK_PKI_NAME = "pki";
    public static final String SHARK_PKI_SI = "shark:pki";

    public static final String SHARK_PKI_ID = "PKI";

    private SemanticTag semanticTagCertificate = InMemoSharkKB.createInMemoSemanticTag(SHARK_CERTIFICATE_NAME, SHARK_CERTIFICATE_SI);
    private SemanticTag semanticTagPki = InMemoSharkKB.createInMemoSemanticTag(SHARK_PKI_NAME, SHARK_PKI_SI);

    private SharkPkiStorage sharkPkiStorage;

    public SharkPkiKP(SharkEngine se, SharkPkiStorage sharkPkiStorage) {
        super(se);
        this.sharkPkiStorage = sharkPkiStorage;
    }

    @Override //incoming knowledge
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        for(ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            //TODO if certificate ....
        }
    }

    @Override //outgoing knowledge
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        //TODO if certificate ....
    }
}
