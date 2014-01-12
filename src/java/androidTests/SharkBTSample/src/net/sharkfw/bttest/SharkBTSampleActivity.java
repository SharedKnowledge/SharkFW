package net.sharkfw.bttest;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.GeoSemanticTag;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.AndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.protocols.Protocols;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author Jacob Zschunke
 * 
 * <h1>Important!</h1>
 * 
 * If you want to use this Activity you have to be sure there are following Permissions set in the Manifest-File:
 * <ul>
 * <li>android.permission.BLUETOOTH</li>
 * <li>android.permission.BLUETOOTH_ADMIN</li>
 * </ul>
 * 
 * Also be sure to add following external jars to the Project:
 * <ul>
 * <li>android_mail.jar</li>
 * <li>additional.jar</li>
 * <li>activation.jar</li>
 * </ul>
 * These jars are necessary because SharkEngine.start() tries to start every Engine including the MailEngine.
 * 
 * <h2>How To Use</h2>
 * 1. Run the Application from Eclipse, or if you already installed it, from the device.
 * 2. Select on the first device <i>create Alice</i> and on the second device <i>create Bob</i>.
 * 3.a Now you can either choose publish on the device running the alice peer or
 * 3.b You can press <i>start PeerSensor</i> for a discovery and auto publish.
 *     If you press <i>start PeerSensor</i> again the auto discovery will stop.
 *     
 * <h2>How it Works</h2>
 * <h3>create Alice</h3>
 * A peer named Alice will be created. Its bluetoothaddress is the address of the physical device.
 * Also a peer named Bob will be created. Its bluetoothaddress is the address of the other device 
 * (defined in static variables). There will be two <i>SemanticTag</i> <i>p2p</i> and <i>Japan</i>. 
 * With each an <i>ContextPoint</i>. There will be also 2 <i>Interest</i>s to <i>send</i> the 
 * <i>Knowledge</i>.
 * 
 * <h3>create Bob</h3>
 * A peer named Bob will be created. Its bluetoothaddress is the address of the physical device.
 * Bob will know two <i>SemanticTag</i>s: <i>p2p</i> and <i>Japan</i>. Bob will have <i>Interest</i>s
 * to receive <i>Knowledge</i> about these topics.
 * 
 * <h3>start PeerSensor</h3>
 * The device will scan all bluetooth enabled devices for a Shark-Service every 30 seconds. If there
 * is a Shark enabled device all KPs will be published. The found device is now on a blacklist for 
 * 1 minute.
 * 
 * <h3>publish</h3>
 * Only Alice can publish because she is the only peer which knows another peer.
 * 
 */
public class SharkBTSampleActivity extends Activity implements KnowledgeBaseListener, KPListener {
	
	// BT Adressen der 2 Galaxy Nexus geräte
	private static final String BT_ADDRESS_1 = "84:25:DB:C8:C0:DC";
	private static final String BT_ADDRESS_2 = "84:25:DB:C8:C0:96";
	
	private AndroidSharkEngine engine;
	private SharkKB kb;
	private TextView tv;
	private String myAdr;
	private Object otherAdr;
	private boolean isFinderRunning;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String s = BluetoothAdapter.getDefaultAdapter().getName() + ": " + BluetoothAdapter.getDefaultAdapter().getAddress();
		System.out.println(s);
		this.tv = (TextView) this.findViewById(R.id.TV_OUT);
		
