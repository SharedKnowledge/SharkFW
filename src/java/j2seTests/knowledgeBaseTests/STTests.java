package knowledgeBaseTests;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thsc
 */
public class STTests {
    
    public STTests() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test pattern in STSet
     */
    @Test
    public void patternTest() throws SharkKBException {
        STSet set = InMemoSharkKB.createInMemoSTSet();
        
        // create several semantic tags
        /* note: SI must be defined - otherwise just the first
        st would be created - other creates would fail because
        they are semantically identical 
        */
        set.createSemanticTag("aabbcc", "http://aabbcc");
        set.createSemanticTag("aaabbcc", "http://aaabbcc");
        set.createSemanticTag("bbaacc", "http://bbaacc");
        set.createSemanticTag("abcc", "http://abcc");
        
        // define a pattern with no wild cards
        String pattern = "aabbcc";
        
        Iterator<SemanticTag> tagIter = set.getSemanticTagByName(pattern);
        // there is just a single match
        for(int i = 0; i < 1; i++) {
            tagIter.next();
        }
        
        // not a second match
        Assert.assertFalse(tagIter.hasNext());
        
        // pattern with wild cards
        pattern = ".*aa.*";
        
        tagIter = set.getSemanticTagByName(pattern);
        // should be three matching tags
        for(int i = 0; i < 3; i++) {
            SemanticTag next = tagIter.next();
            System.out.println(next.getName());
        }

        // shouldn't be more
        Assert.assertFalse(tagIter.hasNext());
    }
    
    
    
    
}
