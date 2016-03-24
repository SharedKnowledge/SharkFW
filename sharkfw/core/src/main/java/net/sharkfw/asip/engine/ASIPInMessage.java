package net.sharkfw.asip.engine;

import net.sharkfw.asip.*;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.system.SharkSecurityException;

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
    private ASIPInterest interest;
    private InputStream raw;
    private String parsedString;
    
    public ASIPInMessage(SharkEngine se, StreamConnection con) throws SharkKBException {

        super(se, con);

        this.se = se;
        this.con = con;
        // Get java.io.inputstream not shark.inputstream
        this.is = con.getInputStream().getInputStream();
    }

    public ASIPInMessage(SharkEngine se, int asipMessageType, Interest anyInterest, StreamConnection con, ASIPStub asipStub) {
        super(se, con);
        // TODO
    }

    public void parse() throws IOException, SharkSecurityException {

        this.parsedString = IOUtils.toString(this.is, "UTF-8");

        ASIPSerializer.deserializeInMessage(this, this.parsedString);
    }

    public ASIPKnowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(ASIPKnowledge knowledge) {
        this.knowledge = knowledge;
    }

    public ASIPInterest getInterest() {
        return interest;
    }

    public void setInterest(ASIPInterest interest) {
        this.interest = interest;
    }

    public InputStream getRaw() {
        return raw;
    }

    public void setRaw(InputStream raw) {
        this.raw = raw;
    }

    public void finished() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ASIPConnection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    boolean keepOpen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ASIPOutMessage convertToASIPOutMessage() throws SharkKBException {
        return new ASIPOutMessage(this.se, this.con, this);
    }
}
