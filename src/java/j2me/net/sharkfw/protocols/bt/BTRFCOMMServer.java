/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;

import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;
import net.sharkfw.protocols.RequestHandler;

/**
 * This server runs endlessly, until one stops it by calling hold().+
 * It waits for clients that want to connect to the shark uuid
 *
 * @author mfi
 */
class BTRFCOMMServer extends Thread {

    private boolean active = false;
    private LocalDevice ldev;
    private RequestHandler rHandler;

    private UUID uuid = new UUID(BTExplorer.UUIDString_rfcomm,false);

    private StreamConnectionNotifier server;

/**
 * Instantiate a new RFComm Server and let it know where to report incoming requests to.
 * @param requestHandler The part of the core, that handles requests from other peers
 * @throws javax.bluetooth.BluetoothStateException if a BT-Hardware error occurrs
 */
    public BTRFCOMMServer(RequestHandler requestHandler) throws BluetoothStateException{
        this.ldev = LocalDevice.getLocalDevice();
        this.ldev.setDiscoverable(DiscoveryAgent.GIAC);

        this.rHandler = requestHandler;
    }

    /**
     *
     * @return a String tht can be used as a connection url in gcf notation
     */
    public String getReplyAddressString(){
        try {
            /* try to get the connection url from the service record, that could have been created by the server */
            return LocalDevice.getLocalDevice().getRecord(server).getConnectionURL(0, false);
        } catch (Exception ex) {
            /* if that fails (server not yet started i.e.) return an concatenated string */
            return "btspp://"+ this.ldev.getBluetoothAddress() + ":" + BTExplorer.UUIDString_rfcomm;
        }
        
    }

    /**
     * method to get the thread running
     */
    public void run(){

        this.active = true;

        while(active){
            try {
                
                this.server = (StreamConnectionNotifier) Connector.open("btspp://localhost:" + BTExplorer.UUIDString_rfcomm);

                // block until a connection is started
                javax.microedition.io.StreamConnection conn = server.acceptAndOpen();
                // received connection

                
                net.sharkfw.protocols.bt.BTRFCOMMConnection sharkConnection = new net.sharkfw.protocols.bt.BTRFCOMMConnection(conn, "btspp://" + this.ldev.getBluetoothAddress() + ":" + BTExplorer.UUIDString_rfcomm);

                this.rHandler.handleStream(sharkConnection);

                this.server.close();
            } catch (IOException ex) {
                System.err.println("Unable to start service");
                ex.printStackTrace();
            }

        }

    }

    public void hold(){
        this.active = false;
        try {
            this.ldev.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }
    }
    
    public void go(){
        this.active = true;
    }
}
