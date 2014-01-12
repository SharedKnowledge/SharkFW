package ontology;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import org.junit.*;

/**
 *
 * @author Matthias
 */
public class InMemoAssociatedSTSetTest {

    public InMemoAssociatedSTSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
    // @Test
    // public void hello() {}

    /**
     * This method tests the fragmentation a <code>SemanticNet</code> to find out
     * if all concepts and associations are present in the result.
     * 
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Test
    public void testFragment() throws SharkKBException {
        SemanticNet sn = new InMemoSemanticNet();

        SNSemanticTag nc11 = sn.createSemanticTag("A", new String[] {"http://a.de"});
        SNSemanticTag nc12 = sn.createSemanticTag("B", new String[] {"http://b.de"});
        SNSemanticTag nc13 = sn.createSemanticTag("C", new String[] {"http://c.de"});
        SNSemanticTag nc14 = sn.createSemanticTag("D", new String[] {"http://d.de"});
        SNSemanticTag nc15 = sn.createSemanticTag("E", new String[] {"http://e.de"});

        nc11.setPredicate("connect", nc12);
        nc12.setPredicate("connect", nc13);
        nc13.setPredicate("connect", nc14);
        nc11.setPredicate("connect", nc15);

        FragmentationParameter fp = new FragmentationParameter(2);

        SemanticNet fragment = sn.fragment(nc11, fp);

        // check if all concepts are present
        SNSemanticTag resN1 = fragment.getSemanticTag("http://a.de");
        Assert.assertNotNull(resN1);

        SNSemanticTag resN2 = fragment.getSemanticTag("http://b.de");
        Assert.assertNotNull(resN2);

        SNSemanticTag resN3 = fragment.getSemanticTag("http://c.de");
        Assert.assertNotNull(resN3);

        SNSemanticTag resN4 = fragment.getSemanticTag("http://e.de");
        Assert.assertNotNull(resN4);

        // check if the associations were preserved
        // Is A connected to B and E?
        boolean b = false, e = false;
        Enumeration<SNSemanticTag> en1 = resN1.targetTags("connect");
        while(en1.hasMoreElements()) {
            SNSemanticTag next = en1.nextElement();
            if(next.equals(resN2)) {
                b = true; // connection to B is present
            } else if(next.equals(resN4)) {
                e = true; // connection to E is present
            }
        }
        // Assume that both connections have been found and traversed
        Assert.assertTrue(b && e);

        // Is B connected to C?
        Enumeration<SNSemanticTag>  en2 = resN2.targetTags("connect");
        SNSemanticTag linked2 = (SNSemanticTag) en2.nextElement();
        Assert.assertEquals(linked2, resN3);
    }

    /**
     * This method tests if the complete STSet is returned, when passing on an anchor
     * with the ANY-URL.
     *
     * FIXME: As of now a reference to the original STSet is returned. This is bad but necessary atm because there is no "copy" function yet.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Test
    public void testANYConcept() throws SharkKBException {
        SemanticNet sn = new InMemoSemanticNet();

        SNSemanticTag nc11 = sn.createSemanticTag("A", new String[] {"http://a.de"});
        SNSemanticTag nc12 = sn.createSemanticTag("B", new String[] {"http://b.de"});
        SNSemanticTag nc13 = sn.createSemanticTag("C", new String[] {"http://c.de"});
        SNSemanticTag nc14 = sn.createSemanticTag("D", new String[] {"http://d.de"});
        SNSemanticTag nc15 = sn.createSemanticTag("E", new String[] {"http://e.de"});

        nc11.setPredicate("connect", nc12);
        nc12.setPredicate("connect", nc13);
        nc13.setPredicate("connect", nc14);
        nc11.setPredicate("connect", nc15);

        FragmentationParameter fp = new FragmentationParameter(1);

        SemanticNet fragment = sn.fragment(SharkCSAlgebra.createAnyTag(), fp);
        //The following is not necesseraily true in the future once a copy function is in place.
        //Assert.assertEquals(fragment, sn);

        SNSemanticTag nc1 = fragment.getSemanticTag("http://a.de");
        Assert.assertNotNull(nc1);

        SNSemanticTag nc2 = fragment.getSemanticTag("http://b.de");
        Assert.assertNotNull(nc2);

        SNSemanticTag nc3 = fragment.getSemanticTag("http://c.de");
        Assert.assertNotNull(nc3);

        SNSemanticTag nc4 = fragment.getSemanticTag("http://d.de");
        Assert.assertNotNull(nc4);

        SNSemanticTag nc5 = fragment.getSemanticTag("http://e.de");
        Assert.assertNotNull(nc5);

        boolean b = false, e = false;
        Enumeration en1 = nc1.targetTags("connect");
        while(en1.hasMoreElements()) {
            SNSemanticTag next = (SNSemanticTag) en1.nextElement();
            if(next.equals(nc2)) {
                b = true; // connection to B is present
            } else if(next.equals(nc5)) {
                e = true; // connection to E is present
            }
        }
        // Assume that both connections have been found and traversed
        Assert.assertTrue(b && e);

        // Is B connected to C?
        Enumeration en2 = nc2.targetTags("connect");
        SNSemanticTag linked2 = (SNSemanticTag) en2.nextElement();
        Assert.assertEquals(linked2, nc3);

        // Is C connected to D?
        Enumeration en3 = nc3.targetTags("connect");
        SNSemanticTag linked3 = (SNSemanticTag) en3.nextElement();
        Assert.assertEquals(linked3, nc4);
    }
}