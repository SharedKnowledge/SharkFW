/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * This class implements the Sharl-StreamConnection over RFCOMM.
 * @author mfi
 */
public class BTRFCOMMConnection implements net.sharkfw.protocols.StreamConnection {

    private InputStream is;
    private OutputStream os;
    private String replyAddressString = "";

    private javax.microedition.io.StreamConnection conn;

    /**
     * Instantiate the class with a connection and information about our own replyAddress
     * @param connection
     */
    public BTRFCOMMConnection(javax.microedition.io.StreamConnection connection, String replyAddressString){
        this.conn = connection;
        this.replyAddressString = replyAddressString;
    }

    /**
     * Open a javax.microedition.io.StreamConnection to a given address and wrap it up. Then present this connection to the invoker of this
     * method.
     *
     */
    public BTRFCOMMConnection(String destinationAddress, String replyAddressString){
        System.out.println("Trying to connect to:" + destinationAddress);
        try {
            this.conn = (StreamConnection) Connector.open(destinationAddress);
            this.replyAddressString = replyAddressString;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Try to grab the inputstream from the connection, if it has not already been grabbed and return it.
     * @return an InputStream from the wrapped javax.microedition.io.StreamConnection
     */
    public InputStream getInputStream() {
        if(this.is == null){
            try {
                this.is = this.conn.openInputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return this.is;
    }

    /**
     * Try to grab the outputstream from the connection, if it has not already been grabbed and return it.
     * @return An outputstrem from the wrapped up javax.microedition.io.StreamConnection
     */
    public OutputStream getOutputStream() {
        if(this.os == null){
            try {
                this.os = this.conn.openOutputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return this.os;
    }

    /**
     * Send a message over the connection. Sen length as int first, and then the actual message.
     * @param msg the message to be sent
     * @throws java.io.IOException
     */
    public void sendMessage(String msg) throws IOException {
        byte[] bytes = new String(msg).getBytes();
        int len = bytes.length;

        OutputStream out = this.getOutputStream(); // make sure there is a stream
        out.write(len); // length first
        out.write(bytes); // then the message itself
    }

    /**
     * return the replyAddressStrign of the Shark-StreamConnection, which has been set when the constructor has been called
     * @return the String by which this peer can be addresses through RFComm.
     */
    public String getReplyAddressString() {
        return this.replyAddressString;
    }

    /**
     * Close the javax.microedition.io.StreamConnection
     */
    public void close() {
        try {
            
            if(this.is != null){ this.is.close(); }
            if(this.os != null){ this.os.close(); }
            
            if(this.conn != null) { this.conn.close(); }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
