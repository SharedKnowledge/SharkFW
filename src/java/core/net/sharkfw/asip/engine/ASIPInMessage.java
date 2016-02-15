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
    private final SharkEngine se;
    private final StreamConnection con;
    private final SharkStub sharkStub;
    
    ASIPInMessage(SharkEngine se, StreamConnection con, SharkStub sharkStub) {
        this.se = se;
        this.con = con;
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
