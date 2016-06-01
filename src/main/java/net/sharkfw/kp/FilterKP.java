package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 * Created by msc on 25.05.16.
 */
public class FilterKP extends KnowledgePort {

    private final ASIPInterest _filter;
    private final KPNotifier _notifier;

    public FilterKP(SharkEngine se, ASIPInterest filter, KPNotifier notifier) {
        super(se);
        _filter = filter;
        _notifier = notifier;
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        _notifier.notifyKnowledgeReceived(asipKnowledge, asipConnection);
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
        if(interest==null) {
            L.d("Interest should not be empty.", this);
            return;
        }

        if(SharkCSAlgebra.isIn(_filter, interest)){

            _notifier.notifyInterestReceived(interest, asipConnection);
        }
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {

    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {

    }
}
