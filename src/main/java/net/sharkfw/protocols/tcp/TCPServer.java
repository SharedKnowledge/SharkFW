package net.sharkfw.protocols.tcp;

import java.io.*;
import java.net.*;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.protocols.ConnectionStatusListener;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.system.L;

/**
 * A TCPServer which is just listening on a specific port. It will not
 * send or edit the incoming data. If a connection is established it
 * will be stored in a TCPConnection object and the Listener (RequestHandler)
 * will be notified with the new TCPConnection. After notifying the Listener
 * the Server will wait for the next incoming Connection.
 *
 * @author thsc
 */
class TCPServer implements SharkServer {

    private final ASIPKnowledge knowledge;
    private final ConnectionStatusListener listener;
    protected int port;
    protected ServerSocket listen_socket;
    boolean active = true;
    private RequestHandler handler;
    private StreamStub stub;
    private int socketTimeout = 10000;

    /**
     * A Server Socket will be created with the given port.
     *
     * @param port Port the server will listen on
     * @param handler Listener which will be notified if a connection is established
     * @param stub the Stub which created the Server (here TCPStreamstub) used to get the local address of the device
     * @throws IOException
     */
    public TCPServer(int port, RequestHandler handler, StreamStub stub, ASIPKnowledge knowledge, ConnectionStatusListener listener)
            throws IOException {
        try {
            if (port == Protocols.ARBITRARY_PORT) {
                listen_socket = new ServerSocket(0);
            } else {
                listen_socket = new ServerSocket(port);
            }
        } catch (IOException e) {
            throw e;
        }

        this.port = listen_socket.getLocalPort();
        this.handler = handler;
        this.stub = stub;
        this.knowledge = knowledge;
        this.listener = listener;

        L.l("TCP Server is bound to port " + this.port, this);
    }



    /**
     * Return the portnumber this server is listening at.
     * @return the current Port the Server is bound to.
     */
    
    public int getPortNumber() {
        return this.port;
    }

    /**
     * Return the local address of this TCPServer.
     * @see TCPStreamStub#getLocalAddress()
     * @return the IP address of the current device
     */
    protected String getLocalAddress() {
        L.d("Listensocket has: " + this.listen_socket.getLocalSocketAddress().toString(), this);
        return this.stub.getLocalAddress();
    }

    /**
     * Puts the server on hold. It will not listen anymore for new
     * incoming connections and will not notify any Listener. The server
     * ist not shut down and can resume anytime through the run() method.
     */
    public void hold() {
        this.active = false;
        try {
            this.listen_socket.close();
        } catch (IOException ex) {
            L.e("TCP Server hold failed: " + ex.getMessage(), this);
        }
    }

    /**
     * The server waits for incoming connections. If there is one established
     * a TCPConnection will be created with the given socket and the local
     * address. The new TCPConnection is given to the Listener (RequestHandler)
     * in order to notify the Listener.
     */
    public void run() {
        try {
            //while (this.active && !isInterrupted()) {
            while (this.active) {
                L.d("TCP Server accepts connection requests on: " + this.port, this);

                Socket client_socket = this.listen_socket.accept();
                client_socket.setSoTimeout(this.socketTimeout);
                TCPConnection con = new TCPConnection(client_socket, this.getLocalAddress());
                if(this.listener!=null){
                    con.addConnectionListener(this.listener);
                }

                L.d("Calling handler for stream on: " + this.port, this);
                handler.handleStream(con, this.knowledge);
            }
            //L.d("Closing socket", this);
            this.listen_socket.close();

        } catch (IOException e) {
            // nothing todo
        }
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
