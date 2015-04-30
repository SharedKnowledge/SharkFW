package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkException;

/**
 * This interface defines the methods that need to be implemented in order to allow answers to
 * received <code>KEPRequest</code>
 *
 * @author mfi
 */
public interface KEPConnection {
    
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

  /**
   * Send <code>interest</code> via <code>expose</code> to all entities on the
   * <code>REMOTEPEER</code> dimension of that interest.
   * 
     * @throws net.sharkfw.system.SharkException
   * @see net.sharkfw.knowledgeBase.ExposedInterest
   * @see net.sharkfw.knowledgeBase.ContextSpace
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param interest The <code>ExposedInterest</code> to send.
   */
  public void expose(SharkCS interest) throws SharkException;

  /**
   * Send the <code>interest</code> to a specified address.
   *
   * @see net.sharkfw.knowledgeBase.ExposedInterest
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param interest The <code>ExposedInterest</code> to send.
   * @param receiveraddress The address to send the interest to.
   */
  public void expose(SharkCS interest, String receiveraddress) throws SharkException;

  /**
   * Send the <code>interest</code> to a number of specified addresses.
   *
   * @param interest The interest to send
   * @param receiveraddresses An Array of String containing receiver's addresses
   */
  public void expose(SharkCS interest, String[] receiveraddresses) throws SharkException;

  /**
   * Send <code>Knowledge</code> to a specified address.
   *
     * @throws net.sharkfw.system.SharkException
   * @see net.sharkfw.knowledgeBase.Knowledge
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param k The <code>Knowledge</code> to send.
   * @param receiveraddress The address to send the <code>Knowledge</code> to (sent back to sender when null).
   */
  public void insert(Knowledge k, String receiveraddress) throws SharkException;

  /**
   * Send the <code>Knowledge</code> to a number of specified addresses.
   *
   * @param k The <code>Knowledge</code> to send.
   * @param receiveraddresses An Array of Strings containing receiver's addresses
   */
  public void insert(Knowledge k, String[] receiveraddresses) throws SharkException;

  /**
   * Return if the delivery of the response was successful or not.
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
}
