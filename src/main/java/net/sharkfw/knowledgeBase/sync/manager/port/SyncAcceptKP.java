package net.sharkfw.knowledgeBase.sync.manager.port;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.PeerSTSet;
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

    public interface SyncAcceptListener{
        void onSyncInviteAccepted(SyncComponent component, PeerSTSet approvers);
    }

    private final SyncManager syncManager;
    private List<SyncAcceptListener> syncAcceptListeners = new ArrayList<>();

    public SyncAcceptKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    public void addSyncAcceptListener(SyncAcceptListener syncAcceptListener){
        syncAcceptListeners.add(syncAcceptListener);
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
                    L.d(L.semanticTag2String(next), this);
                    SyncComponent component = syncManager.getComponentByName(next);
                    if (component!=null){
                        L.w("Found component!", this);
                        component.addApprovedMember(interest.getApprovers());
                        for (SyncAcceptListener syncAcceptListener : syncAcceptListeners) {
                            syncAcceptListener.onSyncInviteAccepted(component, interest.getApprovers());
                        }
                        this.syncManager.doSync(component, peer, message);
                    }
                }
            }
        }
    }
}
