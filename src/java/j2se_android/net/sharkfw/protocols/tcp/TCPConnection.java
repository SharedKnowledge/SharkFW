package net.sharkfw.protocols.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import net.sharkfw.protocols.ConnectionListenerManager;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.protocols.StandardSharkInputStream;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.UTF8SharkOutputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.Streamer;

/**
 * TCPConnection is a container Object which is holding a socket with an
 * established connection as well as the own local IP address.
 * 
 *
 * @author thsc
 */
public class TCPConnection extends ConnectionListenerManager implements StreamConnection {
    
    private int portNo;
    private String localAddress;
    private String recAddress;
    private String replyAddressString;
    private Socket s;
    private OutputStream out;
    private InputStream in;
    private int socketTimeout = 10000;

    public TCPConnection(String recAddress, int port) throws UnknownHostException, IOException {
        this(recAddress, port, null);
    }
    
    /**
     * Establishes a new connection to the given recAddress. It fails if
     * it fails to connect.
     *
     * @param recAddress address of the other Peer
     * @param port port of the other Peer
     * @param replyAddressString reply address which will be send to the other Peer (e.g. if you want to use a different address then the local device address)
     * @throws UnknownHostException
     * @throws IOException
     */
    public TCPConnection(String recAddress, int port, String replyAddressString)
            throws UnknownHostException, IOException {
        this.recAddress = recAddress;
        this.portNo = port;
        
        System.out.println("Trying to connect to " + this.recAddress + ":"
                + this.portNo);
        try {
            s = new Socket(this.recAddress, this.portNo);
        }
        catch(RuntimeException re) {
            throw new IOException(re.getMessage());
        }
        
        s.setSoTimeout(this.socketTimeout);
        this.out = s.getOutputStream();
        this.in = s.getInputStream();
        L.d("Creating TCPConnection w/ local address of:" + s.getLocalAddress() + ":" + s.getLocalPort(), this);
        
        this.localAddress = replyAddressString;
        this.replyAddressString = replyAddressString;
    }

    /**
     * Saves the given Socket and keeps the Stream open.
     *
     * @param s Socket with an established connection
     * @param replyAddressString replyAddressString reply address which will be send to the other Peer (e.g. if you want to use a different address then the local device address)
     * @throws IOException
     */
    public TCPConnection(Socket s, String replyAddressString) throws IOException {
      L.d("Using existing socket: '" + s.getInetAddress().getHostAddress() +"'", this);
        this.s = s;
        s.setSoTimeout(this.socketTimeout);
        this.replyAddressString = replyAddressString;
        this.localAddress = replyAddressString;

        this.recAddress = this.s.getInetAddress().getHostAddress();
        this.portNo = this.s.getPort();

        this.out = this.s.getOutputStream();
        this.in = this.s.getInputStream();
    }

    public String getReplyAddressString() {
        return this.replyAddressString;
    }

    /**
     * Sends a Stringb through the open Stream.
     *
     * @param msg Byte[] that will be sent
     * @throws IOException
     */
    public void sendMessage(byte[] msg) throws IOException {
        System.out.println("TCPConnection: sendMessage: " + msg);
        //byte[] byteMsg = msg.getBytes();
        this.out.write(msg);
    }

    /**
     * Reads data from any InputStream and sends this data
     * through the open TCP Stream.
     *
     * @param is
     * @throws IOException
     */
    void sendStream(InputStream is) throws IOException {
        int b;
        // TODO system.Streamer.stream could be used here, too.
        while ((b = is.read()) != -1) {
            this.out.write(b);
        }
    }

    public SharkInputStream getInputStream() {
        return new StandardSharkInputStream(this.in);
    }

    public SharkOutputStream getOutputStream() {
        return new UTF8SharkOutputStream(this.out);
    }

    /**
     * Terminates the current TCP Connection.
     */
    public void close() {

      L.d("Closing TCP-Connection from: " + this.getReplyAddressString() + " to: " + this.recAddress, this);
        try {
            final InputStream inputStream = s.getInputStream();
            if (inputStream.available() > 0) {
                System.err.println("Closing TCPConnection although there is more data on the stream: ");
                Streamer.stream(inputStream, System.err, 5);
            }
            this.s.close();
            this.notifyConnectionClosed();
        } catch (Exception ex) {
            L.d(ex.getMessage(), this);
        }
    }

  public String getReceiverAddressString() {
    return "tcp://" + this.recAddress + ":" + Integer.toString(this.portNo);
  }

    @Override
    public String getLocalAddressString() {
        return this.localAddress;
    }

    @Override
    public void setLocalAddressString(String localAddress) {
        this.localAddress = localAddress;
    }
    
}
