package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort{

    private final String name;

    public TestKP(SharkEngine se, String name) {

        super(se);
        this.name = name;
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {

    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {

    }

    @Override
    protected void handleExpose(ASIPSpace interest, ASIPConnection asipConnection) throws SharkKBException {
        L.d("Port so und so");
//        asipConnection.
        super.handleExpose(interest, asipConnection);
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        super.handleInsert(asipKnowledge, asipConnection);
    }

    public String getName() {
        return name;
    }
}
