package net.sharkfw.apirev1;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 * This dummy knowledge ports just remembers the last received knowledge or kepInterest.
 * The memory can be reset.
 * @author thsc
 */
public class DummyKP extends KnowledgePort {
    public Knowledge lastKnowledge = null;
    public SharkCS lastInterest;

    public DummyKP(SharkEngine se) {
        super(se);
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
        L.d("DummyKP: handleInsert reached - remember that knowledge:");
        L.d(L.knowledge2String(knowledge));
        this.lastKnowledge = knowledge;
    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
        L.d("DummyKP: handleExpose reached - remember that kepInterest:");
        L.d(L.contextSpace2String(interest));
        this.lastInterest = interest;
    }
    
    /**
     * Sets memory to null;
     */
    public void reset() {
        this.lastKnowledge = null;
        this.lastInterest = null;
    }
}
