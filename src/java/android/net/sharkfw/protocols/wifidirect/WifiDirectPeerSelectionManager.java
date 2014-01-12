package net.sharkfw.protocols.wifidirect;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

public class WifiDirectPeerSelectionManager {

	//- PeerSelectionManager
	// Auswahl des Geräts nach Reihenfolge (längste Zeit keine Verbindung gehabt)
	// Wenn Verbindungsaufbau nicht initiiert werden kann: 60 sekunden nicht mehr versuchen
	// Wenn Verbindungsaufbau 30 Sekunden ohne Antwort bleibt: 30 weitere Sekunden pause
	
	private List<WifiP2pDeviceConnectionInfo> _peerList = new ArrayList<WifiP2pDeviceConnectionInfo>();
	private int _millisecondsBetweenReconnectionToSameDevice = 60000;
	
	public void setMillisecondsBetweenReconnectionToSameDevice(int millisecondsBetweenReconnectionToSameDevice) {
		_millisecondsBetweenReconnectionToSameDevice = millisecondsBetweenReconnectionToSameDevice;
	}
	
	public int getMillisecondsBetweenReconnectionToSameDevice() {
		return _millisecondsBetweenReconnectionToSameDevice;
	}
	
	WifiP2pDevice selectPeer(List<WifiP2pDevice> peers) {
		for (WifiP2pDeviceConnectionInfo info : _peerList)
			info.setIsDeviceAvailable(false);
        for (WifiP2pDevice device : peers) {
        	if (device != null) {
        		WifiP2pDeviceConnectionInfo info = getInfoByDevice(device);
        		if (info == null)
        			_peerList.add(new WifiP2pDeviceConnectionInfo(device, 0, 0, true));
        		else
        			info.setIsDeviceAvailable(true);
        	}
        }
        long lastConnectionLostTimestamp = Long.MAX_VALUE;
        long currentTimeMillis = System.currentTimeMillis();
        WifiP2pDeviceConnectionInfo takeInfo = null;
        for (WifiP2pDeviceConnectionInfo info : _peerList) {
        	if (info.isDeviceAvailable())
        		if (info.getLastConnectionLostTimestamp() < lastConnectionLostTimestamp && info.getLastConnectionLostTimestamp() + getMillisecondsBetweenReconnectionToSameDevice() < currentTimeMillis) {
        			lastConnectionLostTimestamp = info.getLastConnectionLostTimestamp();
        			takeInfo = info;
        		}
        }
        if (takeInfo != null) {
        	takeInfo.setLastConnectionLostTimestamp(System.currentTimeMillis());
        	return takeInfo.getDevice();
        } else
        	return null;
        //return (peers.size() > 0 ? peers.get(peers.size() - 1) : null);
		//return null;
	}
	
	void reset() {
		_peerList.clear();
	}
	
	private WifiP2pDeviceConnectionInfo getInfoByDevice(WifiP2pDevice device) {
		if (device != null)
			for (WifiP2pDeviceConnectionInfo info : _peerList)
				if (info.getDevice() != null)
					if (info.getDevice().equals(device))
						return info;
		return null;
	}
	
	public void peerConnected(WifiP2pDevice device) {
		WifiP2pDeviceConnectionInfo info = getInfoByDevice(device);
		if (info == null) {
			_peerList.add(new WifiP2pDeviceConnectionInfo(device, System.currentTimeMillis(), 0, true));
		} else {
			info.setLastConnectionEstablishedTimestamp(System.currentTimeMillis());
			info.setIsDeviceAvailable(true);
		}
	}
	
	public void peerDisconnected(WifiP2pDevice device) {
		WifiP2pDeviceConnectionInfo info = getInfoByDevice(device);
		if (info == null) {
			_peerList.add(new WifiP2pDeviceConnectionInfo(device, 0, System.currentTimeMillis(), true));
		} else {
			info.setLastConnectionLostTimestamp(System.currentTimeMillis());
			info.setIsDeviceAvailable(true);
		}
	}
}
