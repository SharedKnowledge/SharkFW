package net.sharkfw.protocols;

import java.io.IOException;

/**
 * This interface describes the requirements for a communication stub
 * that utilizes a message based protocol.
 * 
 *
 * @author thsc
 * @author mfi
 */
public interface MessageStub extends Stub {

  /**
   * Set the local address under which the peer is reachable from the network
   * @param addr A string containing a gcf address
   */
  void setReplyAddressString(String addr);

  /**
   * Send a message to a given recipient.
   * 
   * @param msg A string containing the message to be sent
   * @param recAddress The address of the receiver in gcf notation
   * @throws IOException
   */
  void sendMessage(byte[] msg, String recAddress) throws IOException;

  /**
   * Return the local address
   * @return A string containing the local address in gcf notation, which is used to listen for messages
   */
  String getReplyAddressString();

}  
    
