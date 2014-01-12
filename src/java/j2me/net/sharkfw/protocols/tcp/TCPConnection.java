/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.Streamer;
import net.sharkfw.wrapper.StringTokenizer;

/**
 *
 * @author Romy Gerlach
 */
public class TCPConnection implements StreamConnection{
    
    private String receiverAddress;
    private int portnumber;
    private SocketConnection socket;
    private OutputStream out;
    private InputStream in;
    private String replyAddressString;
    
    
    public TCPConnection(String recAddress, String replyAdressString) throws IOException{
        L.d("Creating TCPConnection to " + recAddress, TCPConnection.class);
        this.receiverAddress= recAddress;
        socket = (SocketConnection) Connector.open(recAddress);
        socket.setSocketOption(SocketConnection.LINGER, 0);
        in = socket.openInputStream();
        out = socket.openOutputStream();
        this.replyAddressString = replyAdressString;
        this.portnumber = this.getPortNumberFromString(recAddress);
    }
    
    public TCPConnection(SocketConnection sock_con, String replyAddressString) throws IOException{
        this.socket = sock_con;
        socket.setSocketOption(SocketConnection.LINGER, 0);
        this.replyAddressString = replyAddressString;
        this.receiverAddress = this.socket.getAddress();
        this.portnumber = this.socket.getPort();
        this.out = this.socket.openOutputStream();
        this.in = this.socket.openInputStream();
        L.d("Creating TCPConnection to " + this.receiverAddress, TCPConnection.class);
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public void sendMessage(String msg) throws IOException {
        L.d("Sending message: " + msg, this);
        byte[] byteMsg = msg.getBytes();
        this.out.write(byteMsg);
    }

    public String getReplyAddressString() {
        return this.replyAddressString;
    }

    public void close() {
        L.d("Closing TCP_Connection from:  " + this.getReplyAddressString() + "to: " + this.receiverAddress, this);
        try {
            final InputStream inputStream = socket.openInputStream();
            if(inputStream.available() > 0){
                L.e("Closing TCP_Connection although is more data on the stream: ", this);
                Streamer.stream(inputStream, System.err, 5);
            }
            this.socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Return portnumber as <code>int</code> value from string.
     * @param gcfString the string from which the portnumber is taken
     * @return  integer value portnumber
     */
    private int getPortNumberFromString(String gcfString){
        //StringTokenizer st = new StringTokenizer(gcfString, ":");

        String port = gcfString.substring(gcfString.lastIndexOf(':')+1);
        return Integer.parseInt(port);

    }


}
