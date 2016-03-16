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
import net.sharkfw.system.SharkSecurityException;

public class AliceActivity extends CommunicationActivity implements KPListener {
	
	/* Tests sending data via tcp
	 */
	@Override
	protected void testTcp() {
		startCommunicationTest("tcp://" + aliceIp + ":"
				+ J2SEAndroidSharkEngine.defaultTCPPort, "tcp://"
						+ bobIp + ":"
						+ J2SEAndroidSharkEngine.defaultTCPPort);
	}

	/* Tests sending data via e-mail
	 */
	@Override
	protected void testMail() {
		startCommunicationTest(MainActivity.ALICE_MAIL, MainActivity.BOB_MAIL);
	}

	@Override
	protected void testWifi() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void testCommunication(String aliceAddress, String bobAddress)
			throws SharkKBException, SharkProtocolNotSupportedException,
			IOException, SharkSecurityException {
		
		// Create a new knowledgebase for this peer
		SharkKB aliceKB = new InMemoSharkKB();

		// Create a peer to describe the topic "Shark"
		SemanticTag shark = aliceKB.createSemanticTag("Shark",
				"http://www.sharksystem.net/");

		// Create a peer to describe ourselves
		String[] aliceAddr = new String[1];
		aliceAddr[0] = aliceAddress;

		PeerSemanticTag alice = aliceKB.createPeerSemanticTag("Alice",
				"http://www.sharksystem.net/alice.html", aliceAddr);

		aliceKB.setOwner(alice);

		// Create new coordinates before creating a ContextPoint
		ContextCoordinates cc = aliceKB.createContextCoordinates(shark, /* originator */
				alice, /* peer */alice, /* remote peer */null, /* time */null, /* place */
				null, SharkCS.DIRECTION_OUT);

		// Create a ContextPoint to add information to
		ContextPoint cp = aliceKB.createContextPoint(cc);

		// Add a string to the ContextPoint at the given coordinates
		cp.addInformation("I like Shark");

		// Create a KnowledgePort to handle the interest
		StandardKP kp = new StandardKP(sharkEngine, cc, aliceKB);

		// keep connection 10 seconds open
		sharkEngine.setConnectionTimeOut(10000);

		kp.addListener(this);

		if (aliceAddress.contains("tcp://")) {
			sharkEngine.startTCP(J2SEAndroidSharkEngine.defaultTCPPort);
		} else if (aliceAddress.contains("mail://")) {
			String mail = aliceAddress.replace("mail://", "");
			sharkEngine.setMailConfiguration( MainActivity.ALICE_SMTP, 
					mail, MainActivity.ALICE_PWD, false, 
					 MainActivity.ALICE_POP3, mail, 
	                mail, MainActivity.ALICE_PWD, 1, false);
			sharkEngine.startMail();
			
		}
		// TODO: WIFI DIRECT
		//else if (...) {
		//	
		//}

		// Now the peer is passively waiting for incoming traffic

		// Create a new peer that will act as out partner for communications
		PeerSemanticTag bob = aliceKB.createPeerSemanticTag("Bob",
				"http://www.sharksystem.net/bob.html", bobAddress);

		// publish the KnowledgePort to our partner
		sharkEngine.publishKP(kp, bob);

		System.out.print("Bob has to run before starting Alice. If so, he "
				+ "has already received something from Alice");
	}
	
	
	@Override
	public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
		// ignore
	}

	@Override
	public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
		updateStatusMessage("Alice has sent something - enough for today..." + L.knowledge2String(sentKnowledge));
		sharkEngine.stop();
	}

	@Override
	public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
		// ignore
	}


}
