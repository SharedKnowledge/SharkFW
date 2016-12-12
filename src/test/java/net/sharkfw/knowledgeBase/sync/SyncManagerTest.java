package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by j4rvis on 19.09.16.
 */
public class SyncManagerTest {

    @Test
    public void groupInvitationTest(){

        L.setLogLevel(L.LOGLEVEL_ALL);

        // Basics
        J2SEAndroidSharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SyncManager aliceManager = aliceEngine.getSyncManager();
        aliceManager.allowInvitation(true);

        J2SEAndroidSharkEngine bobEngine = new J2SEAndroidSharkEngine();
        SyncManager bobManager = bobEngine.getSyncManager();
        bobManager.allowInvitation(true);

        // Create alice
        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice.de", "tcp://localhost:7070");
        aliceEngine.setEngineOwnerPeer(alice);

        // Create bob
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob.de", "tcp://localhost:7071");
        bobEngine.setEngineOwnerPeer(bob);
        try {
            bobEngine.startTCP(7071);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a kb to share
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        try {
            sharkKB.addInformation("This is just \"an example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
            sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
            sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
            sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
            sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        PeerSTSet peerSTSet = sharkKB.createInMemoPeerSTSet();
        try {
            peerSTSet.merge(bob);
//            peerSTSet.merge(alice);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

        SemanticTag kbName = sharkKB.createInMemoSemanticTag("kbName", "kbsi.de");

        // Now create the component

        SyncComponent component = aliceManager.createSyncComponent(sharkKB, kbName, peerSTSet, alice, true);
        try {
            aliceManager.sendInvite(component);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getComponentByName_success(){
        L.setLogLevel(L.LOGLEVEL_ALL);

        // Basics
        J2SEAndroidSharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SyncManager aliceManager = aliceEngine.getSyncManager();
        // Create alice
        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice.de", "tcp://localhost:7070");
        aliceEngine.setEngineOwnerPeer(alice);
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        PeerSTSet peerSTSet = sharkKB.createInMemoPeerSTSet();

        SemanticTag kbName = sharkKB.createInMemoSemanticTag("kbName", "kbsi.de");
        SemanticTag kbName1 = sharkKB.createInMemoSemanticTag("kbName1", "kbsi1.de");
        SemanticTag kbName2 = sharkKB.createInMemoSemanticTag("kbName2", "kbsi2.de");
        SemanticTag kbName3 = sharkKB.createInMemoSemanticTag("kbName3", "kbsi3.de");

        // Now create the component

        SyncComponent component = aliceManager.createSyncComponent(sharkKB, kbName, peerSTSet, alice, true);
        SyncComponent component1 = aliceManager.createSyncComponent(sharkKB, kbName1, peerSTSet, alice, true);
        SyncComponent component2 = aliceManager.createSyncComponent(sharkKB, kbName2, peerSTSet, alice, true);
        SyncComponent component3 = aliceManager.createSyncComponent(sharkKB, kbName3, peerSTSet, alice, true);

        SyncComponent componentByName = aliceManager.getComponentByName(kbName1);

        Assert.assertTrue(component1.getUniqueName().getName().equals(componentByName.getUniqueName().getName()));
    }
}
