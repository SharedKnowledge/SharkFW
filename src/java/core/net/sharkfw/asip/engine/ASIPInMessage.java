package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.kep.SharkStub;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;

/**
 * Objects of this class are result of the scanning process
 * of imcomming messages from underlying protocols
 * @author thsc
 */
public class ASIPInMessage extends ASIPMessage {
    private SharkEngine se;
    private StreamConnection con;
    private SharkStub sharkStub;

    public ASIPInMessage() {}    
    
    public ASIPInMessage(SharkEngine se, StreamConnection con, SharkStub sharkStub) {
        this.se = se;
        this.con = con;
        this.sharkStub = sharkStub;
    }

    public void setSe(SharkEngine se) {
        this.se = se;
    }

    public void setCon(StreamConnection con) {
        this.con = con;
    }

    public void setSharkStub(SharkStub sharkStub) {
        this.sharkStub = sharkStub;
    }
    
    public Knowledge getKnowledge() {
        // TODO
        return null;
    }
    
    public ASIPSpace getInterest() {
        return null; // TODO
    }
}
