/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.tcp;

import java.io.IOException;
import java.util.NoSuchElementException;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.system.L;
import net.sharkfw.wrapper.StringTokenizer;

/**
 *
 * @author Romy Gerlach
 */
public class TCPStreamStub implements StreamStub{

    private TCPServer server;

    public TCPStreamStub(RequestHandler handler, int port) throws IOException{
        try{
        this.server = new TCPServer(port, handler, this);
        this.server.start();
        }catch (IOException ex){
            L.e("can't create TCPStreamStub: fatal ", this);
            throw ex;
        }

    }

    public StreamConnection createStreamConnection(String addressString) throws IOException {
        String addrStr = addressString.substring("socket://".length());

        StringTokenizer st = new StringTokenizer(addrStr, ":");

        String hostname;
        String port;
        try {
            hostname = st.nextToken();
            port = st.nextToken();
        } catch (NoSuchElementException ne) {
            throw new IOException("wrong connection string format: " + addrStr);
        }

        int portno = Integer.parseInt(port);
        if (hostname.equalsIgnoreCase("localhost") && portno == this.server.getPortnumber()) {
            System.err.println("cannot create Connection: would be a message loop");
            throw new IOException("message loop detected");
        }
        return new TCPConnection(addressString, this.server.getLocalAddress());
    }

    public String getLocalAddress() {
        String address = System.getProperty("microedition.hostname");
        if(address == null){
            L.d("Couldn't find hostname, using IPAddress from server instead", this);
            address = this.server.getLocalAddress();
        }
        return "socket://" + address + ":" + Integer.toString(this.server.getPortnumber());
        
    }

    public void stop() {
        this.server.hold();
    }

}
