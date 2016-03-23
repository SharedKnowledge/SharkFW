package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.SharkStub;
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

import net.sharkfw.asip.ASIPStub;
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
    private ASIPSpace interest;
    private byte[] raw;
    private String parsedString;
    
    public ASIPInMessage(SharkEngine se, StreamConnection con) throws SharkKBException {

        super(se, con);

        L.d("ASIPInMessage Constructor");
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
        L.d("parse triggered");

        this.parsedString = IOUtils.toString(this.is, "UTF-8");

        L.d(this.parsedString);

        ASIPSerializer.deserializeInMessage(this, this.parsedString);

        L.d("Inputstream serialized");
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

    public void finished() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ASIPConnection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    boolean keepOpen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
