/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.peer.peersensor.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerAssociatedSemanticTag;
import net.sharkfw.system.Util;

/**
 *
 * @author Jacob Zschunke
 */
public class BTDiscoveryAgent extends Thread implements DiscoveryListener {

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private UUID[] uuidSet;
    private BTPeerSensor sensor;
    private boolean peersDiscovered = false;
    private boolean isOnHold = false;

    public BTDiscoveryAgent(BTPeerSensor sensor) {
        this.sensor = sensor;
    }

    public void run() {
        while(!isOnHold) {
            this.findServer();
        }
    }

    void findServer() {
        try {
            this.localDevice = LocalDevice.getLocalDevice();
            this.discoveryAgent = localDevice.getDiscoveryAgent();

            this.uuidSet = new UUID[2];
            this.uuidSet[0] = new UUID(0x1101);
            this.uuidSet[1] = BTService.UUID;

            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);

        } catch (Exception ex) {
            // ups...
        }

    }

    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
        try {
            discoveryAgent.searchServices(null, uuidSet, rd, this);
        } catch (BluetoothStateException ex) {
        }
    }

    public void inquiryCompleted(int i) {
        if(peersDiscovered) {
            sensor.callListener();
            peersDiscovered = false;
        }
    }

    public void serviceSearchCompleted(int i, int i1) {
    }

    public void servicesDiscovered(int j, ServiceRecord[] srs) {
        for (int i = 0; i < srs.length; i++) {
            String url = srs[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (Util.contains(url, BTService.UUID.toString())) {
                sensor.addPeer(createPeerTag(srs[i]));
                peersDiscovered = true;
                break;
            }
        }
    }

    private PeerSemanticTag createPeerTag(ServiceRecord service) {
        String[] addr = {service.getHostDevice().getBluetoothAddress(), BTPeerSensor.HUB_IP};
        String name = addr[0];
        String[] si = {"http://" + addr[0]};
        
        return new InMemoPeerAssociatedSemanticTag(name, name, si, addr);
    }

    public void hold() {
        this.isOnHold = true;
    }

    public void resume() {
        this.isOnHold = false;
    }
}
