/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.tcp;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.system.L;

/**
 *
 * @author Romy Gerlach
 */
public class TCPServer extends Thread{

    private ServerSocketConnection listen_socket;
    private RequestHandler rhandler;
    private StreamStub sstub;
    private int portnumber;
    private boolean active = true;

    public TCPServer(int port, RequestHandler handler, StreamStub stub) throws IOException{
        if(port==Protocols.ARBITRARY_PORT){
            listen_socket = (ServerSocketConnection) Connector.open("socket://:2500");
        }
         else{
            listen_socket = (ServerSocketConnection) Connector.open("socket://:" + port);
         }
        rhandler=handler;
        sstub=stub;
        portnumber=listen_socket.getLocalPort();
    }

    int getPortnumber(){
        return this.portnumber;
    }

    protected String getLocalAddress(){
        return this.sstub.getLocalAddress();
//        try {
//            return this.listen_socket.getLocalAddress();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return "localhost";
//        }
    }

    public void hold(){
        this.active = false;
        L.d("Stopping TCP Server", this);
        try {
            this.listen_socket.close();
        } catch (IOException ex) {
            L.e("ERROR while closing serversocket", this);
            ex.printStackTrace();
        }
    }

    public void run(){
        L.d("TCP Server started on port: " + this.portnumber, this);
        while(this.active){
            try {
                SocketConnection client_socket = (SocketConnection) this.listen_socket.acceptAndOpen();
                L.d("TCP Server recieved connection.", this);
                TCPConnection con = new TCPConnection(client_socket, this.getLocalAddress());
                rhandler.handleStream(con);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            this.listen_socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
