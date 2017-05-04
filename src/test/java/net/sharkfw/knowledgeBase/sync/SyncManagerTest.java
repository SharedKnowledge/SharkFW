package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.J2SESharkEngine;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by j4rvis on 19.09.16.
 */
public class SyncManagerTest {

    @Test
    public void sync_fullStack_success() throws SharkKBException, InterruptedException, IOException {

        L.setLogLevel(L.LOGLEVEL_WARNING);

        // Basics
        J2SESharkEngine aliceEngine = new J2SESharkEngine();
        SyncManager aliceManager = aliceEngine.getSyncManager();
        aliceManager.allowInvitation(true);

        J2SESharkEngine bobEngine = new J2SESharkEngine();
        SyncManager bobManager = bobEngine.getSyncManager();
        bobManager.allowInvitation(true);

        // Create alice
        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice.de", "tcp://localhost:7070");
        aliceEngine.setEngineOwnerPeer(alice);

        // Create bob
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob.de", "tcp://localhost:7072");
        bobEngine.setEngineOwnerPeer(bob);
        bobEngine.startTCP(7072);

        // Create a kb to share
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        sharkKB.addInformation("This is just \"an example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anothasder example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anothasfer easfasxample\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anotherasfasfa example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        SemanticTag kbName = sharkKB.createInMemoSemanticTag("kbName", "kbsi.de");

        // Now create the component

        SyncComponent component = aliceManager.createSyncComponent(sharkKB, kbName, bob, alice, true);
        aliceManager.doInvite(component);

        final CountDownLatch lock = new CountDownLatch(1);
        final SyncComponent[] bobComponent = new SyncComponent[1];

        bobManager.addSyncMergeListener(new SyncMergeKP.SyncMergeListener() {
            @Override
            public void onNewMerge(SyncComponent component, SharkKB changes) {
            bobComponent[0] = component;
            lock.countDown();
            }
        });

        lock.await(20, TimeUnit.SECONDS);

        Assert.assertNotNull(bobComponent[0]);
        Assert.assertEquals(component.getKb().getNumberInformation(), bobComponent[0].getKb().getNumberInformation());
        bobEngine.stopTCP();
    }

    @Test
    public void sync_with_reply_success() throws IOException, SharkKBException, InterruptedException {

        L.setLogLevel(L.LOGLEVEL_WARNING);

        // Basics
        J2SESharkEngine aliceEngine = new J2SESharkEngine();
        SyncManager aliceManager = aliceEngine.getSyncManager();
        aliceManager.allowInvitation(true);

        J2SESharkEngine bobEngine = new J2SESharkEngine();
        SyncManager bobManager = bobEngine.getSyncManager();
        bobManager.allowInvitation(true);

        // Create alice
        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice.de", "tcp://localhost:7070");
        aliceEngine.setEngineOwnerPeer(alice);

        // Create bob
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob.de", "tcp://localhost:7071");
        bobEngine.setEngineOwnerPeer(bob);
        bobEngine.startTCP(7071);
        aliceEngine.startTCP(7070);

        // Create a kb to share
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        sharkKB.addInformation("This is just \"an example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"another example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anothasder example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anothasfer easfasxample\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        sharkKB.addInformation("This is just \"anotherasfasfa example\"!!!", InMemoSharkKB.createInMemoASIPInterest());
        SemanticTag kbName = sharkKB.createInMemoSemanticTag("kbName", "kbsi.de");

        // Now create the component

        SyncComponent component = aliceManager.createSyncComponent(sharkKB, kbName, bob, alice, true);
        aliceManager.doInvite(component);


        final CountDownLatch bobLock = new CountDownLatch(1);
        final SyncComponent[] bobComponent = new SyncComponent[1];

        bobManager.addSyncMergeListener(new SyncMergeKP.SyncMergeListener() {
            @Override
            public void onNewMerge(SyncComponent component, SharkKB changes) {
                bobComponent[0] = component;
                bobLock.countDown();
            }
        });

        bobLock.await(20, TimeUnit.SECONDS);

        Assert.assertNotNull(bobComponent[0]);

        bobComponent[0].getKb().addInformation("absldkjgasöodghuadshglijasdhlkjashdlkjg ldsaghlkadsh kgas dkgh kadsh gadshkg ", InMemoSharkKB.createInMemoASIPInterest());
        bobManager.doSync(bobComponent[0], alice);

        final CountDownLatch aliceLock = new CountDownLatch(1);
        final SyncComponent[] aliceComponent = new SyncComponent[1];

        aliceManager.addSyncMergeListener(new SyncMergeKP.SyncMergeListener() {
            @Override
            public void onNewMerge(SyncComponent component, SharkKB changes) {
                aliceComponent[0] = component;
                aliceLock.countDown();
            }
        });

        aliceLock.await(20, TimeUnit.SECONDS);

        Assert.assertNotNull(aliceComponent[0]);
        Assert.assertEquals(bobComponent[0].getKb().getNumberInformation(), aliceComponent[0].getKb().getNumberInformation());

        aliceEngine.stopTCP();
        bobEngine.stopTCP();
    }

    @Test
    public void getComponentByName_success(){
        L.setLogLevel(L.LOGLEVEL_ALL);

        // Basics
        J2SESharkEngine aliceEngine = new J2SESharkEngine();
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
