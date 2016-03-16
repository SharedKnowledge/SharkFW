///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package filesystemTest;
//
//import java.util.Enumeration;
//import java.util.Vector;
//import junit.framework.Assert;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import net.sharkfw.knowledgeBase.internal.InternalAssociatedSTSet;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.HierarchicalSemanticTag;
//import net.sharkfw.knowledgeBase.legacy.fs.FSTaxonomy;
//import net.sharkfw.knowledgeBase.internal.InternalROSTSet;
//import net.sharkfw.system.SharkNotSupportedException;
//
///**
// * This file tests the FSTaxonomy implementations methods
// *
// * This is yet completely uncommented - we are on it :)
// *
// * TODO: Clean up this test! Any volunteers? ;)
// *
// * @deprecated
// * @author mfi
// */
//public class FilesystemTest {
//
//    FSTaxonomy fst,fst2;
//    String teststring = "Test";
//    String rootfolder = "taxonomy";
//    String si[] = {"http://de.wikipedia.org/wiki/test","www.nocheineURL.de","www.twitter.de","www.Bundestrojaner.de"};
//    String si_tv[] = {"fernseher"};
//    String si_konsole[] = {"konsole"};
//    String si_tiere[] = {"tiere"};
//    String si_futter[] = {"futter"};
//    String si_handy[] = {"handy"};
//    String si_vcr[] = {"vcr"};
//    String si_tv2[] = {"tv"};
//    String si_hundefutter[] = {"hundefutter"};
//    String si_chappi[] = {"chappi"};
//    String si_ps3[] = {"ps3"};
//    String si_wii[] = {"wii"};
//    String si_karnickel[] = {"karnickel"};
//    String si_hase[] = {"hase"};
//    String si_kaninchen[] = {"kaninchen"};
//    String si_hasenartig[] = {"hasenartige"};
//
//    public FilesystemTest() throws SharkKBException {
//        this.fst = new FSTaxonomy(rootfolder);
//        this.fst2 = new FSTaxonomy("MergeTax");
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
//    public void setUp() throws SharkKBException
//    {
//        HierarchicalSemanticTag tv = (HierarchicalSemanticTag) fst.createSemanticTag("Fernseher", si);
//
//        fst.createHierarchicalSemanticTag(tv,"VCR", si_vcr);
//        fst.createHierarchicalSemanticTag(tv, "TV", si_tv2);
//
//        HierarchicalSemanticTag futter = (HierarchicalSemanticTag) fst.createSemanticTag("Futter", si_futter);
//        HierarchicalSemanticTag futter_sub = fst.createHierarchicalSemanticTag(futter, "Hundefutter", si_hundefutter);
//        fst.createHierarchicalSemanticTag(futter_sub, "Chappi", si_chappi);
//
//        fst.createSemanticTag("Handy", si_handy);
//
//        HierarchicalSemanticTag konsole = (HierarchicalSemanticTag) fst.createSemanticTag("Konsole", si_konsole);
//        fst.createHierarchicalSemanticTag(konsole, "PS3", si_ps3);
//        fst.createHierarchicalSemanticTag(konsole, "Wii", si_wii);
//
//        HierarchicalSemanticTag tier = (HierarchicalSemanticTag) fst2.createSemanticTag("Tiere", si_tiere);
//
//        HierarchicalSemanticTag tc1 = fst2.createHierarchicalSemanticTag(tier,"Hasenartige", si_hasenartig);
//        HierarchicalSemanticTag tc3 = fst2.createHierarchicalSemanticTag(tc1,"Karnickel", si_karnickel);
//        HierarchicalSemanticTag tc2 = fst2.createHierarchicalSemanticTag(tc1, "Hase", si_hase);
//        tc1 = fst2.createHierarchicalSemanticTag(tc3, "Zwerg Kaninchen", si_kaninchen);
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void createConceptTest() throws SharkKBException{
//        fst.createSemanticTag(teststring, si);
//    }
//
//
//    @Test /*(expected = SharkNotSupportedException.class)*/
//    public void conceptsTest() throws SharkNotSupportedException{
//        fst.tags();
//    }
//
//    @Test
//    public void createConceptByObjectTest() throws SharkKBException{
//        fst.createHierarchicalSemanticTag(fst.getRootTag(), "Objekt", si);
//    }
//
//    @Test
//    public void fragmentTest() throws SharkKBException{
//       InternalAssociatedSTSet sn;
//       Vector siV = new Vector();
//       siV.add("http://de.wikipedia.org/wiki/test");
//
//       sn = fst.fragment(siV.elements(), 0);
//       Assert.assertNotNull(sn);
//    }
//
//    @Test
//    public void getAllSiTest(){
//    Enumeration enu = fst.getAllSI();
//
//    Assert.assertNotNull(enu);
//    }
//
//    @Test
//    public void getConceptTest() throws SharkKBException{
//        HierarchicalSemanticTag tc;
//        tc = fst.getSemanticTag(si[0]);
//    }
//
//    @Test
//    public void getConceptTestArray() throws SharkKBException{
//        HierarchicalSemanticTag tc;
//        tc = fst.getSemanticTag(si);
//    }
//
//    @Test
//    public void getConceptByIdTest() throws SharkKBException{
//        HierarchicalSemanticTag tc;
//        HierarchicalSemanticTag tc2;
//
//        tc = fst.getSemanticTag(si[0]);
//
//        tc2 = fst.getSemanticTagByID(tc.getID());
//
//        Assert.assertEquals(tc.getName(),tc2.getName());
//
//    }
//
//    @Test
//    public void getConceptByIdCreateTestTrue() throws SharkKBException{
//        HierarchicalSemanticTag tc2 = fst.getConceptByID("Hallo", true);
//    }
//
//    @Test public void getConecptByIDNameTest() throws SharkKBException
//    {
//        HierarchicalSemanticTag tc2;
//        String[] tmp;
//
//        tc2 = fst.getSemanticTagByID(teststring);
//        tmp = tc2.getSI();
//
//
//        Assert.assertEquals(tc2.getName(), teststring);
//        int i = 0;
//        while(i >= tmp.length)
//        {
//            Assert.assertEquals(tmp[i], si[i]);
//        }
//    }
//
//    @Test
//    public void getRootConceptTest() throws SharkKBException
//    {
//        HierarchicalSemanticTag tc_root = fst.getSemanticTagByID("/");
//        HierarchicalSemanticTag tc = fst.getRootTag();
//
//        Assert.assertEquals(tc_root.getName(), tc.getName());
//    }
//
//    @Test
//    public void mergeTest() throws SharkKBException
//    {
//        fst.merge((InternalROSTSet) fst2);
//    }
//
//    @Test
//    public void removeConceptTest() throws SharkKBException
//    {
//        fst.removeSemanticTag("Handy", si_handy);
//    }
//
//    /* This test moves a subtree from one point in the tree to another point */
//    @Test
//    public void moveFSTaxonomyConceptTest() throws SharkKBException{
//        HierarchicalSemanticTag destination = fst.getSemanticTagByID("Fernseher/TV");
//        HierarchicalSemanticTag tc = fst.getSemanticTagByID("Fernseher/VCR");
//
//        HierarchicalSemanticTag tc4 = fst.createHierarchicalSemanticTag(tc, "TVGeraet", si_tv);
//
//        tc4.move(destination);
//    }
//
////    /* This test removes a concept from the tree. SubConcepts of the concept in question will be moved up within the tree */
////    /* Already works, when the working directory is empty (no "taxonomy" folder). Otherwise it won't delete "Spielekonsole" :( */
////     @Test
////    public void removeFSTaxonomyConceptTest() throws SharkKBException{
////        fst.getSemanticTag("Konsole/Wii").remove(null);
////    }
////
////     /* see above, only that the whole subtree will be deleted */
////     @Test
////     public void removeRecursivelyTaxonomyConceptTest() throws SharkKBException{
////        fst.getSemanticTagByID("Futter/Hundefutter").removeRecursively(null);
////     }
//
//}
