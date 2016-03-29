package net.sharkfw.asip.engine;

import net.sharkfw.asip.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

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
    private ASIPOutMessage response;

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

    public ASIPOutMessage createResponse(String[] address) throws SharkKBException {
        return this.se.createASIPOutResponse(this.con, address, this);
    }

    @Override
    public void sendMessage(ASIPOutMessage msg, String[] addresses) throws SharkException {
        // TODO sendMessage
    }

    @Override
    public void sendMessage(ASIPOutMessage msg) throws SharkException {
        // TODO sendMessage
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
        // do nothing
    }

    @Override
    public void expose(SharkCS interest, String receiveraddress) throws SharkException {
        // do nothing
    }

    @Override
    public void expose(SharkCS interest, String[] receiveraddresses) throws SharkException {
        // do nothing
    }

    @Override
    public void insert(Knowledge k, String receiveraddress) throws SharkException {
        // do nothing
    }

    @Override
    public void insert(Knowledge k, String[] receiveraddresses) throws SharkException {
        // do nothing
    }

    @Override
    public void expose(ASIPInterest interest) throws SharkException {

        L.d("Expose called", this);

        try {
            STSet remotepeers = interest.getReceivers();
            Enumeration rPeers = null;

            if(remotepeers != null) {
                rPeers = remotepeers.tags();
            }

            if(rPeers == null || !rPeers.hasMoreElements()) {
                // there are no peer at all - maybe we got it through a stream
                this.expose(interest, (String[]) null);
                return;
            }

            // Send kepInterest to every peer
            while (rPeers.hasMoreElements()) {
                L.d("peers", this);
                PeerSemanticTag rpst = (PeerSemanticTag) rPeers.nextElement();
                // try every address of that peer
                String[] adr = rpst.getAddresses();
                if (adr == null) {
                    L.e("Peer has no addresses. Unable to proceed.", this);
                    continue;
                }

                this.expose(interest, adr);
            }
        } catch (SharkException ex) {
            // KB Error
            L.e(ex.getMessage(), this);
        }


    }

    @Override
    public void expose(ASIPInterest interest, String receiveraddress) throws SharkException {
        this.expose(interest, new String[]{ receiveraddress });
    }

    @Override
    public void expose(ASIPInterest interest, String[] receiveraddresses) throws SharkException {
        if(interest == null)
            L.d("no interest");
        if(receiveraddresses.length < 0 )
            L.d("no address");

        L.d("create Response.");
        ASIPOutMessage outMessage = this.createResponse(receiveraddresses);
        if(outMessage != null){
            outMessage.expose(interest);
        }
    }

    @Override
    public void insert(ASIPKnowledge k, String receiveraddress) throws SharkException {
        this.insert(k, new String[] { receiveraddress });
    }

    @Override
    public void insert(ASIPKnowledge k, String[] receiveraddresses) throws SharkException {
        ASIPOutMessage outMessage = this.createResponse(receiveraddresses);
        if(outMessage != null){
            outMessage.insert(k);
        }
    }

    @Override
    public void raw(InputStream stream, String address) throws SharkException {
        this.raw(stream, new String[] { address });
    }

    @Override
    public void raw(InputStream stream, String[] address) throws SharkException {
        ASIPOutMessage outMessage = this.createResponse(address);
        if(outMessage != null){
            outMessage.raw(stream);
        }
    }

    @Override
    public boolean responseSent() {
        if(this.response == null) {
            return false;
        }
        return response.responseSent();
    }

    @Override
    public void sendToAllAddresses(PeerSemanticTag pst) {

    }

    @Override
    public KEPConnection asKepConnection() {
        return this;
    }
}
