package net.sharkfw.protocols;

/**
 * An interface that defines a generic handler for received requests.
 *
 * @author thsc
 * @author mfi
 */
public interface RequestHandler {

  /**
   * Handle a message that has been received through message based communication.
   * The received message must be a byte[] to avoid encoding issues.
   * Encoding is the job of a higher protocol level (KEP).
   *
   * @see net.sharkfw.protocols.MessageStub
   *
   * @param msg The message to handle as byte[].
   * @param stub The <code>MessageStub</code> through which this message has been received.
   */
  public void handleMessage(byte[] msg, MessageStub stub);

  /**
   * Handle an incoming <code>StreamConnection</code>.
   *
   * @see net.sharkfw.protocols.StreamConnection
   *
   * @param con The <code>StreamConnection</code> to handle.
   */
  public void handleStream(StreamConnection con);
  
  /**
   * Ad hoc networks create new connections by their own.
   * Those stubs should use this method to annonce a newly
   * created stream connection
   * @param con 
   */
  public void handleNewConnectionStream(StreamConnection con);
}
