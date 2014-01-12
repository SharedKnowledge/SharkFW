/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer.peersensor.bluetooth;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.peer.PeerSensor;
import net.sharkfw.peer.SensorListener;

/**
 *
 * @author Jacob Zschunke
 */
public class BTPeerSensor extends PeerSensor {
    static String HUB_IP = "socket://127.0.0.1:7777";

    private boolean isOnHold = false;
    private BTDiscoveryAgent btAgent;
    private BTService btService;

    public BTPeerSensor() {
        super();
        btService = new BTService();
        btService.start();

        btAgent = new BTDiscoveryAgent(this);
        btAgent.start();
    }

    protected void callListener() {
        if(isOnHold) return;
        for(int i = 0; i < sensorListeners.size(); i++) {
            SensorListener listener = (SensorListener) sensorListeners.elementAt(0);
            listener.updateSensor(this);
        }
    }

    protected void addPeer(PeerSemanticTag discoveredPeer) {
        ont.createPeerSemanticTag(discoveredPeer.getName(),
                                  discoveredPeer.getSI(),
                                  discoveredPeer.getAddresses());
    }

    public void hold() {
        this.isOnHold = true;
    }

    public void resume() {
        this.isOnHold = false;
    }

}
