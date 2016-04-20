package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    private final String name;
    private String rawContent;

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
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        super.handleRaw(is, asipConnection);
        L.d("Lol");
        try {
            IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
        L.d(this.name + " says: Ping.", this);

        if (asipConnection == null) {
            L.d("Connection = null");
        }

        if (interest == null) {
            L.d("Interest = null");
        }

//        super.handleExpose(interest, asipConnection);

        try {
            asipConnection.expose(interest);
        } catch (SharkException e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        super.handleInsert(asipKnowledge, asipConnection);
    }

    @Override
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        //TODO: after fix: use is instead of asipConnection to get InputStream
        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
        InputStream is2 = inMessage.getRaw();
        try {
            rawContent = IOUtils.toString(is2, "UTF-8");
        } catch (IOException e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        super.handleRaw(is, asipConnection);
    }

    public String getName() {
        return name;
    }

    public String getRawContentOnce() {
        String tmp = rawContent;
        rawContent = null;
        return tmp;
    }
}
