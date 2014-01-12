/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.peer.peersensor.bluetooth;

import java.io.IOException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

/**
 *
 * @author Desty Nova
 */
public class BTService extends Thread {

    private boolean isOnHold = false;
    private LocalDevice localDevice;
    private ServiceRecord service;
    private StreamConnectionNotifier notifier;
    
    public static final UUID UUID = new UUID("624AC902C9FCD9D0F123B60CEE90D35F", false);

    public BTService() {
    }

    public void run() {
        try {
            this.localDevice = LocalDevice.getLocalDevice();

            this.localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            StringBuffer url = new StringBuffer("btspp://");
            url.append("localhost").append(':');
            url.append(UUID.toString());
            url.append(";name=" + "Shark BT Discovery Service");
            url.append(";authorize=false");

            this.notifier = (StreamConnectionNotifier) Connector.open(url.toString());

            this.service = localDevice.getRecord(notifier);

            while (true) {
                notifier.acceptAndOpen();
            }
        } catch (IOException ex) {
            // do nothing
        }
    }

    public void hold() {
    }

    public void resume() {
    }
}
