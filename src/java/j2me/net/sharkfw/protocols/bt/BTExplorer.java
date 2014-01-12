/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import net.sharkfw.kep.PeerHandler;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamStub;

/**
 *
 * @author mfi
 */
public class BTExplorer extends Thread implements DiscoveryListener {

    /**
     * These Strings are the uuids which we use to register our service
     */
    public static final String UUIDString_rfcomm = "102030405060708090A1B1C1D1D1E100";
    public static final String UUIDString_l2cap = "102030405060708090A1B1C1D1D1E1AA";

    public static final int RXMTU = 512;
    public static final int TXMTU = 512;

    private LocalDevice ldev;

    private DiscoveryAgent dAgent;

    private PeerHandler pHandler;
    private StreamStub streamStub;
    private MessageStub messageStub;

    private Object monitor = new Object();

    private UUID uuid;
    private UUID[] uuidSet;

    private Vector discoveredDevices = new Vector();
    private Vector knownDevices = new Vector();

    private boolean active = false;
    private boolean isL2CAP = false;
    private boolean isRFCOMM = false;

    /* keep track of all transactions in order to shut them down in a clean manner */
    private Vector transIds = new Vector();

    private boolean btOk = false;

    /* Singleton */
    
    private static BTExplorer btexplorer;

    public static BTExplorer getInstanceForL2CAP(PeerHandler peerHandler, MessageStub stub){
        if(btexplorer == null){
            btexplorer = new BTExplorer(peerHandler, stub);
        }
        return btexplorer;
    }

    public static BTExplorer getInstanceForRFCOMM(PeerHandler peerHandler, StreamStub stub){
        if(btexplorer == null){
            btexplorer = new BTExplorer(peerHandler, stub);
        } 
        return btexplorer;
    }
    /* =====================================================================*/

    private BTExplorer(PeerHandler peerHandler, StreamStub stub){
        this.pHandler = peerHandler;
        this.streamStub = stub;
        
        this.uuid = new UUID(UUIDString_rfcomm,false);
        this.isRFCOMM = true;
    }

    private BTExplorer(PeerHandler peerHandler, MessageStub stub){
        this.pHandler = peerHandler;
        this.messageStub = stub;

        this.uuid = new UUID(UUIDString_l2cap,false);
        this.isL2CAP = true;
    }

    public void run(){

        this.uuidSet = new UUID[1];
        this.uuidSet[0] = this.uuid;

        this.active = true;

        /* one time init of the local hardware */
        try {
            this.ldev = LocalDevice.getLocalDevice();
            this.dAgent = this.ldev.getDiscoveryAgent();

            this.btOk = true;
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();

            this.btOk = false;
        }
        
                
        while(active  && btOk){

            System.out.println("Explorer durchlauf");
            try {

                this.dAgent.startInquiry(DiscoveryAgent.GIAC, this);

                synchronized(this.monitor){
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                // Deviceinquiry finished :)
                // Now find shark-devices!

                Enumeration discovered = this.discoveredDevices.elements();
                while(discovered.hasMoreElements()){
                    RemoteDevice btDev = (RemoteDevice) discovered.nextElement();
                    // For every device we have found ...
                    this.dAgent.searchServices(null, this.uuidSet, btDev, this);

                    synchronized(this.monitor){
                        try {
                            monitor.wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            } catch (BluetoothStateException ex) {

                ex.printStackTrace();
            }

            // sleep 2 sec
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        System.out.println("SYS:: found " + btDevice.getBluetoothAddress());
        if(!this.discoveredDevices.contains(btDevice)){
            System.out.println("SYS:: added " + btDevice.getBluetoothAddress());
            this.discoveredDevices.addElement(btDevice);
        }
    }

    /**
     *
     */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        
        this.transIds.addElement(new Integer(transID));

        System.out.println("services Discovered!");
        // Shark-enabled device found!
        for(int i = 0; i < servRecord.length; i++){
            RemoteDevice rDev = servRecord[i].getHostDevice();

            
           if(this.knownDevices.contains(rDev)){
                // if we already know this one, there is no need to start all over again!
                return;
            }

            /* try to find name */
            String peerName = "Unknown";
            try {
                peerName = rDev.getFriendlyName(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            String serviceName = "shark";

            String addressString = servRecord[i].getConnectionURL(0, false);

            this.knownDevices.addElement(rDev);
            
            if(this.isL2CAP){
                this.pHandler.handlePeer(peerName, serviceName, addressString, 1, messageStub);
            } else if(this.isRFCOMM){
                this.pHandler.handlePeer(peerName, serviceName, addressString, 0, streamStub);
            }
        }

            
        }
    

    public void serviceSearchCompleted(int transID, int respCode) {
        synchronized(this.monitor){
            this.transIds.removeElement(new Integer(transID));
            this.monitor.notifyAll();
        }
    }

    public void inquiryCompleted(int discType) {
        // finished :)
        synchronized(this.monitor){
            this.monitor.notifyAll();
        }
    }

    public void hold(){
        this.active = false;
        if(this.dAgent != null){
            dAgent.cancelInquiry(this);

            // stop all searches for devices or services!
            Enumeration transEnumeration = this.transIds.elements();
            while(transEnumeration.hasMoreElements()){
                // only if the field has been used!
                Integer id = (Integer) transEnumeration.nextElement();
                this.dAgent.cancelServiceSearch(id.intValue());
                }
            }
        }

    public void destroy(){
        System.out.println("BTX: Destroy gerufen!");
        this.hold();
        
        BTExplorer.btexplorer = null;
        System.out.println("Destroy beendet!");
    }
    

    public void go(){
        this.active = true;
    }


}
