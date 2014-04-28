/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import net.sharkfw.protocols.tcp.TCPConnection;

/**
 *
 * @author Jacob Zschunke
 */
public class HTTPConnection extends TCPConnection {

    static final byte[] EOL = {(byte)'\r', (byte)'\n' };
    private final boolean isRequest;
    
    public HTTPConnection(Socket s, String replyAddressString, boolean isRequest) throws IOException {
        super(s, replyAddressString);
        this.isRequest = isRequest;
        
        if(this.isRequest) {
            this.printRequestHeader();
        }        
    }

    public HTTPConnection(String recAddress, int port, String replyAddressString, boolean isRequest) throws UnknownHostException, IOException {
        super(recAddress, port, replyAddressString);
        this.isRequest = isRequest;
        
        if(this.isRequest) {
            this.printRequestHeader();
        }
    }    
    
    private void printRequestHeader() throws IOException {        
        PrintStream ps = new PrintStream(super.getOutputStream().getOutputStream());
        ps.print("POST HTTP/1.1");
        ps.write(EOL);
        ps.print("HOST: " + super.getReplyAddressString());
        ps.write(EOL);
        ps.print("User-Agent: Shark");        
        ps.write(EOL);
        ps.write(EOL);
        // do not flush to send the Header with the message
    }

}
