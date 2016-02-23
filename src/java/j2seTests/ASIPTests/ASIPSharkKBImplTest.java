package ASIPTests;

import java.util.Iterator;
import junit.framework.Assert;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thsc
 */
public class ASIPSharkKBImplTest {
    
    public ASIPSharkKBImplTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void hello() throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        
        ASIPSpace asipSpace = kb.createASIPSpace(null, null, null, null, null, null, null);
        
        String testString = "test";
        kb.addInformation(testString, asipSpace);
        
        Iterator<ASIPInformation> informationIter = kb.getInformation(asipSpace);
        
        ASIPInformation info = informationIter.next();
        
        String contentAsString = info.getContentAsString();
        
        Assert.assertEquals(contentAsString, testString);
    
    }
}
