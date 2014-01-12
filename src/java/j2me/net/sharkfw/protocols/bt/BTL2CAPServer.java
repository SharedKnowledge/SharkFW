/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;


import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;

/**
 *
 * @author mfi
 */
public class BTL2CAPServer extends Thread {
    
    private boolean active;

    //private UUID uuid = new UUID(BTExplorer.UUIDString_l2cap,false);

    private RequestHandler rHandler;
    private MessageStub mStub;

    private L2CAPConnectionNotifier l2con;
    private LocalDevice lDev;

    private String connectionUrl = "btl2cap://localhost:" + BTExplorer.UUIDString_l2cap + ";ReceiveMTU="+BTExplorer.RXMTU + ";TransmitMTU="+BTExplorer.TXMTU;

    public BTL2CAPServer(RequestHandler requestHandler, MessageStub stub) throws BluetoothStateException{
        this.rHandler = requestHandler;
        this.mStub = stub;
        
        this.lDev = LocalDevice.getLocalDevice();
        this.lDev.setDiscoverable(DiscoveryAgent.GIAC); // as a server we need to be visible to other devices around!

    }

    public void go(){
        this.active = true;
    }

    public void hold(){
        this.active = false;
        try {
            this.lDev.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }
    }

    public void run(){
        this.active = true;

        while(active){

            try {
                this.l2con = (L2CAPConnectionNotifier) Connector.open(this.connectionUrl);
                L2CAPConnection connection = this.l2con.acceptAndOpen();

                // first we receive the length to know how many bytes we need to read afterwards
                byte[] lengthBuffer = new byte[4]; // 4 byte for an integer
                connection.receive(lengthBuffer);
                String lengthString = new String(lengthBuffer);
                lengthString = lengthString.trim();
                int messageLength = Integer.parseInt(lengthString);

                // now we receive the message with the given length
                byte[] messageBuffer = new byte[messageLength]; //allocate enough mem for message
                connection.receive(messageBuffer);

                String message = new String(messageBuffer);

                this.rHandler.handleMessage(message, this.mStub);

                this.l2con.close();


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getLocalAdress(){
            try {
                // try to return connection url from the service record
               return  this.lDev.getRecord(l2con).getConnectionURL(0, false);
            } catch (Exception ex) {
                return this.connectionUrl;
            }
        }
    
}
