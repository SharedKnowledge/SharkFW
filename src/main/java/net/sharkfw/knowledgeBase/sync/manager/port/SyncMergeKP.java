package net.sharkfw.knowledgeBase.sync.manager.port;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j4rvis on 19.07.16.
 * 
 * @author thsc
 */
public class SyncMergeKP extends KnowledgePort {

    public void addSyncMergeListener(SyncMergeListener listener) {
        this.mergeListeners.add(listener);
    }

    public interface SyncMergeListener {
        /**
         *
         * @param component
         * @param changes - Should just be used to display e.g. the number of new messages
         */
        void onNewMerge(SyncComponent component, SharkKB changes);
    }

    private SyncManager syncManager;
    private List<SyncMergeListener> mergeListeners = new ArrayList<>();

    public SyncMergeKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        if(message.getType()==null || message.getType().isAny()) return;
        if(!SyncManager.SHARK_SYNC_MERGE_TAG.getName().equals(message.getType().getName())) return;

        L.w(this.se.getOwner().getName() + " received a Merge from " + message.getPhysicalSender().getName(), this);
        SyncComponent component = syncManager.getComponentByName(message.getTopic());
        if(component == null) return;

        try {
            SharkKB previousChanges = syncManager.getChanges(component, message.getPhysicalSender());
            if(previousChanges != null && syncManager.hasChanged(previousChanges)){
                syncManager.doSync(component, message.getPhysicalSender(), message);
            }
            component.getKb().putChanges((SharkKB) asipKnowledge);
            L.w(se.getOwner().getName() + " merged the changes!", this);
            for (SyncMergeListener listener : this.mergeListeners) {
                listener.onNewMerge(component, (SharkKB) asipKnowledge);
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
