package net.sharkfw.asiptests;

import java.util.Iterator;
import junit.framework.Assert;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class ASIPSharkKBImplTest {

    // replace / overwrite method to test other kb implementations
    public SharkKB getKB() {
        return new InMemoSharkKB();
    }
    
    public ASIPSharkKBImplTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void unconstrainedInformationInAndOut() throws SharkKBException {
        SharkKB kb = this.getKB();
        
        ASIPSpace asipSpace = kb.createASIPSpace(null, null, null, null, null, null, null);
        
        String testString = "test";
        kb.addInformation(testString, asipSpace);
        
        Iterator<ASIPInformation> informationIter = kb.getInformation(asipSpace);
        
        ASIPInformation info = informationIter.next();
        
        String contentAsString = info.getContentAsString();
        
        Assert.assertEquals(contentAsString, testString);
    
    }
    
    @Test
    public void RealLookingForInformation() throws SharkKBException {
        SharkKB kb = this.getKB();
        
        // space 1: "java and ps"
        STSet topics1 = InMemoSharkKB.createInMemoSTSet();
        topics1.createSemanticTag("Java", "http://sharksystem.net/java");
        topics1.createSemanticTag("Programming languages", "http://sharksystem.net/ps");
        
        ASIPSpace asipSpace1 = kb.createASIPSpace(topics1, null, null, null, null, null, null);
        String spaceString1 = "Java und Programming Languages";
        
        // space 2: "java and c#"
        STSet topics2 = InMemoSharkKB.createInMemoSTSet();
        topics2.createSemanticTag("Java", "http://sharksystem.net/java");
        topics2.createSemanticTag("C#", "http://sharksystem.net/csharp");
        
        ASIPSpace asipSpace2 = kb.createASIPSpace(topics1, null, null, null, null, null, null);
        String spaceString2 = "Java und C#";
        
        kb.addInformation(spaceString1, asipSpace1);
        kb.addInformation(spaceString2, asipSpace2);
        
        Iterator<ASIPInformation> informationIter = kb.getInformation(asipSpace1);
        
        // should find both
        ASIPInformation info = informationIter.next();
        info = informationIter.next();
        Assert.assertFalse(informationIter.hasNext());
    }
}
