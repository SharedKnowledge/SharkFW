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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Created by j4rvis on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    private final String name;
    private String rawContent;
    private String text;

    public TestKP(SharkEngine se, String name) {

        super(se);
        this.name = name;
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(this.text.isEmpty()){
            L.d(this.name + " says: Ping.", this);
        } else{
            L.d(this.name + " says: " + this.text, this);
        }

        if (asipConnection == null) {
            L.d("Connection = null");
        }

        if (interest == null) {
            L.d("Interest = null");
        }

        try {
            asipConnection.expose(interest);
        } catch (SharkException e) {
            L.d(e.getMessage(), this);
            e.printStackTrace();
        }
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
    }


    @Deprecated
//    @Override
//    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
//        //TODO: after fix: use is instead of asipConnection to get InputStream
//        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
//        InputStream is2 = inMessage.getRaw();
//        try (Scanner scanner = new Scanner(is2, StandardCharsets.UTF_8.name())) {
//            rawContent = scanner.useDelimiter("\\A").next();
//        }
//
//        super.handleRaw(is, asipConnection);
//    }

    public String getName() {
        return name;
    }

    public String getRawContentOnce() {
        String tmp = rawContent;
        rawContent = null;
        return tmp;
    }
}
