/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.genericProfile;

import java.util.Iterator;
import net.sharkfw.genericProfile.GenericProfile;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
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
        GenericProfile profile = new GenericProfileImpl();
        
        SharkKB kb = new InMemoSharkKB();

        //ContextCoordinates interest = kb.createContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        SharkCS interest = null;
        profile.addInterest(interest);
        profile.removeInterest(interest);

        String key = "testKey";
        byte[] daten = null;
        profile.addInformation(key, daten);
        daten = profile.getInformation(key);
        profile.removeInformation(key);

        int ExposeStatus = 0;
        Iterator<PeerSemanticTag> peers = null;
        String[] keys = null;
        profile.setExposeStatus(keys, ExposeStatus, peers);

    }
    
    @Test
    public void GPKPTests() throws SharkKBException {
        
    }
    
}
