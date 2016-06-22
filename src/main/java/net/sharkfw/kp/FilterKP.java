package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
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
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        _notifier.notifyKnowledgeReceived(asipKnowledge, asipConnection);
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest==null) {
            L.d("Interest should not be empty.", this);
            return;
        }

        if(SharkCSAlgebra.isIn(_filter, interest)){

            _notifier.notifyInterestReceived(interest, asipConnection);
        }
    }
}
