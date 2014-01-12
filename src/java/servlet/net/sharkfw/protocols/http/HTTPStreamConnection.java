/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import net.sharkfw.protocols.StreamConnection;

/**
 *
 * @author Jacob Zschunke
 */
public class HTTPStreamConnection implements StreamConnection {

    private InputStream is;
    private OutputStream os;
    private String replyAddress;

    public HTTPStreamConnection(String replyAddress, InputStream is, OutputStream os) {
        this.replyAddress = replyAddress;
        this.is = is;
        this.os = os;
    }

    public HTTPStreamConnection(String addressString, String replyAddress) {
        try {            
            URL url = new URL(addressString);
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setAllowUserInteraction(false);

            this.is = con.getInputStream();
            this.os = con.getOutputStream();
            this.replyAddress = replyAddress;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        return is;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public void sendMessage(String msg) throws IOException {
        this.os.write(msg.getBytes());
        this.os.flush();
    }

    public String getReplyAddressString() {
        return replyAddress;
    }

    public void close() {
        try {
            is.close();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
