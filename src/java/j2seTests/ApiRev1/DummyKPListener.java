package ApiRev1;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.KnowledgePort;

/**
 * Class remember last event.
 * @author thsc
 */
public class DummyKPListener implements KPListener {
    public SharkCS lastSentInterest = null;
    public Knowledge lastSentKnowledge = null;
    public ContextPoint lastAssimilatedKnowledge = null;
    public KnowledgePort lastKP = null;

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        this.lastKP = kp;
        this.lastSentInterest = sentMutualInterest;
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        this.lastKP = kp;
        this.lastSentKnowledge = sentKnowledge;
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        this.lastKP = kp;
        this.lastAssimilatedKnowledge = newCP;
    }
    
    /**
     * Sets memory to null;
     */
    public void reset() {
        this.lastAssimilatedKnowledge = null;
        this.lastKP = null;
        this.lastSentInterest = null;
        this.lastSentKnowledge = null;
    }
}
