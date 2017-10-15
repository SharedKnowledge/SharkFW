package net.sharkfw.routing;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.system.L;

import java.util.ArrayList;
import java.util.List;

public class SemanticRoutingKP extends KnowledgePort {

    public void addSemanticRoutingListener(SemanticRoutingKP.SemanticRoutingListener listener) {
        this.mergeListeners.add(listener);
    }

    public interface SemanticRoutingListener {
        /**
         *
         * @param component
         * @param changes
         */
        void onNewMerge(SyncComponent component, SharkKB changes);
    }

    private SyncManager syncManager;
    private List<SemanticRoutingKP.SemanticRoutingListener> mergeListeners = new ArrayList<>();

    public SemanticRoutingKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        if(message.getType()==null || message.getType().isAny()) return;
        if(!SyncManager.SHARK_SYNC_MERGE_TAG.getName().equals(message.getType().getName())) return;

        L.w(this.se.getOwner().getName() + " received a Message from " + message.getPhysicalSender().getName(), this);
        SyncComponent component = syncManager.getComponentByName(message.getTopic());
        if(component == null) return;

        try {
            SharkKB previousChanges = syncManager.getChanges(component, message.getPhysicalSender());
            if(previousChanges != null && syncManager.hasChanged(previousChanges)){
                //TODO: compare new message with entryProfile
                //syncManager.doSync(component, message.getPhysicalSender(), message);
            }
            //component.getKb().putChanges((SharkKB) asipKnowledge); //TODO: putChanges only if the message passes the entryProfile
            L.w(se.getOwner().getName() + " merged the message!", this);
            for (SemanticRoutingKP.SemanticRoutingListener listener : this.mergeListeners) {
                listener.onNewMerge(component, (SharkKB) asipKnowledge); //Display of the new message in Android
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
