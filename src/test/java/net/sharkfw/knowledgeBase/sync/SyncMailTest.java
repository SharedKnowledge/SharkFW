package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.J2SESharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by j4rvis on 5/4/17.
 */
public class SyncMailTest {

    @Test
    public void testSyncViaMail_success() throws IOException, SharkKBException, InterruptedException {

        String mail_pop3 = "pop3.sharksystem.net";
        String mail_smtp = "smtp.sharksystem.net";

        String aliceName = "Alice";
        String aliceMail = "alice@sharksystem.net";
        String aliceMailUsername = "1658940";
        String aliceMailPassword = "alice";
        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag(aliceName, aliceMailUsername, Protocols.MAIL_PREFIX + aliceMail);

        String bobName = "Bob";
        String bobMail = "bob@sharksystem.net";
        String bobMailUsername = "1672016";
        String bobMailPassword = "bobbob";
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag(bobName, bobMailUsername, Protocols.MAIL_PREFIX + bobMail);

        L.setLogLevel(L.LOGLEVEL_WARNING);

        J2SESharkEngine aliceEngine = new J2SESharkEngine(new InMemoSharkKB());
        aliceEngine.setEngineOwnerPeer(alice);
        aliceEngine.setMailConfiguration(mail_smtp, aliceMailUsername, aliceMailPassword, false, mail_pop3, aliceMailUsername, aliceMail, aliceMailPassword, 1, false, 1024 * 10);
        J2SESharkEngine bobEngine = new J2SESharkEngine(new InMemoSharkKB());
        bobEngine.setEngineOwnerPeer(bob);
        bobEngine.setMailConfiguration(mail_smtp, bobMailUsername, bobMailPassword, false, mail_pop3, bobMailUsername, bobMail, bobMailPassword, 1, false, 1024 * 10);

        aliceEngine.startMail();
        bobEngine.startMail();

        aliceEngine.getSyncManager().allowInvitation(true);
        bobEngine.getSyncManager().allowInvitation(true);

        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        inMemoSharkKB.addInformation("This is a new Info", InMemoSharkKB.createInMemoASIPInterest());

        SemanticTag componentName = InMemoSharkKB.createInMemoSemanticTag("chat", "alicebob");

        aliceEngine.getSyncManager().createSyncComponent(inMemoSharkKB, componentName, bob, alice, true);

        aliceEngine.getSyncManager().doInviteOrSync(bob);

        final CountDownLatch lock = new CountDownLatch(1);
        final SyncComponent[] bobComponent = new SyncComponent[1];

        bobEngine.getSyncManager().addSyncMergeListener(new SyncMergeKP.SyncMergeListener() {
            @Override
            public void onNewMerge(SyncComponent component, SharkKB changes) {
                bobComponent[0] = component;
                lock.countDown();
            }
        });

        lock.await(20, TimeUnit.SECONDS);

        Assert.assertNotNull(bobComponent[0]);

        Iterator<ASIPInformation> information = bobComponent[0].getKb().getInformation(InMemoSharkKB.createInMemoASIPInterest());
        if(information.hasNext()){
            ASIPInformation next = information.next();
            Assert.assertEquals(next.getContentAsString(), "This is a new Info");
        }

        aliceEngine.stopMail();
        bobEngine.startMail();
    }
}
