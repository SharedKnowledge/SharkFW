/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;

/**
 * <code>HTTPClientMessageStub</code> is a special MessageStub which can connect
 * to an <code>Hub</code> but can not provide a server. It makes use of the
 * HTTP POST Method to communicate with the Hub. Therefor it even sets an
 * HTTP Header. Special here is that the user-agent attribute is set to "shark".
 * Thats important because the Hub accepts only connections from shark clients.
 *
 * Whenever a message will be sent the method listen for any return value. If
 * there is something coming back, the <code>RequestHandler</code> will be
 * notified.
 *
 * @author Jacob Zschunke
 */
public class HTTPClientMessageStub implements MessageStub {

    public static String GET = "GET";
    public static String POST = "POST";
    public static String KEPMETHOD = "KEP_Command";
    public static String KEPPAYLOAD = "KEP_Payload";
    private RequestHandler handler;
    private String replyAddress;
    private String method;

    public HTTPClientMessageStub(RequestHandler handler, String replyAddress, String httpMethod) {
        this.handler = handler;
        this.replyAddress = replyAddress;
        this.method = httpMethod;
        if (!method.equalsIgnoreCase(GET) && !method.equalsIgnoreCase(POST)) {
            method = POST;
        }
    }

    public void setReplyAddressString(String addr) {
        this.replyAddress = addr;
    }

    /**
     * creates the HTTP Header and send the Message String to the given address.
     * If there is any ReturnValue the <code>RequestHandler</code> will be
     * notified.
     *
     * @param msg
     * @param recAddress
     * @throws IOException
     */
    public void sendMessage(String msg, String recAddress) throws IOException {
        if (method.equalsIgnoreCase(GET)) {
            doGET(msg, recAddress);
        } else {
            doPOST(msg, recAddress);
        }
    }

    private void doPOST(String msg, String recAddress) throws IOException {
        HttpConnection con = (HttpConnection) Connector.open(recAddress);

        con.setRequestMethod(HttpConnection.POST);
        con.setRequestProperty("HTTP/Version", "HTTP/1.0");
        con.setRequestProperty("user-agent", "shark");
        con.setRequestProperty("connection", "close");

        OutputStream out = con.openOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, "ISO8859_1");

        writer.write(msg);
        writer.flush();
        writer.close();
        
        this.receiveResponse(con);
    }

    private void doGET(String msg, String recAddress) throws IOException {        
        String urlString = recAddress + "?" + "isShark=true&" + KEPPAYLOAD + "=" + msg;
        HttpConnection con = (HttpConnection) Connector.open(urlString);

        con.setRequestMethod("GET");
        con.setRequestProperty("HTTP/Version", "HTTP/1.0");
        con.setRequestProperty("user-agent", "shark");
        con.setRequestProperty("connection", "close");
        
        this.receiveResponse(con);
    }

    /**
     * where no server is there is nothing to stop
     */
    public void stop() {
        // can't stop the rock
    }

    public String getReplyAddressString() {
        return replyAddress;
    }

    private void receiveResponse(HttpConnection con) throws IOException {
        InputStream in = con.openInputStream();
        InputStreamReader reader = new InputStreamReader(in, "ISO8859_1");

        byte[] bytes = new byte[0];
        int curByte = 0;
        while((curByte = reader.read()) != -1) {
            bytes = this.putByte(bytes, curByte);
        }

        String incoming = new String(bytes);

        if (incoming != null || !incoming.equalsIgnoreCase("")) {
            handler.handleMessage(incoming, this);
        }
    }

    private byte[] putByte(byte[] bytes, int b) {
        byte[] newBytes = new byte[bytes.length+1];
        for(int i = 0; i < bytes.length; i++) {
            newBytes[i] = bytes[i];
        }
        newBytes[bytes.length] = (byte) b;

        return newBytes;
    }

}
