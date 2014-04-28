package net.sharkfw.protocols;

import java.io.IOException;

/**
 * Shark's own StreamConnection.
 * <bold>NOTE:</bold> This is not related to JavaME's <code>StreamConnection</code>
 *
 * @author thsc
 * @author mfi
 */
public interface StreamConnection {

  /**
   * Returns an <code>InputStream</code> from this connection
   *
   * @see java.io.InputStream
   *
   * @return An <code>InputStream</code> from this connection.
   */
    public SharkInputStream getInputStream();

    /**
     * Return an <code>OutputStream</code> from this connection.
     *
     * @see java.io.OutputStream
     *
     * @return An <code>OutputStream</code> from this connection.
     */
    public SharkOutputStream getOutputStream();

    /**
     * Package a message into a stream and send it to the communication partner
     * on the other side of this <code>StreamConnection</code>.
     * 
     * Messages must be passed as byte[] to avoid encoding issues on this level.
     * Encoding must be handled on a higher protocol level (KEP).
     *
     * @param msg A non-null byte[] containing the message to be sent.
     * @throws IOException
     */
    public void sendMessage(byte[] msg) throws IOException; // vll Obj statt String?
    
    /**
     * Return the address of the originator of the stream in GCF notation.
     * 
     * @return A string containing the replyaddress for this stream.
     */
    public String getReplyAddressString();
    
    /**
     * Return the address of the destination of this stream connection.
     * 
     * @return  A String containing the receiver's address for this stream.
     */
    public String getReceiverAddressString();

    /**
     * Return the address of the local device.
     * 
     * @return A String containing the local device address.
     */
    public String getLocalAddressString();
    
    /**
     * If a Stream is not able to determine the devices local address, it can be
     * set through this method.
     * 
     * @param localAddress the local address string which will returned through getLocalAddressString().
     */
    public void setLocalAddressString(String localAddress);
    
    /**
     * Close this connection.
     */
    public void close();
    
    /**
     * Add listener that is informed about connection status changes
     */
    public void addConnectionListener(ConnectionStatusListener newListener);

    public void removeConnectionListener(ConnectionStatusListener listener);
}
