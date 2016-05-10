package net.sharkfw.asip.engine;

import java.io.InputStream;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.system.SharkException;

/**
 *
 * @author thsc
 */
public interface ASIPConnection extends KEPConnection{

    public void sendMessage(ASIPOutMessage msg, String[] addresses) throws SharkException;

    public void sendMessage(ASIPOutMessage msg) throws SharkException;

    public InputStream getInputStream();

    /**
     * Test wether the message was encrypted.
     * @return
     */
    public boolean receivedMessageEncrypted();

    /**
     * Received message was signed?
     * @return
     */
    public boolean receivedMessageSigned();

    public void expose(ASIPInterest interest) throws SharkException;

    public void expose(ASIPInterest interest, String receiveraddress) throws SharkException;

    public void expose(ASIPInterest interest, String[] receiveraddresses) throws SharkException;

    public void insert(ASIPKnowledge k, String receiveraddress) throws SharkException;

    public void insert(ASIPKnowledge k, String[] receiveraddresses) throws SharkException;

    public void raw(InputStream stream, String address) throws SharkException;

    public void raw(InputStream stream, String[] address) throws SharkException;

    /**
     * That methode can be used to figure out if a response was succcessully sent.
     * A failing sending attempt will result in a "false".
     *
     * @return True if response could be sent. False otherwise.
     */
    public boolean responseSent();

    /**
     * Send this response to all addresses of the given peer.
     * @param pst The peer to send this response to.
     */
    public void sendToAllAddresses(PeerSemanticTag pst);

    public PeerSemanticTag getSender() throws SharkKBException;

    public KEPConnection asKepConnection();
}
