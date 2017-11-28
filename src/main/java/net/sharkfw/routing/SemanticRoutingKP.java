package net.sharkfw.routing;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.broadcast.BroadcastManager;
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
        void onNewMerge(SyncComponent component, SharkKB changes, boolean accepted);
    }

    private BroadcastManager broadcastManager;
    private List<SemanticRoutingKP.SemanticRoutingListener> mergeListeners = new ArrayList<>();

    public SemanticRoutingKP(SharkEngine se, BroadcastManager broadcastManager) {
        super(se);
        this.broadcastManager = broadcastManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        if(message.getType()==null || message.getType().isAny()) return;
        if(!BroadcastManager.SHARK_BROADCAST_TAG.getName().equals(message.getType().getName())) return;
        L.w(this.se.getOwner().getName() + " ___received a Message from___ " + message.getPhysicalSender().getName(), this);
        SyncComponent component = broadcastManager.getBroadcastComponent();
        if(component == null) return;

        try {
                if (broadcastManager.isUnknown((SharkKB) asipKnowledge) &&
                        broadcastManager.checkWithEntryProfile((SharkKB) asipKnowledge, message.getPhysicalSender(), message)) {

                    //broadcastManager.doBroadcast(component, message.getPhysicalSender(), message);
                    component.getKb().putChanges((SharkKB) asipKnowledge);
                    L.w(se.getOwner().getName() + " received the message!", this);
                    System.out.println("___________________3______________________");
                    for (SemanticRoutingKP.SemanticRoutingListener listener : this.mergeListeners) {
                        listener.onNewMerge(component, (SharkKB) asipKnowledge, true); //Display of the new message in Android
                    }
                    if (broadcastManager.checkWithOutProfile()) {   //TODO:

                    }
                }
                else {
                    for (SemanticRoutingKP.SemanticRoutingListener listener : this.mergeListeners) {
                        listener.onNewMerge(component, (SharkKB) asipKnowledge, false); //Message was rejected
                    }
                }
            //}

        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
