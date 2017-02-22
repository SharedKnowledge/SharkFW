package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPStub;
import net.sharkfw.asip.SharkStub;
import net.sharkfw.asip.serialization.ASIPMessageSerializer;
import net.sharkfw.asip.serialization.ASIPSerializationHolder;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.Stub;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.SharkSecurityException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Objects of this class are resultSet of the scanning process
 * of imcomming messages from underlying protocols
 *
 * @author thsc, j4rvis
 */
public class ASIPInMessage extends ASIPMessage implements ASIPConnection {
    private SharkEngine se;
    private StreamConnection con;
    private InputStream is;
    private SharkStub sharkStub;
    private ASIPKnowledge knowledge;
    private ASIPInterest interest;
    private InputStream raw;
    private ASIPOutMessage response;
    private boolean parsed = false;
//    private boolean isEmpty = true;

    public ASIPInMessage(SharkEngine se, StreamConnection con) throws SharkKBException {

        super(se, con);

        this.se = se;
        this.con = con;
        this.is = con.getInputStream();
    }

    public ASIPInMessage(SharkEngine se, byte[] msg, Stub stub) {
        super(se, null);

        this.se = se;
        this.is = new ByteArrayInputStream(msg);
    }

    public ASIPInMessage(SharkEngine se, int asipMessageType, ASIPInterest anyInterest, StreamConnection con, ASIPStub asipStub) {
        super(se, con);
    }

    public ASIPInMessage(SharkEngine se, ASIPInterest interest, SharkStub stub){
        super(se, null);

        this.se = se;
        this.interest = interest;
        this.sharkStub = stub;
        this.setCommand(ASIPMessage.ASIP_EXPOSE);
    }

    public void parse() throws IOException, SharkSecurityException {




        char[] buffer = new char[1024];
        BufferedReader in = new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8));
        StringBuilder response= new StringBuilder();
        int charsRead;

        if(in.ready()){
            do{
                charsRead = in.read(buffer);
                response.append(buffer, 0, charsRead) ;
            } while(charsRead == buffer.length);

//            L.d("Read " + response.toString().length() + " Bytes of Data.", this);
//            L.d(response.toString(), this);

            if(!response.toString().isEmpty()){
                this.parsed = ASIPMessageSerializer.deserializeInMessage(this, response.toString());
            }
        }

    }

//    public boolean isEmpty() {
//        return isEmpty;
//    }

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

    public ASIPConnection getConnection() {
        return this;
    }

    public boolean keepOpen() {
        return true;
    }

    public ASIPOutMessage createResponse(SemanticTag topic, SemanticTag type) throws SharkKBException {
        return this.se.createASIPOutResponse(this.con, this, topic, type);
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
    public void expose(ASIPInterest interest) throws SharkException {

        this.expose(interest, this.con.getReceiverAddressString());
    }

    @Override
    public void expose(ASIPInterest interest, String receiveraddress) throws SharkException {
        this.expose(interest, new String[]{receiveraddress});
    }

    @Override
    public void expose(ASIPInterest interest, String[] receiveraddresses) throws SharkException {
        //TODO address not used
        if (interest == null)
            L.d("no interest", this);
        if (receiveraddresses.length < 0)
            L.d("no address", this);

        this.response = this.createResponse(null, null);
        if (this.response != null) {
            this.response.expose(interest);
        }
    }

    @Override
    public void insert(ASIPKnowledge k, String receiveraddress) throws SharkException {
        this.insert(k, new String[]{receiveraddress});
    }

    @Override
    public void insert(ASIPKnowledge k, String[] receiveraddresses) throws SharkException {
        //TODO address not used
        this.response = this.createResponse(null, null);
        if (this.response != null) {
            L.d("Now go insert!!!", this);
            this.response.insert(k);
        }
    }

    @Override
    public void raw(InputStream stream, String address) throws SharkException {
        this.raw(stream, new String[]{address});
    }

    @Override
    public void raw(InputStream stream, String[] address) throws SharkException {
        //TODO address not used
        ASIPOutMessage outMessage = this.createResponse(null, null);
        if (outMessage != null) {
            outMessage.raw(stream);
        }
    }

    @Override
    public void raw(byte[] bytes, String address) throws SharkException {
        this.raw(bytes, new String[]{address});
    }

    @Override
    public void raw(byte[] bytes, String[] address) throws SharkException {
        //TODO address not used
        ASIPOutMessage outMessage = this.createResponse(null, null);
        if (outMessage != null) {
            outMessage.raw(bytes);
        }
    }

    public void resetResponse(){
        this.response = null;
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

    public boolean isParsed(){
        return this.parsed;
    }
}
