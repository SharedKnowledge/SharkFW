package net.sharkfw.protocols.wifidirect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.peer.SharkEngine;

import net.sharkfw.protocols.ConnectionStatusListener;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.protocols.tcp.TCPConnection;
import net.sharkfw.protocols.tcp.TCPStreamStub;

/** Searches for WifiDirect peers in range and tries to establish connections to them.
 * 	Since every device can be the Group owner in Wifi Direct (Server) every device will 
 * 	also create TCPServer in case it gets the Server. After Establishing a connection to a device the 
 * 	connection will be handled by the Sharkengine. This Streamstub will also 
 * 	observe itself and restart in case it got unstable.
 * 
 * 	Make sure to call stop()!
 * 	
 */

public class WifiDirectStreamStub extends BroadcastReceiver implements StreamStub, PeerListListener, ConnectionInfoListener, ConnectionStatusListener {
	/** TCP port */
	private final static int PORT = 8955;

	/** Possible states of the WifiDirectStreamStub. */
	public enum WifiDirectStreamStubState {
		NOT_READY,
		READY,
		DISCOVERING,
		CONNECTING,
		CONNECTED
	}

	/** Observes the WifiDirectStreamStub and resets it, if it becomes unstable.
	 *  Checks every 5 seconds the state of the WifiDirectSstreamStub and resets it if 
	 *  it got stuck in a connecting process.
	 *  
	 *  */
	private class WifiDirectControlThread extends Thread {
		/** The wifi stub. */
		private WifiDirectStreamStub _stub;
		private boolean _stopped = false;

		/** Creates the control thread. */
		public WifiDirectControlThread(WifiDirectStreamStub stub) {
			_stub = stub;
		}

		/** Returns the observed wifi stub. */
		public WifiDirectStreamStub getStub() {
			return _stub;
		}

		/** Causes the control thread to be stopped as soon as possible. */
		public void stopThread() {
			_stopped = true;
		}
		
		/** Returns true, if the control thread has stopped or is about to be stopped. */
		public boolean stopped() {
			return _stopped;
		}

		/** Runs the wifi control thread. The wifi stub is restarted if it's broken (state = NOT_READY) or a connection attempt
		 *	is unanswered for at least 30 seconds (which is not handled by the wifi-framework). A new discovery is going to be
		 *	initiated if there is no ongoing discovery or connection attempt. If the last connection to a peer was longer than 30
		 *	seconds ago, a new connection is going to be initiated.
		 */
		@Override
		public void run() {

			while(!stopped()) {
				long now = System.currentTimeMillis();
				WifiDirectStreamStub stub = getStub();
				if (stub.getState() == WifiDirectStreamStubState.NOT_READY)
					getStub().start();
				if (stub.getState() == WifiDirectStreamStubState.READY)
					if (stub.getStateTimestamp() + 5000 < now)
						getStub().startDiscovery();
				if (stub.getState() == WifiDirectStreamStubState.DISCOVERING) {
					if (stub.getStateTimestamp() + 120000 < now) {
						log("Discovering since 2 min, restarting");
						init();
					}
					if (stub.getStateTimestamp() + 5000 < now) {
						Log.d("WifiDirect", "discovering since at least 5000ms");
						if (stub.getDeviceListTimestamp() > stub.getStateTimestamp() && !stub.getDeviceList().isEmpty()) {
							Log.d("WifiDirect", "new check for possible connection attempts should be started");
							getStub().initiateConnect();
						}
					}
				}
				if (getStub().getState() == WifiDirectStreamStubState.CONNECTING) {
					if (getStub().getStateTimestamp() + 30000 < now) {
						/** no anwser for 30 seconds*/
						Log.d("WifiDirect", "selected peer did not answer the connection attempt for 30000ms - restarting");
						getStub().start();
					}
					
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// 	do nothing
				}
			}
		}
	}

	/** The control thread. */
	private WifiDirectControlThread _controlThread;
	/** Current context. */
	private Context _context;
	/** The shark engine. */
	private SharkEngine _sharkEngine;
	/** Main class of the android wifi framework. */
	private WifiP2pManager _manager;
	/** Wifi channel. */
	private Channel _channel;
	/** Request handler. */
	private RequestHandler _handler;
	/** Server-side used tcp stream stub. */
	private TCPStreamStub _tcpStreamStub;
	/** Server-side tcp connection. */
	private TCPConnection _tcpConnection;
	/** Service name used for only finding shark peers */
	private String servicename = "shark_peer";
	private String _connectionStr = "";
	private NetworkInfo _networkInfo;
	

	public String getConnectionStr() {
		return _connectionStr;
	}

