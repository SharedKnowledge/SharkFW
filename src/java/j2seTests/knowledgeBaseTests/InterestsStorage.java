package knowledgeBaseTests;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.AbstractSharkKB;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class InterestsStorage {
    
    public InterestsStorage() {
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

     @Test
     public void storeAndRestore() throws SharkKBException {
         L.setLogLevel(L.LOGLEVEL_ALL);
         STSet topics = InMemoSharkKB.createInMemoSTSet();
         topics.createSemanticTag("test", "http://test.de");

         SharkCS interest = InMemoSharkKB.createInMemoInterest(topics, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
         
         SharkKB is = new InMemoSharkKB();
         
         is.addInterest(interest);
         // add twice - only one should be added
         is.addInterest(interest);
         
        Iterator<SharkCS> interests = is.interests();
        SharkCS iBack = interests.next();
         
         Assert.assertTrue(SharkCSAlgebra.identical(interest, iBack));
         
         // there must not be another interest in the list due to duplicate supression
         Assert.assertFalse(interests.hasNext());
         
         // for developers eyes only:
         System.out.println(AbstractSharkKB.INTEREST_PROPERTY_NAME + ":" + is.getProperty(AbstractSharkKB.INTEREST_PROPERTY_NAME));
         
         // remove the only interest and try again
         is.removeInterest(interest);
         
         // for developers eyes only:
         System.out.println(AbstractSharkKB.INTEREST_PROPERTY_NAME + ":" + is.getProperty(AbstractSharkKB.INTEREST_PROPERTY_NAME));
         
         interests = is.interests();
         Assert.assertFalse(interests.hasNext());
     }
}
