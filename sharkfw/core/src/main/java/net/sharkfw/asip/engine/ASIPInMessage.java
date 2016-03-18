package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.kep.SharkStub;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Objects of this class are result of the scanning process
 * of imcomming messages from underlying protocols
 * @author thsc
 */
public class ASIPInMessage extends ASIPMessage{
    private SharkEngine se;
    private StreamConnection con;
    private SharkInputStream is;
    private SharkStub sharkStub;
    private ASIPKnowledge knowledge;
    private ASIPSpace interest;
    
    public ASIPInMessage(SharkEngine se, StreamConnection con) throws SharkKBException {
        super(se, con);
        this.se = se;
        this.con = con;
        this.is = con.getInputStream();
    }

    public void parse(){

    }

    private void parseHeader() throws IOException {
        if(this.is.available() > 0) {
        } else {
            L.d("No more bytes on stream!", this);
        }

        is.readUTF8();
    }

    public ASIPKnowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(ASIPKnowledge knowledge) {
        this.knowledge = knowledge;
    }

    public ASIPSpace getInterest() {
        return interest;
    }

    public void setInterest(ASIPSpace interest) {
        this.interest = interest;
    }
}
