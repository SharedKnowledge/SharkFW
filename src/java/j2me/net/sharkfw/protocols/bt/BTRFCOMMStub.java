/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;



import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import net.sharkfw.kep.PeerHandler;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

/**
 * This Class hides networking from the core. It offers a variety of methods to be called in order to establish connections
 * and handle requests from other peers. It consists of the BTExplorer, a Server and a mechanism for opening connections to other peers
 * 
 * @author mfi
 */
public class BTRFCOMMStub implements StreamStub{

    private BTExplorer btx;
    private BTRFCOMMServer server;

    private PeerHandler pHandler;
    private RequestHandler rHandler;


    public BTRFCOMMStub(PeerHandler peerHandler, RequestHandler requestHandler){
        try {
            this.pHandler = peerHandler;
            this.rHandler = requestHandler;
            this.server = new BTRFCOMMServer(this.rHandler);
            this.server.start();
            this.btx = BTExplorer.getInstanceForRFCOMM(peerHandler, this);
            this.btx.start();
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }


    }

    
    public StreamConnection createStreamConnection(String addressString) throws IOException {
        return new net.sharkfw.protocols.bt.BTRFCOMMConnection(addressString, this.server.getReplyAddressString());
    }

    public String getLocalAddress() {
       return this.server.getReplyAddressString();
    }

    public void stop() {
        this.server.hold();
        this.btx.destroy();
    }


}
