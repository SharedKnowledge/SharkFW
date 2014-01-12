package ApiRev1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkSecurityException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Frederik Steffen (s0528634)
 */
public class BUG_TaxonomyTest {

    private Logger _log = Logger.getLogger("ForumTaxonomyTest");

    @Test
    public void testTaxonomyCommunication() throws IOException, InterruptedException, SharkKBException, SharkSecurityException {

        /*---------------------- Alice ----------------------*/

        this._log.info("Create Alice... ");
        J2SEAndroidSharkEngine engineAlice = new J2SEAndroidSharkEngine();
        SharkKB kbAlice = new InMemoSharkKB();

        PeerSemanticTag peerAlice_alice = kbAlice.createPeerSemanticTag("Alice", "derForum://alice", "tcp://127.0.0.1:7070");
        PeerSemanticTag peerBob_alice = kbAlice.createPeerSemanticTag("Bob", "derForum://bob", "tcp://127.0.0.1:7171");

        this._log.info("[ ALICE ] creating Tags... ");
        Taxonomy topicsTX = kbAlice.getTopicsAsTaxonomy();
        TXSemanticTag rootTagAlice = topicsTX.createTXSemanticTag("Root", "http://root.derForum.de");
        TXSemanticTag categorieTagAlice = topicsTX.createTXSemanticTag("Fussball", "http://fifa.com");
        TXSemanticTag threadTagAlice = topicsTX.createTXSemanticTag("Wer wird deutscher Meister?", "http://bundesliga.de");
        TXSemanticTag postTagAlice = topicsTX.createTXSemanticTag("Post1", "http://post1.derForum.de");

        this._log.info("[ ALICE ] make hierarchy... ");
        categorieTagAlice.move(rootTagAlice);
        threadTagAlice.move(categorieTagAlice);
        postTagAlice.move(threadTagAlice);

        assertEquals(categorieTagAlice, postTagAlice.getSuperTag().getSuperTag());
        assertEquals(postTagAlice, ((TXSemanticTag) categorieTagAlice.subTags().nextElement()).subTags().nextElement());

        this._log.info("[ ALICE ] create ContextCoordinates for PostTag... ");
        ContextCoordinates aliceCC = kbAlice.createContextCoordinates(postTagAlice, peerAlice_alice, null, null, null, null, SharkCS.DIRECTION_INOUT);

        ContextPoint cpAlice = kbAlice.createContextPoint(aliceCC);
        cpAlice.addInformation("BVB");

        this._log.info("[ ALICE ] create interest... ");

        ContextCoordinates interestCCAlice = kbAlice.createContextCoordinates(categorieTagAlice, peerAlice_alice, null, null, null, null, SharkCS.DIRECTION_INOUT);

        Interest interestAlice = kbAlice.createInterest(interestCCAlice);

        FragmentationParameter[] defaultFpAlice = kbAlice.getStandardFPSet();
        FragmentationParameter fpAlice = new FragmentationParameter(false, true, 3);
        defaultFpAlice[SharkCS.DIM_TOPIC] = fpAlice;

        StandardKP kpAlice = engineAlice.createKP(interestAlice, kbAlice);
        kpAlice.setFP(defaultFpAlice);

        this._log.info("[ ALICE ] start engine on Port 7070... ");
        engineAlice.startTCP(7070);

        /*---------------------- Bob ----------------------*/

        this._log.info("Create Bob... ");
        J2SEAndroidSharkEngine engineBob = new J2SEAndroidSharkEngine();
        SharkKB kbBob = new InMemoSharkKB();

        PeerSemanticTag peerAlice_bob = kbBob.createPeerSemanticTag("Alice", "derForum://alice", "tcp://127.0.0.1:7070");
        PeerSemanticTag peerBob_bob = kbBob.createPeerSemanticTag("Bob", "derForum://bob", "tcp://127.0.0.1:7171");

        this._log.info("[ BOB ] creating Tags... ");
        Taxonomy bobTopicsTX = kbBob.getTopicsAsTaxonomy();
        TXSemanticTag rootTagBob = bobTopicsTX.createTXSemanticTag("Root", "http://root.derForum.de");
        TXSemanticTag categorieTagBob = bobTopicsTX.createTXSemanticTag("Fussball", "http://fifa.com");

        this._log.info("[ BOB ] make hierarchy... ");
        categorieTagBob.move(rootTagBob);

        int tagCount = this.getEnumCount(kbBob.getTopicSTSet().tags());

        this._log.info("[ BOB ] create interest... ");

        ContextCoordinates interestCCBob = kbBob.createContextCoordinates(categorieTagBob, peerBob_bob, null, null, null, null, SharkCS.DIRECTION_INOUT);

        Interest interestBob = kbBob.createInterest(interestCCBob);

        FragmentationParameter[] defaultFpBob = kbBob.getStandardFPSet();
        FragmentationParameter fpBob = new FragmentationParameter(false, true, 3);
        defaultFpBob[SharkCS.DIM_TOPIC] = fpBob;

        StandardKP kpBob = engineBob.createKP(interestBob, kbBob);
        kpBob.setFP(defaultFpBob);

        this._log.info("[ BOB ] start engine on Port 7171... ");

        engineBob.startTCP(7171);

        Thread.sleep(1000);

        this._log.info("[Alice] publishing KP..");
        // engineAlice.publishKP( kpAlice, peerBob_alice );
        engineBob.publishKP(kpBob, peerAlice_bob);

        Thread.sleep(5000);

        /*---------------------- Auswertung ----------------------*/

        this._log.info("Check results... ");

        // TODO: asserts anpassen

        assertEquals(1, this.getEnumCount(categorieTagBob.subTags()));

        TXSemanticTag threadTagBob = (TXSemanticTag) categorieTagBob.subTags().nextElement();
        assertEquals(1, this.getEnumCount(threadTagBob.subTags()));

        TXSemanticTag postTagBob = (TXSemanticTag) threadTagBob.subTags().nextElement();
        assertTrue(postTagBob.subTags() == null);

        assertEquals(threadTagAlice.getSI()[0], threadTagBob.getSI()[0]);
        assertEquals(postTagAlice.getSI()[0], postTagBob.getSI()[0]);

        assertEquals(tagCount + 2, this.getEnumCount(kbBob.getTopicSTSet().tags()));

        ContextCoordinates resultCCBob = kbBob.createContextCoordinates(postTagBob, peerAlice_bob, null, null, null, null, SharkCS.DIRECTION_INOUT);

        Enumeration<ContextPoint> allCpBob = kbBob.getContextPoints(resultCCBob);

        ContextCoordinates resultCCAlice = kbBob.createContextCoordinates(postTagAlice, null, null, null, null, null, SharkCS.DIRECTION_INOUT);

        Enumeration<ContextPoint> allCpAlice = kbAlice.getContextPoints(resultCCAlice);

        assertNotNull(allCpBob);
        assertNotNull(allCpAlice);

        assertEquals(1, getEnumCount(allCpBob));
        assertEquals(1, getEnumCount(allCpAlice));

        allCpBob = kbBob.getContextPoints(resultCCBob);
        ContextPoint resultCPBob = allCpBob.nextElement();

        allCpAlice = kbAlice.getContextPoints(resultCCAlice);

        assertEquals(1, allCpAlice.nextElement().getNumberInformation());
        assertEquals(1, resultCPBob.getNumberInformation());

        Information newInfoBob = (Information) resultCPBob.enumInformation().nextElement();
        byte[] infoByteBob = newInfoBob.getContentAsByte();
        String infoStringBob = new String(infoByteBob);

        assertEquals("BVB", infoStringBob);

        TXSemanticTag resultTagBob = (TXSemanticTag) resultCPBob.getContextCoordinates().getTopic();

        assertEquals(postTagBob.getSI()[0], resultTagBob.getSI()[0]);

        printAllInformations(kbBob, resultCCBob, "Bob");
        printAllInformations(kbAlice, resultCCAlice, "Alice");

    }

    private void printAllInformations(SharkKB kb, ContextCoordinates cc, String owner) throws SharkKBException {

        Enumeration<ContextPoint> allCps = kb.getContextPoints(cc);
        Enumeration<Information> infos = null;
        String content = null;

        assertTrue(allCps.hasMoreElements());
        this._log.info("[ RESULT ] print all information contents for " + owner);

        while (allCps.hasMoreElements()) {
            ContextPoint currentCp = allCps.nextElement();
            infos = currentCp.enumInformation();
            assertNotNull(infos);

            this._log.info("[ RESULT ] " + owner + " ContextPoint: " + currentCp);

            while (infos.hasMoreElements()) {
                Information info = infos.nextElement();
                assertNotNull(info);

                byte[] byteContent = info.getContentAsByte();
                content = new String(byteContent);

                this._log.info("[ RESULT ] " + owner + " Content : " + content);
            }
        }

        assertNotNull(content);
    }

    private int getEnumCount(Enumeration e) {
        int count = 0;
        while (e != null && e.hasMoreElements()) {
            e.nextElement();
            count++;
        }
        return count;
    }
}