	/** Stores the timestamps for the last connection attempts. */
	private WifiDirectPeerSelectionManager _peerSelectionManager = new WifiDirectPeerSelectionManager();

	/** Stores the available devices. */
	private List<WifiP2pDevice> _deviceList = new ArrayList<WifiP2pDevice>();
	/** Timestamp of the current device list. */
	private long _deviceListTimestamp = 0;
	/** Info about the current connection. */
	private WifiP2pInfo _info = null;
	/** Connected peer. */
	private WifiP2pDevice _device = null;

	/** Current stub state. */
	private WifiDirectStreamStubState _state = WifiDirectStreamStubState.NOT_READY;
	/** Timestamp of the last state change. */
	private long _stateTimestamp;
	/** True, if wifi is enabled, false otherwise. */
	private boolean _isWifiP2pEnabled = false;
	/** true, if the BroadcastReceiver was registered, false otherwise. */
	private boolean _receiverRegistered = false;
	/** Server-side used request handler. Sets the connection string and calls the standard handler. */
	private RequestHandler _internHandler = new RequestHandler() {
		public void handleMessage(byte[] msg, MessageStub stub) {
			WifiDirectStreamStub.this._handler.handleMessage(msg, stub);
		}
		public void handleStream(StreamConnection con) {
			WifiDirectStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
			WifiDirectStreamStub.this._handler.handleStream(con);
		}
		@Override
		public void handleNewConnectionStream(StreamConnection con) {
			WifiDirectStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
			WifiDirectStreamStub.this._handler.handleNewConnectionStream(con);
		}
	};

    /** Creates the wifi direct stream stub. 
     * @throws SharkProtocolNotSupportedException */
	public WifiDirectStreamStub(Context context, SharkEngine sharkEngine, RequestHandler requestHandler) throws SharkProtocolNotSupportedException {
		_context = context;
		_manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
		if (_manager == null) {
			/** Wifi direct is not supported*/
			throw new SharkProtocolNotSupportedException("wifi direct is not supported");
		}
        _sharkEngine = sharkEngine;
        _handler = requestHandler;
    }

	/** Logs the given string on the console with the "WifiDirect" tag. */
	private void log(String s) {
		Log.d("WifiDirect", s);
	}

	/** Sets a new state. */
	private void setState(WifiDirectStreamStubState state) {
		_state = state;
		_stateTimestamp = System.currentTimeMillis();
		log("New state: " + state);
	}

	/** Sets the request handler used at server side. */
	@Override
	public void setHandler(RequestHandler handler) {
		_handler = handler;
	}

	/** Gets the current state. */
	public WifiDirectStreamStubState getState() {
		return _state;
	}

	/** Gets the timestamp of the last state change. */
	public long getStateTimestamp() {
		return _stateTimestamp;
	}

