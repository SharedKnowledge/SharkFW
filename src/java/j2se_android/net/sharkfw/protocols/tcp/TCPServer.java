package net.sharkfw.protocols.tcp;

import java.io.*;
import java.net.*;
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

    protected int port;
    protected ServerSocket listen_socket;
    boolean activ = true;
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
    public TCPServer(int port, RequestHandler handler, StreamStub stub)
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
        System.out.println("Listensocket has: " + this.listen_socket.getLocalSocketAddress().toString());
        return this.stub.getLocalAddress();
    }

    /**
     * Puts the server on hold. It will not listen anymore for new
     * incoming connections and will not notify any Listener. The server
     * ist not shut down and can resume anytime through the run() method.
     */
    public void hold() {
        this.activ = false;
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
            //while (this.activ && !isInterrupted()) {
            while (this.activ) {
                L.d("TCP Server accepts connection requests", this);

                Socket client_socket = this.listen_socket.accept();
                client_socket.setSoTimeout(this.socketTimeout);
                TCPConnection con = new TCPConnection(client_socket, this.getLocalAddress());

                L.d("Calling handler for stream", this);
                handler.handleStream(con);
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
