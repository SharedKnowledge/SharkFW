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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Objects of this class are result of the scanning process
 * of imcomming messages from underlying protocols
 * @author thsc
 */
public class ASIPInMessage extends ASIPMessage{
    private SharkEngine se;
    private StreamConnection con;
    private InputStream is;
    private SharkStub sharkStub;
    private ASIPKnowledge knowledge;
    private ASIPSpace interest;
    private byte[] raw;
    private String parsedString;
    
    public ASIPInMessage(SharkEngine se, StreamConnection con) throws SharkKBException {
        super(se, con);
        this.se = se;
        this.con = con;
        // Get java.io.inputstream not shark.inputstream
        this.is = con.getInputStream().getInputStream();
    }

    public void parse(){

        try {
            this.parsedString = IOUtils.toString(this.is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ASIPSerializer.deserializeInMessage(this, this.parsedString);

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

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }
}
