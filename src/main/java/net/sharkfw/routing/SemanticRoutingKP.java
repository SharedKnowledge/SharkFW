package net.sharkfw.routing;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.ports.KnowledgePort;

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
        //TODO: Message mit Eingangsprofil vergleichen
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        //TODO: Message mit Ausgangsprofil vergleichen
    }
}
