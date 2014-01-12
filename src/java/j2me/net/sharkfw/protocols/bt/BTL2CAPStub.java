/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;


import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import net.sharkfw.kep.PeerHandler;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;

/**
 *
 * @author mfi
 */
public class BTL2CAPStub implements MessageStub {

    private BTExplorer btx;
    private BTL2CAPServer server;

    private RequestHandler rHandler;
    private PeerHandler pHandler;

    private String replyAddressString;

    private BTL2CAPClient client;

    public BTL2CAPStub(RequestHandler requestHandler, PeerHandler peerhandler){
        try {
            this.rHandler = requestHandler;
            this.pHandler = peerhandler;
            this.server = new BTL2CAPServer(this.rHandler, this);
            this.server.start();
            this.btx = BTExplorer.getInstanceForL2CAP(peerhandler, this);
            this.btx.start();
            
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }

    }
    /**
     */
    public void setReplyAddressString(String addr) {
        this.replyAddressString = addr;
    }

    /**
     */
    public void sendMessage(String msg, String recAddress) {
        if(this.client == null){
            this.client = new BTL2CAPClient();
        }
        try {
            System.out.println("appending to queue ..");
            client.sendMessage(msg, recAddress);
            
        } catch (IOException ex) {
            // What to do?
            ex.printStackTrace();
        }
    }

    /**
     */
    public void stop() {
        this.server.hold();
        this.client.hold();
        this.btx.destroy();

        this.server = null;
        this.client = null;
        this.btx  = null;
    }

    /**
     */
    public String getReplyAddressString() {
        if(this.replyAddressString != null){
            return this.replyAddressString;
        } else {
            return this.server.getLocalAdress();
        }
    }

}
