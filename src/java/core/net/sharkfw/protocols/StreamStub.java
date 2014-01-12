/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols;

import java.io.IOException;

/**
 * This interface denotes the requirements of a general stub for handling
 * stream based communication, without making assumptions as to the protocol
 * that's being used.
 *
 * @author thsc
 * @author mfi
 */
public interface StreamStub extends Stub {

  /**
   * Create a new <code>StreamConnection</code> to the given address.
   *
   * @param addressString The gcf address to connect to.
   * @return A <code>StreamConnection</code> connected to the given address
   * @throws IOException If a component tries to connect to the local address of this peer, the user is informed of a 'message loop'.
   */
  public StreamConnection createStreamConnection(String addressString) throws IOException ;

  /**
   * Return the local address which is used to listen for incoming connections.
   * @return A string containing the gcf address of the local peer
   */
  public String getLocalAddress();
}
