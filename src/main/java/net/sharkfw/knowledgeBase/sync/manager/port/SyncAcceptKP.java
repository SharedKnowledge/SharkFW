package net.sharkfw.knowledgeBase.sync.manager.port;

import java.util.Iterator;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class SyncAcceptKP extends KnowledgePort {

    private final SyncManager syncManager;

    public SyncAcceptKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest.getTypes() != null && interest.getSender() != null) {

            PeerSemanticTag peer = interest.getSender();
            SemanticTag st = interest.getTypes().getSemanticTag(SyncManager.SHARK_SYNC_ACCEPT_TYPE_SI);

            if(st != null && peer != null) {

                L.w(this.se.getOwner().getName() + " received an Accept from " + message.getPhysicalSender().getName(), this);

                Iterator<SemanticTag> iterator = interest.getTopics().stTags();
                while (iterator.hasNext()){
                    SemanticTag next = iterator.next();
                    SyncComponent component = syncManager.getComponentByName(next);
                    if (component!=null){
                        component.addApprovedMember(interest.getApprovers());
                        this.syncManager.doSync(component, peer, message);
                    }
                }
            }
        }
    }
}