		myAdr = BluetoothAdapter.getDefaultAdapter().getAddress();
		if(myAdr.equalsIgnoreCase(BT_ADDRESS_1)) {
			otherAdr = BT_ADDRESS_2;
		} else {
			otherAdr = BT_ADDRESS_1;
			myAdr = BT_ADDRESS_2;
		}
	}

	public void startAlice(View v) {
		engine = new AndroidSharkEngine();
		kb = new InMemoSharkKB();
		kb.addListener(this);

		PeerSemanticTag alice = kb.createPeerSemanticTag("Alice", "http://www.sharksystem.net/Alice.html", Protocols.BT_RFCOMM_PREFIX + myAdr);
		PeerSemanticTag bob = kb.createPeerSemanticTag("Bob", "http://www.sharksystem.net/Bob.html", Protocols.BT_RFCOMM_PREFIX + otherAdr);

		SemanticTag p2p = kb.createSemanticTag("p2p", "http://www.p2p.de");
		SemanticTag japan = kb.createSemanticTag("japan", "http://www.nippon.jp");

		ContextPoint cp = kb.createContextPoint(kb.createContextCoordinates(p2p, alice, bob, null, null, null, ContextSpace.OUT));
		cp.addInformation("P2P ist klasse!");
		println("cp1 added");

		ContextPoint cp2 = kb.createContextPoint(kb.createContextCoordinates(japan, alice, bob, null, null, null, ContextSpace.OUT));
		cp2.addInformation("Japan ist klasse!");
		println("cp2 added");

		Interest interest = kb.createInterest(kb.createContextCoordinates(p2p, alice, bob, null, null, null, ContextSpace.OUT));
		KnowledgePort kp = new KnowledgePort(engine, kb, interest);
		kp.start();
		kp.addListener(this);

		Interest interest2 = kb.createInterest(kb.createContextCoordinates(japan, alice, bob, null, null, null, ContextSpace.OUT));
		KnowledgePort kp2 = new KnowledgePort(engine, kb, interest2);
		kp2.start();
		kp2.addListener(this);

		engine.start();
	}

	public void startBob(View v) {
		engine = new AndroidSharkEngine();
		kb = new InMemoSharkKB();
		kb.addListener(this);

		PeerSemanticTag bob = kb.createPeerSemanticTag("Bob", "http://www.sharksystem.net/Bob.html", Protocols.BT_RFCOMM_PREFIX + myAdr);

		SemanticTag p2p = kb.createSemanticTag("p2p", "http://www.p2p.de");
		SemanticTag japan = kb.createSemanticTag("japan", "http://www.nippon.jp");

		Interest interest = kb.createInterest(kb.createContextCoordinates(p2p, bob, null, null, null, null, ContextSpace.IN));
		KnowledgePort kp = new KnowledgePort(engine, kb, interest);
		kp.start();
		kp.addListener(this);

		Interest interest2 = kb.createInterest(kb.createContextCoordinates(japan, bob, null, null, null, null, ContextSpace.IN));
		KnowledgePort kp2 = new KnowledgePort(engine, kb, interest2);
		kp2.start();
		kp2.addListener(this);

		engine.start();
	}

	public void startBTFinder(View v) {
		if(!isFinderRunning) {
			this.engine.startBTFinder(this);			
		} else {
			this.engine.stopBTFinder();
		}
		isFinderRunning = !isFinderRunning;
	}

	public void publish(View v) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				engine.publishAllKp();
			}
		}).start();
	}

	private void println(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String prefix = "(" + sdf.format(new Date(System.currentTimeMillis())) + ") ";

		final String msg = prefix + s;

		tv.post(new Runnable() {

			@Override
			public void run() {
				tv.append(msg + "\n");
			}
		});
	}
	
	
	
	/************************************************************************
	 *																		* 
	 * 			KnowledgeBaseListener Methoden für den Log Output.			*
	 * 																		*
	 ************************************************************************/

	@Override
	public void topicAdded(SemanticTag tag) {
		println("topic added: " + tag.getName());
		println("with SI: " + tag.getSI()[0]);
		println("");
	}

	@Override
	public void peerAdded(PeerSemanticTag tag) {
		println("peer added: " + tag.getName());
		println("with SI: " + tag.getSI()[0]);
		println("with adr: " + tag.getAddresses()[0]);
		println("");
	}

	@Override
	public void locationAdded(GeoSemanticTag location) {
		// TODO Auto-generated method stub
	}

	@Override
	public void timespanAdded(TimeSemanticTag time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void topicRemoved(SemanticTag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void peerRemoved(PeerSemanticTag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void locationRemoved(GeoSemanticTag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timespanRemoved(TimeSemanticTag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextPointAdded(ContextPoint cp) {
		println("created ContextPoint");
	}

	@Override
	public void cpChanged(ContextPoint cp) {
	}

	@Override
	public void contextPointRemoved(ContextPoint cp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void predicateCreated(AssociatedSemanticTag subject, String type, AssociatedSemanticTag object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void predicateRemoved(AssociatedSemanticTag subject, String type, AssociatedSemanticTag object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exposeSent(KnowledgePort kp, Interest sentMutualInterest) {
		println("expose sent");
		println("");
	}

	@Override
	public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
		println("insert sent");
		println("");
	}

	@Override
	public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
		Information info = (Information) newCP.enumInformation().nextElement();
		String value = new String(info.getContentAsByte());
		String topicname = newCP.getContextCoordinates().getTopic().getName();
		println("ContextPoint added!");
		println("Content: " + value);
		println("for Topic: " + topicname);
		println("");
		println("");
		println(" ---------- OK ----------");
		println("");
		println("");
	}
}