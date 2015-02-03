/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledgeBaseTests;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.InterestStorage;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
         STSet topics = InMemoSharkKB.createInMemoSTSet();
         topics.createSemanticTag("test", "http://test.de");

         SharkCS interest = InMemoSharkKB.createInMemoInterest(topics, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
         
         InterestStorage is = new InMemoSharkKB();
         
         is.addInterest(interest);
         // add twice - only one should be added
         is.addInterest(interest);
         
        Iterator<SharkCS> interests = is.interests();
        SharkCS iBack = interests.next();
         
         Assert.assertTrue(SharkCSAlgebra.identical(interest, iBack));
         
         // there must not be another interest in the list due to duplicate supression
         Assert.assertFalse(interests.hasNext());
     }
}