	/** Registers on the android system for the following events: Wifi is turned on/off, a new device list is available,
	 *	the wifi connection state changed. These evWents can now be handled by the wifi stub. */
	private void registerBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        _context.registerReceiver(this, intentFilter);
        _receiverRegistered = true;
	}

	/** Unregisters all events. */
	private void unregisterBroadcastReceiver() {
		if (_receiverRegistered) {
			_context.unregisterReceiver(this);
			_receiverRegistered = false;
		}
	}
	/** 
	 * The purpose of using Service discovery 
	 * is that only shark peers will try to connect to each other.
	 */

	/** Initiates the wifi framework and the wifi stream stub. Creates a wifi channel, registers for wifi events, starts the wifi control
	 *	thread and starts a tcp server. */
	private void init() {
		log("init");
		_channel = _manager.initialize(_context, _context.getMainLooper(), null); // callback for loss of framework communication should be added
		_peerSelectionManager.reset();
		registerBroadcastReceiver();
		setState(WifiDirectStreamStubState.READY);
		if (_controlThread == null) {
			log("controll thread == null");
			_controlThread = new WifiDirectControlThread(this);
			_controlThread.start();
		}
		if (_tcpStreamStub == null) {
			try {
				log("controll stub == null");
				_tcpStreamStub = new TCPStreamStub(_internHandler, PORT);
				_tcpStreamStub.start();
			} catch (IOException e) {
				log("FATAL!! Creating TCP Server failed!!!");
				Log.e("WifiDirect", "msg >> " + e);
			}
		}
	}

	/** Starts or restarts the StreamStub. Closes the current connection (if connected) and initiates a new peer discovery.
	 *
	 */
	@Override
	public void start() {
		log("start :: State: " + getState());
		if (_state == WifiDirectStreamStubState.NOT_READY) {
			init();
		} else if (_state != WifiDirectStreamStubState.READY) {
			disconnect();
		}
	}
	/** Starts a new discovery. The stub must be started first.
	 *
	 */
	private void startDiscovery() {
		log("startDiscovery :: State: " + getState());
		setState(WifiDirectStreamStubState.DISCOVERING);
		_manager.discoverPeers(_channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            	log("Discovery initiated.");
            }

            @Override
            public void onFailure(int reasonCode) {
            	log("Discovery initiation failed. Reason: " + reasonCode);
            	setState(WifiDirectStreamStubState.READY);
            }
        });
	}

	/** Closes the current connection (if connected) without starting a new one.
	 *
	 */
	public void disconnect() {
		log("disconnect :: State: " + getState());
		if (_state == WifiDirectStreamStubState.DISCOVERING) {
			_manager.stopPeerDiscovery(_channel, null); 
		} else if (_state == WifiDirectStreamStubState.CONNECTING) {
			_manager.cancelConnect(_channel, null);
			setState(WifiDirectStreamStubState.READY);
		} else if (_state == WifiDirectStreamStubState.CONNECTED) {
			if (_tcpConnection != null) {
				_tcpConnection.close();
				_tcpConnection = null;
			}
			_manager.removeGroup(_channel,new ActionListener() {

	            @Override
	            public void onFailure(int reasonCode) {
	              log("Disconnect failed. Reason :" + reasonCode);

	            }

	            @Override
	            public void onSuccess() {
	              log("Disconnect succeed");
	            }

	        });
	    
			setState(WifiDirectStreamStubState.READY);
		}
	}

	/** Stops the StreamStub and the control thread. Unregisters from all wifi events. Sets the state to NOT_READY.
	 *
	 */
	@Override
	public void stop() {
		log("stop :: State: " + getState());
		unregisterBroadcastReceiver();
		if (_controlThread != null) {
			_controlThread.stopThread();
			_controlThread.interrupt();
		}
		if (_tcpStreamStub != null) {
			_tcpStreamStub.stop();
			_tcpStreamStub = null;
		}
		disconnect();
		setState(WifiDirectStreamStubState.NOT_READY);
	}

	/** True, if the wifi stream stub was started, false otherwise. */
	@Override
	public boolean started() {
		return _state != WifiDirectStreamStubState.NOT_READY;
	}

	/** Throws an IOException, because the wifi stream stub cannot create a stream connection. */
	@Override
	public StreamConnection createStreamConnection(String addressString)
			throws IOException {
		throw new IOException("WifiDirectStreamStub cannot create a stream connection");
	}

	/** Returns null. */
	@Override
	public String getLocalAddress() {
		return null;
	}

	/** Called if peers are available. Updates the device list and initiates the peer connection decision */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		log("onPeersAvailable :: State: " + getState());
		if (_state == WifiDirectStreamStubState.DISCOVERING) {
			setDeviceList(peers.getDeviceList());
	        initiateConnect();
		}
	}

	/** Updates the device list. */
	public void setDeviceList(Collection<WifiP2pDevice> peers) {
		log("setDeviceList :: State: " + getState());
		_deviceList.clear();
        _deviceList.addAll(peers);
        _deviceListTimestamp = System.currentTimeMillis();
	}

	/** Returns the list of currently available devices. */
	public List<WifiP2pDevice> getDeviceList() {
		return _deviceList;
	}

	/** Timestamp of the current device list. */
	public long getDeviceListTimestamp() {
		return _deviceListTimestamp;
	}

	/** Decides, which device a wifi connection shall be initiated to. Starts the connection. */
	public void initiateConnect() {
		
		log("initiateConnect :: State: " + getState());
		if (_deviceList.size() > 0) { // devices found
	        _device = _peerSelectionManager.selectPeer(_deviceList);
	        if (_device != null) {
	        	log("connect initiated");
	        	setState(WifiDirectStreamStubState.CONNECTING);
		        final WifiP2pConfig config = new WifiP2pConfig();
		        config.deviceAddress = _device.deviceAddress;
		        config.wps.setup = WpsInfo.PBC;
		   
		        _manager.connect(_channel, config, new ActionListener() {
		            @Override
		            public void onSuccess() {
		            	// wird aufgerufen, wenn der Versuch, die Verbindung aufzubauen, gelingt.
		            	// Das andere Geraet erhaelt nun den Annehmen/Ablehnen-Dialog, der in dieser
		            	// Methode NICHT ausgewertet wird!
		            }
		            @Override
		            public void onFailure(int reason) {
		            	// wird aufgerufen, wenn der Versuch, die Verbindung aufzubauen, fehlschlaegt.
		            	// Zum Beispiel, wenn das andere Geraet zuerst versucht die Verbindung mit diesem
		            	// aufzubauen.
		            }
		        });
	        } else {
	        	log("connect initiated, but no peer available");
	        }
        }
	}

	/** Called if a connection to another device is ready or about to be established. If a new connection was made, the wifi-server does
	 *	nothing, because the tcp-server already is listening. If the device is the wifi-client it tries to open a tcp connection to the
	 *	other device's tcp-server. When this is done, SharkEngine.handleConnection() is called.
	 *
	 *	If opening the tcp connection fails, the wifi stub restarts.
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		
		log("on ConnectionInfo Available");
		if (_networkInfo.isConnected()) {
			log("connecting process");
			log("onConnectionInfoAvailable :: State: " + getState());
			log("...info.groupFormed=" + info.groupFormed + ", info.isGroupOwner=" + info.isGroupOwner);
			if (_state == WifiDirectStreamStubState.READY || _state == WifiDirectStreamStubState.CONNECTING || _state == WifiDirectStreamStubState.DISCOVERING) {
				this._info = info;
				/** server code*/
				if (info.groupFormed && info.isGroupOwner) { 
					setState(WifiDirectStreamStubState.CONNECTED);
					_peerSelectionManager.peerConnected(_device);
					Toast.makeText(_context, "Connection established. This is the server.", Toast.LENGTH_LONG).show();
					log("Connection established. This is the server.");
				/** client code*/
				} else if (info.groupFormed) { 
					setState(WifiDirectStreamStubState.CONNECTED);
					final InetAddress adr = info.groupOwnerAddress;
					/** the client wants to know with which device it is connected */
					_manager.requestGroupInfo(_channel, new WifiP2pManager.GroupInfoListener() {
						@Override
						public void onGroupInfoAvailable(WifiP2pGroup group) {
							/** the callback can give us a null pointer*/
							if (group != null) {
								_peerSelectionManager.peerConnected(group.getOwner());
							} else {
								log("Group owner = null");
							}
							
						}
					});
					new Thread(new Runnable() { 
						
						@Override
						public void run() {
							try {
								_tcpConnection = new TCPConnection(adr.getHostAddress(), PORT);
								
								_tcpConnection.addConnectionListener(WifiDirectStreamStub.this);
								_connectionStr = _tcpConnection.getReceiverAddressString();															
								/* calling the shark engine here*/
								_internHandler.handleNewConnectionStream(_tcpConnection);
							} catch (UnknownHostException e) {
								log("Host not found. Got some problems here... restarting!");
								Log.e("WifiDirect", "msg >> " + e);
								start();
							} catch (IOException e) {
								log("Creating TCP Connection failed! Restarting!");
								Log.e("WifiDirect", "msg >> " + e);
								start();
							}
						}

					}).start();
					Toast.makeText(_context, "Connection established. This is the client.", Toast.LENGTH_LONG).show();
					log("Connection established. This is the client.");
				} else /* groupFormed == false */ {
					log("groupformed == false");
					if (_state == WifiDirectStreamStubState.CONNECTING)
						log("connect unanswered ?");
					startDiscovery();
				}
			} else if (_state == WifiDirectStreamStubState.CONNECTED) {
				if (!info.groupFormed) {
					_peerSelectionManager.peerDisconnected(_device);
					
					startDiscovery();
				}
			}
		} else if(_networkInfo.getState() == NetworkInfo.State.DISCONNECTED ){
			log(_networkInfo.getDetailedState().name() + _networkInfo.getTypeName());
			log("disconnecting process");
			if (_networkInfo.getDetailedState() != NetworkInfo.DetailedState.FAILED)
			Toast.makeText(_context, "disconnected", Toast.LENGTH_SHORT).show();
			
			startDiscovery();

		}
	}

	/** Sets the current state of wifi direct. (enabled/disabled) */
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		_isWifiP2pEnabled = isWifiP2pEnabled;
	}

	/** Called if a wifi event occurres. Forwards the given information to the right method. */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                setIsWifiP2pEnabled(true);
            } else {
                setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	if (_state != WifiDirectStreamStubState.READY)
        		_manager.requestPeers(_channel, WifiDirectStreamStub.this);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	log("connection changed");
        	_networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	_manager.requestConnectionInfo(_channel, WifiDirectStreamStub.this);
         
        }
	}

	/** Called if the tcp connection was closed because of a connection timeout. The wifi stub restarts. */
	@Override
	public void connectionClosed() {
		start();
	}
}
