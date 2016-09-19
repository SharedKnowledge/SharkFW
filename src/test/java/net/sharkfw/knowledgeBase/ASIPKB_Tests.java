package net.sharkfw.knowledgeBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.system.L;
import static net.sharkfw.knowledgeBase.SharkCS.DIRECTION_INOUT;
import org.junit.Assert;

/**
 *
 * @author thsc
 */
public class ASIPKB_Tests {
    
    public ASIPKB_Tests() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void twoInformations() throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        L.setLogLevel(L.LOGLEVEL_ALL);

        SemanticTag topic1 = InMemoSharkKB.createInMemoSemanticTag("Shark", "http://sharksystem.net/");
        SemanticTag topic2 = InMemoSharkKB.createInMemoSemanticTag("HTW", "http://www.htw-berlin.de/");

        PeerSemanticTag author1 = InMemoSharkKB.createInMemoPeerSemanticTag("Tim", "tim@mail.com", null);
        PeerSemanticTag author2 = InMemoSharkKB.createInMemoPeerSemanticTag("Harald", "harald@mail.com", null);

        ASIPSpace space1 = kb.createASIPSpace(topic1, null, author1, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space2 = kb.createASIPSpace(topic2, null, author2, null, null, null, null, DIRECTION_INOUT);
        
        kb.addInformation("Ein schoener Sharktext...", space1);

        // Ist folgender Code nicht auskommentiert, wird statt der gueltigen Ausgabe eine "Endlosschleife" produziert,
        // ich erhalte dann keine Ausgabe.
        kb.addInformation("HTW-Krimskrams", space2);
        L.d(L.kb2String(kb), this);
        
        Assert.assertEquals(2, kb.getNumberInformation());

    }
}
