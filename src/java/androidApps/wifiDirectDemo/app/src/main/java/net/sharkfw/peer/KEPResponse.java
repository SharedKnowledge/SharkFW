/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.protocols.PeerAddress;

/**
 * This interface defines the methods that need to be implemented in order to allow answers to
 * received <code>KEPRequest</code>
 *
 * @author mfi
 */
public interface KEPResponse {

  /**
   * Send <code>interest</code> via <code>expose</code> to all entities on the
   * <code>REMOTEPEER</code> dimension of that interest.
   * 
   * @see net.sharkfw.knowledgeBase.ExposedInterest
   * @see net.sharkfw.knowledgeBase.ContextSpace
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param interest The <code>ExposedInterest</code> to send.
   */
  public void expose(SharkCS interest);

  /**
   * Send the <code>interest</code> to a specified address.
   *
   * @see net.sharkfw.knowledgeBase.ExposedInterest
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param interest The <code>ExposedInterest</code> to send.
   * @param receiveraddress The address to send the interest to.
   */
  public void expose(SharkCS interest, String receiveraddress);

  /**
   * Send the <code>interest</code> to a number of specified addresses.
   *
   * @param interest The interest to send
   * @param receiveraddresses An Array of String containing receiver's addresses
   */
  public void expose(SharkCS interest, String[] receiveraddresses);

  /**
   * Send <code>Knowledge</code> to all entites on the <code>REMOTEPEER</code>
   * dimension of the explanation (or background) of that <code>Knowledge</code>
   *
   * @see net.sharkfw.knowledgeBase.Knowledge
   * @see net.sharkfw.knowledgeBase.ContextSpace
   *
   * @param k the <code>Knowledge</code> object to send.
   */
  public void insert(Knowledge k);

  /**
   * Send <code>Knowledge</code> to a specified address.
   *
   * @see net.sharkfw.knowledgeBase.Knowledge
   * @see net.sharkfw.kep.KEPResponse
   *
   * @param k The <code>Knowledge</code> to send.
   * @param receiveraddress The address to send the <code>Knowledge</code> to.
   */
  public void insert(Knowledge k, String receiveraddress);

  /**
   * Send the <code>Knowledge</code> to a number of specified addresses.
   *
   * @param k The <code>Knowledge</code> to send.
   * @param receiveraddresses An Array of Strings containing receiver's addresses
   */
  public void insert(Knowledge k, String[] receiveraddresses);

  /**
   * Return if the delivery of the response was successful or not.
   *
   * @return True if response could be sent. False otherwise.
   */
  public boolean responseSent();


  /**
   * Get the address of the peer that sent the message we are about to answer to.
   * @return An address object telling the address of the peer that sent the request we are answering to.
   */
  public PeerAddress getSenderAddress();

  /**
   * Send this response to all addresses of the given peer.
   * @param pst The peer to send this response to.
   */
  public void sendToAllAddresses(PeerSemanticTag pst);
}
