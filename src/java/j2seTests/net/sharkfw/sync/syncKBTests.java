package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;


// JUnit imports
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Veit Heller <veit@veitheller.de>
 * @author Simon Arnold <s0539710@htw-berlin.de>
 */
public class SyncKBTest{

	SharkKB _sharkKB = null;		

    public SyncKBTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
		_sharkKB = new InMemoSharkKB();
		SemanticTag teapotST = _sharkKB.createInMemoSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
		PeerSemanticTag alice = _sharkKB.createInMemoPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void createProfile() throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        GenericProfile profile = new GenericProfileImpl(kb);
        ContextCoordinates interest = kb.createContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        profile.addInterest(interest);
        profile.removeInterest(interest);
        byte input[] = {(byte) 5, (byte) 4};
        String key = "testKey";
        Iterator<Information> daten = null;
        profile.addInformation(key, input);
        daten = profile.getInformation(key);
        assertNotNull(daten);
        profile.removeInformation(key);
        PeerSemanticTag peer = kb.createPeerSemanticTag("dfdf", "dcxcfv", "dfdf");
        ArrayList<PeerSemanticTag> peerList = new ArrayList<>();
        peerList.add(peer);
        profile.setExposeStatusTrue(key, peerList);
        assertNotNull(profile.getAllowedPeers(key));
    }

    @Test
    public void GPKPTests() throws SharkKBException {
        SharkEngine aliceSE;
        aliceSE = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();
        ContextCoordinates interest = kb.createContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        SemanticTag testTag
                = InMemoSharkKB.createInMemoSemanticTag("TestTag", (String) "www.testtag.de");
        ContextCoordinates interest2 = kb.createContextCoordinates(testTag, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        GenericProfileImpl levioza = new GenericProfileImpl(kb);
        SharkEngine se;
        GenericProfileKP testKP = new GenericProfileKP(aliceSE, interest, kb, levioza);
        assertNotNull(testKP.getInterest());
        assertNotNull(testKP.getGenericProfile());

        KEPConnection response = null;
        testKP.doExpose(interest2, response);
        //levioza.addInterest(interest2);
        assertNotNull(levioza.getInterest(interest2));

    }

}
