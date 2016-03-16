package net.sharkfw.protocols.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiP2pDeviceConnectionInfo implements Comparable {

	public enum DeviceConnectionState {
		NONE,
		CONNECTING,
		CONNECTED,
		CONNECTION_UNANSWERED,
		CONNECTION_FAILED
	}
	
	private WifiP2pDevice _device;
	private long _lastConnectionEstablishedTimestamp;
	private long _lastConnectionLostTimestamp;
	private boolean _isDeviceAvailable;
	private boolean _retry;
	
	public WifiP2pDeviceConnectionInfo(WifiP2pDevice device, long lastConnectionEstablishedTimestamp, long lastConnectionLostTimestamp, boolean isDeviceAvailable) {
		_device = device;
		_lastConnectionEstablishedTimestamp = lastConnectionEstablishedTimestamp;
		_lastConnectionLostTimestamp = lastConnectionLostTimestamp;
		_isDeviceAvailable = isDeviceAvailable;
	}
	
	public WifiP2pDevice getDevice() {
		return _device;
	}
	
	public long getLastConnectionEstablishedTimestamp() {
		return _lastConnectionEstablishedTimestamp;
	}
	
	public long getLastConnectionLostTimestamp() {
		return _lastConnectionLostTimestamp;
	}
	
	public boolean isDeviceAvailable() {
		return _isDeviceAvailable;
	}
	
	public void setLastConnectionEstablishedTimestamp(long lastConnectionEstablishedTimestamp) {
		_lastConnectionEstablishedTimestamp = lastConnectionEstablishedTimestamp;
	}
	
	public void setLastConnectionLostTimestamp(long lastConnectionLostTimestamp) {
		_lastConnectionLostTimestamp = lastConnectionLostTimestamp;
	}
	
	public void setIsDeviceAvailable(boolean isDeviceAvailable) {
		_isDeviceAvailable = isDeviceAvailable;
	}

	@Override
	public int compareTo(Object another) {
		WifiP2pDeviceConnectionInfo other = (WifiP2pDeviceConnectionInfo) another;
		if (other.getLastConnectionEstablishedTimestamp() == this.getLastConnectionEstablishedTimestamp())
			return 0;
		if (other.getLastConnectionEstablishedTimestamp() > this.getLastConnectionEstablishedTimestamp()) 
			return 1;
		else 
			return -1;
	}
	
}
