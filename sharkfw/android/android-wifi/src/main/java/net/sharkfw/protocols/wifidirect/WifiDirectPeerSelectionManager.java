package net.sharkfw.protocols.wifidirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import net.sharkfw.system.L;
import android.util.Log;

/** Manages the Peers the WifiDiriectStreamStub connects to.
 * 	It saves all peers we were connected to and decides which will be
 * 	the next peer to establish a connection.
 * @author jgig
 *
 */
public class WifiDirectPeerSelectionManager {


	private List<WifiP2pDeviceConnectionInfo> _peerList = new ArrayList<WifiP2pDeviceConnectionInfo>();
	
	
	 
	/** The delay between reconnect attempts when connection was lost or connection  attempts were unanswered in milliseconds*/
	private int _millisecondsBetweenReconnectionToSameDevice = 60000;
	/** The delay between reconnect attempts between devices that were connected previously*/
	private long _millisecondsBetweenSuccessfulReconnect;
	
	
	public WifiDirectPeerSelectionManager(long milliSecondsBetweenReconnect) {
		if (milliSecondsBetweenReconnect < 0) {
			throw new IllegalArgumentException("milliseconds cant be smaller or equal to 0");
		}
		_millisecondsBetweenSuccessfulReconnect = milliSecondsBetweenReconnect;
		
	}
	public WifiDirectPeerSelectionManager() {
		
		_millisecondsBetweenReconnectionToSameDevice = 60000;
		_millisecondsBetweenSuccessfulReconnect = 60000;
	}

	
	public void setMillisecondsBetweenReconnectionToSameDevice(int millisecondsBetweenReconnectionToSameDevice) {
		_millisecondsBetweenReconnectionToSameDevice = millisecondsBetweenReconnectionToSameDevice;
	}
	
	public int getMillisecondsBetweenReconnectionToSameDevice() {
		return _millisecondsBetweenReconnectionToSameDevice;
	}
	/**	Selects the next peer which we try to connect to. 
	 * 	It chooses the peer with which we were not connected for the longest time
	 * @param peers the Peerlist
	 * @return the Peer we want to connect to (can be null if no peer available)
	 */
	WifiP2pDevice selectPeer(List<WifiP2pDevice> peers) {
		for (WifiP2pDeviceConnectionInfo info : _peerList) {
			info.setIsDeviceAvailable(false);
		}	
        for (WifiP2pDevice device : peers) {
        	if (device == null) {
        		L.e("selectPeer device == null", this);
        	}
        	if (device != null) { 
        		WifiP2pDeviceConnectionInfo info = getInfoByDevice(device);
        		if (info == null) {
        			_peerList.add(new WifiP2pDeviceConnectionInfo(device, 0, 0, true));
        			
        		} else {
        			info.setIsDeviceAvailable(true);
        		}
        	} 
        }
        Collections.sort(_peerList);
        Collections.reverse(_peerList);
        
        long lastConnectionLostTimestamp = Long.MAX_VALUE;
        
        long currentTimeMillis = System.currentTimeMillis();
        WifiP2pDeviceConnectionInfo takeInfo = null;
        for (WifiP2pDeviceConnectionInfo info : _peerList) {
        	
        	if (info.isDeviceAvailable())
        		if (info.getLastConnectionEstablishedTimestamp() + _millisecondsBetweenSuccessfulReconnect < currentTimeMillis) {
        			if (info.getLastConnectionLostTimestamp() < lastConnectionLostTimestamp && info.getLastConnectionLostTimestamp() + getMillisecondsBetweenReconnectionToSameDevice() < currentTimeMillis) {
            			lastConnectionLostTimestamp = info.getLastConnectionLostTimestamp();
            			takeInfo = info;
            			
            		}
            
        		}
        }
        String peersString = "";
		
        for (WifiP2pDeviceConnectionInfo w : _peerList) {
        	if (w == null)
        		L.e("WifiP2pDeviceConnectionInfo == null", this);
        	if (w.getDevice() != null){
        		peersString +="," + w.getDevice().deviceAddress;
        	}
        	else  {
        		peersString +=",null";
        	}
        }
		
		L.e("---Peermanager has" +_peerList.size() + "peers saved atm. ---\n" + peersString ,this);
		Log.d("WifiDirect","---Peermanager has" +_peerList.size() + "peers saved atm. ---\n" + peersString);
		
        		
        if (takeInfo != null) {
        	takeInfo.setLastConnectionLostTimestamp(System.currentTimeMillis());
        	return takeInfo.getDevice();
        } else
        	return null;
      
	}
	
	void reset() {
		_peerList.clear();
	}
	
	private WifiP2pDeviceConnectionInfo getInfoByDevice(WifiP2pDevice device) {
		if (device == null) {
			L.e("Device == null",this);
		}
		if (device != null) {
			for (WifiP2pDeviceConnectionInfo info : _peerList)
				if (info.getDevice() != null) {
					if (info.getDevice().deviceAddress.equalsIgnoreCase(device.deviceAddress))
						return info;
				}
		
		}
		
		
		return null;
	}
	public void peerConnected(WifiP2pDevice device) {
		if (device == null) {
			L.e("peerConnected == null", this);
		}
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
