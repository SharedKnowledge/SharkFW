package net.sharkfw.asip.engine;

import net.sharkfw.asip.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.SharkException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import net.sharkfw.system.SharkSecurityException;

/**
 * Objects of this class are result of the scanning process
 * of imcomming messages from underlying protocols
 *
 * @author thsc, msc
 */
public class ASIPInMessage extends ASIPMessage implements ASIPConnection {
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
        this.is = con.getInputStream();
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
        if (this.se.getAsipStub() != null && this.con != null) {
            this.se.getAsipStub().handleStream(this.con);
        }
    }

    ASIPConnection getConnection() {
        return this;
    }

    boolean keepOpen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ASIPOutMessage convertToASIPOutMessage() throws SharkKBException {
        return new ASIPOutMessage(this.se, this.con, this);
    }

    @Override
    public void sendMessage(ASIPOutMessage msg, String[] addresses) throws SharkException {

    }

    @Override
    public void sendMessage(ASIPOutMessage msg) throws SharkException {

    }

    @Override
    public InputStream getInputStream() {
        return is;
    }

    @Override
    public boolean receivedMessageEncrypted() {
        return isEncrypted();
    }

    @Override
    public boolean receivedMessageSigned() {
        return isSigned();
    }

    @Override
    public void expose(SharkCS interest) throws SharkException {

    }

    @Override
    public void expose(SharkCS interest, String receiveraddress) throws SharkException {

    }

    @Override
    public void expose(SharkCS interest, String[] receiveraddresses) throws SharkException {

    }

    @Override
    public void insert(Knowledge k, String receiveraddress) throws SharkException {

    }

    @Override
    public void insert(Knowledge k, String[] receiveraddresses) throws SharkException {

    }

    @Override
    public void expose(ASIPInterest interest) throws SharkException {

    }

    @Override
    public void expose(ASIPInterest interest, String receiveraddress) throws SharkException {

    }

    @Override
    public void expose(ASIPInterest interest, String[] receiveraddresses) throws SharkException {

    }

    @Override
    public void insert(ASIPKnowledge k, String receiveraddress) throws SharkException {

    }

    @Override
    public void insert(ASIPKnowledge k, String[] receiveraddresses) throws SharkException {

    }

    @Override
    public void raw(InputStream stream, String address) throws SharkException {

    }

    @Override
    public void raw(InputStream stream, String[] address) throws SharkException {

    }

    @Override
    public boolean responseSent() {
        return false;
    }

    @Override
    public void sendToAllAddresses(PeerSemanticTag pst) {

    }

    @Override
    public KEPConnection asKepConnection() {
        return this;
    }
}
