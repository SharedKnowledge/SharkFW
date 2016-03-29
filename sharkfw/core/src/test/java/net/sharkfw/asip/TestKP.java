package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    private final String name;

    public TestKP(SharkEngine se, String name) {

        super(se);
        this.name = name;
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
        // UNUSED
    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
        // UNUSED
    }

    @Override
    protected void doProcess(ASIPInMessage msg, ASIPConnection con) {
        super.doProcess(msg, con);
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
        L.d(this.name + " says: Ping.");
        try {
            asipConnection.expose(interest);
        } catch (SharkException e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }
//        asipConnection.
//        super.handleExpose(interest, asipConnection);
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        super.handleInsert(asipKnowledge, asipConnection);
    }

    public String getName() {
        return name;
    }
}
