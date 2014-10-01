/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.genericProfile;

import java.util.Iterator;
import net.sharkfw.genericProfile.GenericProfile;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author s0540042
 */
public class BasicGPKPTest {

    public BasicGPKPTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
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
        byte input[] = null;
        String key = "testKey";
        Iterator<Information> daten = null;
        profile.addInformation(key, input);
        daten = profile.getInformation(key);
        profile.removeInformation(key);
        Iterator<PeerSemanticTag> peers = null;
        profile.setExposeStatusTrue(key, peers);
    }

    @Test
    public void GPKPTests() throws SharkKBException {
        SharkEngine aliceSE;
        aliceSE = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();
        ContextCoordinates interest = kb.createContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        GenericProfileImpl levioza = new GenericProfileImpl(kb);
        SharkEngine se;
        
        GenericProfileKP testKP = new GenericProfileKP(aliceSE, interest, kb);
        assertNotNull(testKP.getInterest());
        
        
    }

}
