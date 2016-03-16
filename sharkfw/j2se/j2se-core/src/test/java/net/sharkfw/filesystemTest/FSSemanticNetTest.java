///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package filesystemTest;
//
//import java.io.File;
//import java.util.Enumeration;
//import junit.framework.Assert;
//import net.sharkfw.knowledgeBase.internal.InternalAssociatedSTSet;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSTSet;
//import net.sharkfw.system.SharkNotSupportedException;
//
///**
// *
// * @deprecated
// * @author Jacob Zschunke
// */
//public class FSSemanticNetTest
//{
//    private FSAssociatedSTSet sn;
//    private FSAssociatedSTSet snMerge;
//
//    private String animeConcept = "Ani=me";
//    private String[] animeSI = {"http://www.animexx.de","http://www.wikipedia.de/anime"};
//    private AssociatedSemanticTag anime;
//
//    private String removeName = "Remove";
//    private String[] removeSI = {"http://www.remove.de","http://www.wikipedia.de/remove"};
//    private AssociatedSemanticTag remove;
//
//    private String mangakaConept = "Mangaka";
//    private String[] mangakaSI = {"http://www.lovehina.com","http://www.wikipedia.de/KenAkamatsu"};
//    private AssociatedSemanticTag mangaka;
//
//    private String mangakaName = "Yukito Kishiro";
//    private String[] kishiroSI = {"http://www.yukitopia.com","http://www.wikipedia.de/YukitoKishiro"};
//    private AssociatedSemanticTag kishiro;
//
//    private String mangaName = "Manga";
//    private String[] mangaSI = {"http://www.yukitopia.com/manga","http://www.wikipedia.de/Manga","http://www.Battle-Angel-Alita.de"};
//    private AssociatedSemanticTag manga;
//
//    private String fernseherConcept = "Fernseher";
//    private String[] tvSI = {"http://www.tv.tv","http://www.tv.de"};
//    private AssociatedSemanticTag fernseher;
//
//    private String[] sushiSI = {"http://Shake Maki.de", "http://Shake Nigiri.de", "http://Tekka Maki.de", "http://Tekka Nigiri.de"};
//    private AssociatedSemanticTag sushi;
//
//    public FSSemanticNetTest() throws SharkKBException
//    {
//        this.sn = new FSAssociatedSTSet("Nippon1");
//        this.sushi = this.sn.createAssociatedSemanticTag("Sushi", sushiSI);
//        this.snMerge = new FSAssociatedSTSet("GEZ-Geraete");
//        this.manga = this.sn.createAssociatedSemanticTag(mangaName, mangaSI);
//        this.kishiro = this.sn.createAssociatedSemanticTag(mangakaName, kishiroSI);
//        this.remove = this.sn.createAssociatedSemanticTag(removeName, removeSI);
//        this.fernseher = this.snMerge.createAssociatedSemanticTag(fernseherConcept, tvSI);
//        this.mangaka = this.sn.createAssociatedSemanticTag(mangakaConept, mangakaSI);
//        this.kishiro.setPredicate(InternalAssociatedSTSet.SUPERASSOC, mangaka);
//        this.mangaka.setPredicate(InternalAssociatedSTSet.SUBASSOC, kishiro);
//        this.mangaka.setPredicate(InternalAssociatedSTSet.SUPERASSOC, manga);
//        this.manga.setPredicate(InternalAssociatedSTSet.SUBASSOC, mangaka);
//        this.manga.setPredicate(InternalAssociatedSTSet.SUPERASSOC, sushi);
//        this.anime = this.sn.createAssociatedSemanticTag(animeConcept, animeSI);
//        mangaka = this.sn.getAssociatedSemanticTag(mangakaSI);
//        this.anime.setPredicate(InternalAssociatedSTSet.SUPERASSOC, mangaka);
//        this.mangaka.setPredicate(InternalAssociatedSTSet.SUBASSOC, anime);
//
//        //this.manga.setPredicate(AssociatedSTSet.SUPERASSOC, fernseher);
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception
//    {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception
//    {
//    }
//
//    @Before
//    public void setUp() throws SharkKBException
//    {
//
//    }
//
//    @After
//    public void tearDown()
//    {
//
//    }
//
//
//    //@Test
//    /*public void createConceptTest() throws SharkKBException
//    {
//        this.anime = this.sn.createAssociatedSemanticTag(animeConcept, animeSI);
//        this.anime.setPredicate(AssociatedSTSet.SUPERASSOC, mangaka);
//        this.mangaka.setPredicate(AssociatedSTSet.SUBASSOC, anime);
//
//        //File f = new File(this.sn.getName() + "/" + this.anime.getName() + "/" + FSAssociatedSemanticTag.PROPFILENAME);
//        //Assert.assertTrue(f.exists());
//    }*/
//
//    @Test
//    public void setPredicateTest() throws SharkKBException
//    {
//        this.sn.setPredicate(kishiro, mangaka, "01");
//        this.sn.setPredicate(manga, kishiro, "02");
//        this.sn.setPredicate(manga, mangaka, "03");
//        this.sn.setPredicate(manga, kishiro, "04");
//        Enumeration en = this.kishiro.getAssociatedTags("01");
//        AssociatedSemanticTag nc = (AssociatedSemanticTag) en.nextElement();
//
//        Assert.assertEquals(this.mangaka.getName(), nc.getName());
//    }
//
//    @Test
//    public void getConceptTest() throws SharkKBException
//    {
//        AssociatedSemanticTag nc = this.sn.getAssociatedSemanticTag(mangaSI[0]);
//
//        Assert.assertEquals(this.manga.getName(), nc.getName());
//    }
//
//    @Test
//    public void getConceptByIDTest() throws SharkKBException
//    {
//        AssociatedSemanticTag nc = this.sn.getAssociatedSemanticTagByID(mangaka.getName());
//        Assert.assertEquals(this.mangaka.getName(), nc.getName());
//    }
//
//    @Test
//    public void getAllSITest()
//    {
//        Enumeration enu = this.sn.getAllSI();
//        String[] si = (String[]) enu.nextElement();
//        System.out.printf("\n" + si[0] + "\n");
//
//        Assert.assertEquals(this.removeSI[0], si[0]);
//        //NetConcept nc = (NetConcept) enu.nextElement();
//    }
//
//    @Test
//    public void conceptsTest() throws SharkNotSupportedException
//    {
//        Enumeration enu = this.sn.tags();
//        AssociatedSemanticTag nc = null;
//
//        System.out.printf("\n=============================\n");
//        while(enu.hasMoreElements())
//        {
//            nc = (FSAssociatedSemanticTag) enu.nextElement();
//            System.out.printf(nc.getName() + "\n");
//        }
//
//        if(enu == null)
//        {
//            System.out.printf("nix im enu");
//        }
//        System.out.printf("=============================\n");
//
//        Assert.assertEquals(this.kishiro.getName(), nc.getName());
//
//
//    }
//
//    @Test
//    public void removeConceptTest()
//    {
//        this.snMerge.removeSemanticTag(this.fernseher.getName(), this.fernseher.getSI());
//
//        File f = new File(this.snMerge.getName() + "/" + this.fernseher.getName() + "/" + FSAssociatedSemanticTag.PROPFILENAME);
//        Assert.assertTrue(!f.exists());
//    }
//
//    @Test
//    public void removePredicateTest()
//    {
//        this.sn.setPredicate(manga, kishiro, "05");
//        this.sn.setPredicate(manga, kishiro, "06");
//        this.manga.removePredicate("05", kishiro);
//        Enumeration enu = this.manga.getAssociatedTags("05");
//        Assert.assertNull(enu);
//    }
//}
