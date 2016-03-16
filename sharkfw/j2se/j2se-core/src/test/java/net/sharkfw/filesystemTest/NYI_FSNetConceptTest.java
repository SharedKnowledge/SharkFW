///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package filesystemTest;
//
//import java.util.Enumeration;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.internal.InternalAssociatedSTSet;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSTSet;
//import net.sharkfw.system.SharkNotSupportedException;
//
///**
// *
// * @author Jacob Zschunke
// */
//public class NYI_FSNetConceptTest {
//
//    InternalAssociatedSTSet sn;
//    AssociatedSemanticTag sushi;
//    AssociatedSemanticTag mangaka;
//    AssociatedSemanticTag kishiro;
//
//    public NYI_FSNetConceptTest() throws SharkKBException
//    {
//        this.sn = new FSAssociatedSTSet("Japan");
//        this.sushi = this.sn.createAssociatedSemanticTag("Sushi", new String[] {"Shake Maki", "Shake Nigiri", "Tekka Maki", "Tekka Nigiri"});
//        this.mangaka = this.sn.createAssociatedSemanticTag("Mangaka", new String[] {"www.yukitopia.com","www.wikipedia.de/YukitoKishiro"});
//        this.kishiro = this.sn.createAssociatedSemanticTag("Kishiro", new String[]  {"www.yukitopia.com","www.wikipedia.de/YukitoKishiro"});
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void getConceptsTest() throws SharkNotSupportedException
//    {
//        Enumeration enu = this.sn.tags();
//
//        System.out.printf("\n=============================\n");
//        while(enu.hasMoreElements())
//        {
//            AssociatedSemanticTag nc = (FSAssociatedSemanticTag) enu.nextElement();
//            System.out.printf(nc.getName() + "\n");
//        }
//
//        if(enu == null)
//        {
//            System.out.printf("nix im enu");
//        }
//
//        System.out.printf("=============================\n");
//    }
//
//    @Test
//    public void getAssociatedConceptsTest()
//    {
//
//    }
//
//}