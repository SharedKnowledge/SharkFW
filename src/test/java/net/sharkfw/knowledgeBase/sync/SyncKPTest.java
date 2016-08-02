package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class SyncKPTest {
    
    public SyncKPTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void syncKPTest() throws SharkKBException {
         SharkEngine se = new J2SEAndroidSharkEngine();
         SharkKB kb = new InMemoSharkKB();
         SyncKB synckb = new SyncKB(kb);
         
         SemanticTag kbTitel = InMemoSharkKB.createInMemoSemanticTag("syncKB", "http://aUniqueSI");
         SyncKP kp = new SyncKP(se, synckb, kbTitel);

         L.setLogLevel(L.LOGLEVEL_ALL);
     }
}
