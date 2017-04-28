package net.sharkfw.knowledgeBase.sync.manager.port;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 * Created by j4rvis on 19.07.16.
 * 
 * @author thsc
 */
public class SyncMergeKP extends KnowledgePort {

    private SyncManager syncManager;

    public SyncMergeKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

        if(message.getType()==null || message.getType().isAny()) {
            return;
        }

        if(!SyncManager.SHARK_SYNC_MERGE_TAG.getName().equals(message.getType().getName())){
            return;
        }

        L.d(this.se.getOwner().getName() + " received a Merge from " + message.getPhysicalSender().getName(), this);

        SyncComponent component = syncManager.getComponentByName(message.getTopic());

        if(component == null) return;

        SyncKB syncKB = component.getKb();

        try {
            SharkKB kb1 = syncManager.getChanges(component, message.getPhysicalSender());
            boolean anyChanges = kb1 != null;
            L.d("Before syncing, does " + this.se.getOwner().getName() + " has any changes to reply? " + anyChanges, this);

//            L.d("Changes: " + L.kb2String((SharkKB) asipKnowledge), this);
//            L.d("--------------------------------------------------------", this);
//            L.d("SyncKB: " + L.kb2String((SharkKB) syncKB), this);
//            L.d("--------------------------------------------------------", this);
//            L.d("--------------------------------------------------------", this);
//            L.d("--------------------------------------------------------", this);
            syncKB.putChanges((SharkKB) asipKnowledge);
//            L.d("Merged SyncKB: " + L.kb2String((SharkKB) syncKB), this);

            if(anyChanges){
                L.d("Now send " + this.se.getOwner().getName() + "'s changes to " + message.getPhysicalSender().getName(), this);
                syncManager.sendMerge(component, message.getPhysicalSender(), message);
            }

        } catch (SharkKBException e) {
            e.printStackTrace();
            L.d(e.getMessage(), this);
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
