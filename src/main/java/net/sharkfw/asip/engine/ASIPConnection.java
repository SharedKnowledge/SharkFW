package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkException;

import java.io.InputStream;

/**
 *
 * @author thsc
 */
public interface ASIPConnection {

    void sendMessage(ASIPOutMessage msg, String[] addresses) throws SharkException;

    void sendMessage(ASIPOutMessage msg) throws SharkException;

    InputStream getInputStream();

    /**
     * Test wether the message was encrypted.
     * @return
     */
    boolean receivedMessageEncrypted();

    /**
     * Received message was signed?
     * @return
     */
    boolean receivedMessageSigned();

    void expose(ASIPInterest interest) throws SharkException;

    void expose(ASIPInterest interest, String receiveraddress) throws SharkException;

    void expose(ASIPInterest interest, String[] receiveraddresses) throws SharkException;

    void insert(ASIPKnowledge k, String receiveraddress) throws SharkException;

    void insert(ASIPKnowledge k, String[] receiveraddresses) throws SharkException;

    void raw(InputStream stream, String address) throws SharkException;

    void raw(InputStream stream, String[] address) throws SharkException;

    void raw(byte[] bytes, String address) throws SharkException;

    void raw(byte[] bytes, String[] address) throws SharkException;

    /**
     * That methode can be used to figure out if a response was succcessully sent.
     * A failing sending attempt will resultSet in a "false".
     *
     * @return True if response could be sent. False otherwise.
     */
    boolean responseSent();

    /**
     * Send this response to all addresses of the given peer.
     * @param pst The peer to send this response to.
     */
    void sendToAllAddresses(PeerSemanticTag pst);

    PeerSemanticTag getSender() throws SharkKBException;
}
