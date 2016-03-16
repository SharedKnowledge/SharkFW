package net.sharkfw.apps.basicscenario;

import java.io.IOException;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import android.util.Log;

public class BobActivity extends CommunicationActivity implements KPListener {

	/* tests receiving data via tcp
	 */
	@Override
	protected void testTcp() {
		startCommunicationTest(null, "tcp://" + bobIp + ":"
				+ J2SEAndroidSharkEngine.defaultTCPPort);
	}

	/* tests receiving data via tcp
	 */
	@Override
	protected void testMail() {
		startCommunicationTest(null, "mail://douglas@sharksystem.net");
	}

	@Override
	protected void testWifi() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void testCommunication(String aliceAddress, String bobAddress) throws SharkKBException,
			IOException, SharkProtocolNotSupportedException {
		Log.d("BOB", bobAddress);
		// Create an in memory knowledgebase to store your information in
		SharkKB kb = new InMemoSharkKB();

		// Create a tap representing the subject "Java"
		SemanticTag shark = kb.createSemanticTag("Shark",
				"http://www.sharksystem.net/");

		// Create a peer to describe ourselves (Bob)
		String[] bobAddr = new String[1];
		bobAddr[0] = bobAddress;

		PeerSemanticTag bob = kb.createPeerSemanticTag("Bob",
				"http://www.sharksystem.net/bob.html", bobAddress);
		// bobSE.setOwner(bobPST);

		// Create new ContextCoordinates
		ContextCoordinates interest = kb.createContextCoordinates(shark, null,
				bob, null, null, null, SharkCS.DIRECTION_IN);

		// Activate a KnowledgePort using the interest to handle incoming events
		StandardKP kp = new StandardKP(sharkEngine, interest, kb);

		kp.addListener(this);

		sharkEngine.setConnectionTimeOut(10000);

		if (bobAddress.contains("tcp://")) {
			sharkEngine.startTCP(J2SEAndroidSharkEngine.defaultTCPPort);
		} else if (bobAddress.contains("mail://")) {
			String mail = bobAddress.replace("mail://", "");
			sharkEngine.setMailConfiguration(MainActivity.BOB_SMTP, 
					mail, MainActivity.BOB_PWD, false, 
					MainActivity.BOB_POP3, mail, 
	                mail, MainActivity.BOB_PWD, 1, false);
			sharkEngine.startMail();
		} 
		// TODO: WIFI DIRECT
		//else if (...) {
		//	
		//}

		System.out.println("Bob is up and running... Now start Alice.");

	}

	@Override
	public void exposeSent(KnowledgePort kp, final SharkCS sentMutualInterest) {
		updateStatusMessage("Sent interest as reply to incoming KEP message: "
				+ L.contextSpace2String(sentMutualInterest));
	}

	@Override
	public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
		updateStatusMessage("Sent knowledge: "
				+ L.knowledge2String(sentKnowledge));
	}

	@Override
	public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
		updateStatusMessage("Assimilated ContextPoint with coordinates\n: "
				+ L.contextSpace2String(newCP.getContextCoordinates()));
		sharkEngine.stop();
	}

}
