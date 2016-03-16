package net.sharkfw.protocols.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.protocols.http.HTTPConnection;
import net.sharkfw.protocols.http.WebServer;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * This class represents / manages all TCP based connections
 * of this peer.
 * Its able to establish a TCPConnection with an Address and to run
 * or stop a TCPServer or WebServer which is listening for incoming Connections.
 * Through the given RequestHandler in the constructor it is able to
 * notify if a connection is established.
 * 
 * @author thsc
 */
public class TCPStreamStub implements StreamStub {

    private SharkServer server;
    private final RequestHandler handler;
    private final int port;
    private final String uri;

    /**
     * @see TCPServer Starts a TCPServer
     *
     * @param handler Listener which will be notified if an connection is established
     * @param port Port the Server is listening on
     * @throws IOException
     */
    public TCPStreamStub(RequestHandler handler, int port) throws IOException {
        this.handler = handler;
        this.port = port;
        
        this.uri = null; // TODO - shouldn't be in this class but in a HTTPStub
    }

    public final void start() throws IOException {
        if(!this.started()) {
            try {
                this.server = new TCPServer(this.port, this.handler, this);
                new Thread(server).start();

            } catch (IOException ex) {
                System.err.println("cannot create TCPStreamStub: fatal");
                throw ex;
            }
        }
    }
    
    public boolean started() {
        return this.server != null;
    }

    /**
     * Puts the server on hold. The server will not be shut down but
     * also will not receive new incomming connections.
     *
     * @see TCPServer
     */
    public void stop() {
        if(this.started()) {
            this.server.hold();
            this.server = null;
        }
    }

    /**
     * TODO: It's actually the HTTPStub - should be in its own class.
     * @param handler
     * @param port
     * @param uri
     * @throws IOException
     */
    public TCPStreamStub(RequestHandler handler, int port, String uri) throws IOException {
        this.handler = handler;
        this.port = port;
        this.uri = uri;
        
        try {
            this.server = new WebServer(port, handler, this);
            new Thread(server).start();

        } catch (IOException ex) {
            System.err.println("cannot create TCPStreamStub: fatal");
            throw ex;
        }
    }

    /**
     * Retrieves the IP of the devices which is running the client at the moment.
     *
     * @return null if an error occurs
     * @return "tcp://hostname:port"
     */
    public String getLocalAddress() {

        /*
         * First try to get the ip address independently of the platform by using
         * tcp connections to a given server and reading out the local address
         * of the socket that is being used.
         */
//        try {
//          Socket socket = new Socket("www.google.com", 80);
//          socket.setSoTimeout(500);
//          String localaddress = socket.getLocalAddress().toString();
//
//          if(localaddress.startsWith("/")) {
//            localaddress = localaddress.substring(1);
//          }
//          L.l(localaddress, this);
//
//          System.out.println("Local address is: " + localaddress);
//          return "tcp://" + localaddress + ":" + Integer.toString(this.server.getPortNumber());
//        } catch (Exception e) {
          /*
         * This is the fallback if the above function throws an exception.
         * This is only the second best choice as its returnvalues 
         */
        // Not possible. Try 'old' way.
        String hostName = null;
        //L.e(e.getMessage(), this);
        try {
            /*
             * Platform specific behaviour! Works in SE, does not work under Android!
             * Returns "localhost" when used in Android.
             */
            InetAddress adr = InetAddress.getLocalHost();
            hostName = adr.getHostAddress();
            hostName = Util.DNtoIP(hostName);
        } catch (UnknownHostException ex) {
            return null;
        }
        String addr = "tcp://" + hostName + ":";
        if (this.server != null) {
            addr += Integer.toString(this.server.getPortNumber());
        } else {
			addr += J2SEAndroidSharkEngine.defaultTCPPort;
        }
        L.d("Local address is: "+addr, this);
        return addr;
    }
    
    /**
     * Retrieves hostname and port from the address String and creates a new
     * @see TCPConnection .
     *
     * @param addrStr e.g. tcp://213.32.123.42:4221
     * @return TCPConnection new TCPConnection for the given address String
     * @throws IOException
     */
    public StreamConnection createStreamConnection(String addrStr) throws IOException {
        if (addrStr.startsWith("http://")) {
            // TODO: http request erzeugen und senden. immer?
            String addrStr2 = addrStr.substring("http://".length());
            String host = addrStr2;
            int portno = 8080;
            if (addrStr2.contains(":")) {
                StringTokenizer st = new StringTokenizer(addrStr2, ":");
                host = st.nextToken();
                String port = st.nextToken();

                try {
                    portno = Integer.parseInt(port);

                } catch (NumberFormatException nfe) {
                    L.e("Unable to extract Port from URL: " + addrStr, this);
                }
            }

            if (host.equalsIgnoreCase("localhost") && portno == this.server.getPortNumber()) {
                System.err.println("cannot create Connection: would be a message loop");
                throw new IOException("message loop detected");
            }
            return new HTTPConnection(host, portno, this.getLocalAddress(), true);
        }

        // shark uses GCF convention to describe addresses - translate it
        if (!addrStr.startsWith("tcp://")) {
            System.err.println("TCP stub cannot send to address:" + addrStr);
            return null;
        }

        addrStr = addrStr.substring("tcp://".length());

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
        if (this.server != null && hostname.equalsIgnoreCase("localhost") && portno == this.server.getPortNumber()) {
            System.err.println("cannot create Connection: would be a message loop");
            throw new IOException("message loop detected");
        }

//        return new TCPConnection(hostname, portno, this.getLocalAddress());
        return new TCPConnection(hostname, portno);
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this.server.setHandler(handler);
    }
}
