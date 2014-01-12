//package filesystemTest;
//
//import AlphaTests.TestUtil;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.util.Enumeration;
//import java.util.Hashtable;
//import net.sharkfw.kep.SharkProtocolNotSupportedException;
//import net.sharkfw.kep.format.XMLSerializer;
//import net.sharkfw.knowledgeBase.AnchorSet;
//import net.sharkfw.knowledgeBase.ContextCoordinates;
//import net.sharkfw.knowledgeBase.ContextPoint;
//import net.sharkfw.knowledgeBase.ContextSpace;
//import net.sharkfw.knowledgeBase.ExposedInterest;
//import net.sharkfw.knowledgeBase.FragmentationParameter;
//import net.sharkfw.knowledgeBase.InMemoExposedInterest;
//import net.sharkfw.knowledgeBase.Information;
//import net.sharkfw.knowledgeBase.Knowledge;
//import net.sharkfw.knowledgeBase.LocalInterest;
//import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.STSet;
//import net.sharkfw.knowledgeBase.SemanticTag;
//import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
//import net.sharkfw.knowledgeBase.ROSTSet;
//import net.sharkfw.knowledgeBase.ROSemanticTag;
//import net.sharkfw.knowledgeBase.ROPeerSemanticTag;
//import net.sharkfw.knowledgeBase.AssociatedSTSet;
//import net.sharkfw.knowledgeBase.GeoSTSet;
//import net.sharkfw.knowledgeBase.GeoSemanticTag;
//import net.sharkfw.knowledgeBase.PeerSTSet;
//import net.sharkfw.knowledgeBase.PeerSemanticTag;
//import net.sharkfw.knowledgeBase.SharkDuplicateException;
//import net.sharkfw.knowledgeBase.SharkKB;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.fs.FSAssociatedSTSet;
//import net.sharkfw.knowledgeBase.fs.FSPeerAssociatedSTSet;
//import net.sharkfw.knowledgeBase.fs.FSSharkKB;
//import net.sharkfw.knowledgeBase.time.InMemoTimeSTSet;
//import net.sharkfw.knowledgeBase.time.InMemoTimeSemanticTag;
//import net.sharkfw.peer.KnowledgePort;
//import net.sharkfw.platform.J2SESharkEngine;
//import net.sharkfw.system.SharkNotSupportedException;
//import net.sharkfw.wrapper.Vector;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// * Sehr gutmütige Tests, die zeigen sollen, dass die 2DimFSKB prinzipiell benutzbar ist.
// * @author mfi
// */
//public class FSSharkKBTest {
//
//    SharkKB kb;
//
//    public FSSharkKBTest() {
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
//        File dir1 = new File("TestKbOwner");
//        deleteDir(dir1);
//        dir1 = new File("filledKB");
//        deleteDir(dir1);
//        dir1 = new File("Testinfo");
//        deleteDir(dir1);
//        dir1 = new File("remoteContent");
//        deleteDir(dir1);
//        //SharkEngineTest
//        dir1 = new File("SharkEngineTest");
//        deleteDir(dir1);
//        dir1 = new File("SharkEngineTest2");
//        deleteDir(dir1);
//        dir1 = new File("Content");
//        deleteDir(dir1);
//        dir1 = new File("retrieval");
//        deleteDir(dir1);
//        kb = new FSSharkKB("TestKbOwner");
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    private boolean deleteDir(File path) {
//        if (path.exists()) {
//            File[] fileList = path.listFiles();
//            for (int i = 0; i < fileList.length; i++) {
//                if (fileList[i].isDirectory()) {
//                    if (!deleteDir(fileList[i])) {
//                        return false;
//                    }
//                } else {
//                    if (!fileList[i].delete()) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return (path.delete());
//    }
//
//    @Test
//    public void getSTSetTest() throws SharkKBException {
//        ROSTSet stset = kb.getSTSet(ContextSpace.DIM_TOPIC);
//        System.out.println("FSSharkKBTest.getSTSetTest: " + stset.toString());
//        Assert.assertNotNull(stset);
//    }
//
//    @Test
//    public void getSTSetAndAddTagTest() throws SharkKBException {
//        ROSTSet stset = kb.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSemanticTag nc = null;
//
//        if (stset instanceof FSAssociatedSTSet) {
//            FSAssociatedSTSet sn = (FSAssociatedSTSet) stset;
//            String name = "Themenkonzept";
//            String sis[] = {"http://themenkonzept.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis);
//
//        }
//
//        SemanticTag oc = stset.getSemanticTag("http://themenkonzept.de");
//        System.out.println("FSSharkKBTest.getSTSetAndAddConceptTest: Added:" + nc.getName() + " - " + oc.getName());
//        Assert.assertEquals(nc.getName(), oc.getName());
//    }
//
//    @Test
//    public void createAndRemoveContextPoint() throws SharkKBException {
//        ROSTSet stset = kb.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSemanticTag nc = null;
//
//        // Thema erzeugen
//        if (stset instanceof FSAssociatedSTSet) {
//            FSAssociatedSTSet sn = (FSAssociatedSTSet) stset;
//            String name = "Themenkonzept";
//            String sis[] = {"http://themenkonzept.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis);
//
//        }
//
//        // Peer erzeugen
//        ROSTSet peerStSet = kb.getSTSet(ContextSpace.DIM_ORIGINATOR);
//        AssociatedSemanticTag ncPeer = null;
//
//        if (peerStSet instanceof FSAssociatedSTSet) {
//            FSAssociatedSTSet sn = (FSAssociatedSTSet) peerStSet;
//            String name = "Peerkonzept";
//            String sis[] = {"http://peerkonzept.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis);
//
//        }
//
//        // Versuche Koordinaten zu erzeugen
//        ContextCoordinates co = new ContextCoordinates();
//        String topicSis[] = {"http://themenkonzept.de"};
//        String peerSis[] = {"http://peerkonzept.de"};
//
//        co.setSI(ContextSpace.DIM_TOPIC, topicSis);
//        co.setSI(ContextSpace.DIM_ORIGINATOR, peerSis);
//
//        String names[] = new String[ContextSpace.MAXDIMENSIONS];
//        names[ContextSpace.DIM_TOPIC] = "themenkonzept";
//        names[ContextSpace.DIM_ORIGINATOR] = "peerkonzept";
//
//        ContextPoint cp = kb.createContextPoint(co, names);
//
//        System.out.println("FSSharkKBTest.createContextPoint " + cp.getCoordinates().toString());
//
//        String info = "Testinfo";
//        byte[] infoArray = info.getBytes();
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(infoArray);
//        cp.addInformation("Test", bais, infoArray.length);
//        System.out.println("FSSharkKBTest.createContextPoint: Adding information: " + info);
//
//        Enumeration information = cp.getInformation();
//
//        while (information.hasMoreElements()) {
//            Information i = (Information) information.nextElement();
//            byte[] result = i.getContentAsByte();
//            Assert.assertNotNull(result);
//
//            String resultString = new String(result);
//            System.out.println("FSSharkKBTest.createContextPoint: Reading information: " + resultString);
//            Assert.assertEquals(resultString, info);
//        }
//
//        Assert.assertNotNull(cp);
//
//        // try to remove the cp
//        ContextCoordinates co2 = cp.getCoordinates();
//        kb.removeContextPoint(co2);
//
//        try {
//            ContextPoint cp2 = kb.getContextPoint(co2);
//            Assert.assertTrue(false);
//        } catch (SharkKBException ex) {
//            Assert.assertTrue(true);
//        }
//
//
//    }
//
//    @Test
//    public void testSaveAndRemoveInterest() throws Exception {
//        InMemoExposedInterest ei = new InMemoExposedInterest();
//        kb.saveLocalInterest("test", ei);
//
//        ExposedInterest ei2 = kb.getExposedInterestByName("TestKbOwner", "test");
//        Assert.assertNotNull(ei2);
//
//        kb.removeInterest("TestKbOwner", "test");
//        ExposedInterest ei3 = kb.getExposedInterestByName("TestKbOwner", "test");
//
//        Assert.assertNull(ei3);
//    }
//
//    @Test
//    public void testSetProperty() throws Exception {
//        kb.setProperty("test", "testvalue");
//        String value = kb.getProperty("test");
//
//        Assert.assertEquals(value, "testvalue");
//    }
//
//    /**
//     * Teste einfache Assimilation von zuvor extrahiertem Wissen aus fast gleicher KB
//     * mit effektivem Interesse = Hintergrundwissen
//     *
//     * @throws java.lang.Exception
//     */
//    @Test
//    public void testAssimilate() throws Exception {
//        System.out.println("Test 1 f. Assimilation");
//        // Mache etwas zum extrahieren ...
//        FSSharkKB imkb = this.createFilledKB("Testinfo");
//        AnchorSet as = new AnchorSet();
//        as.addAnchor("http://themenkonzept.de", ContextSpace.DIM_TOPIC);
//        as.addAnchor("http://peerkonzept.de", ContextSpace.DIM_ORIGINATOR);
//        // init FPs
//        FragmentationParameter fp = new FragmentationParameter(true, true, 1);
//        FragmentationParameter fps[] = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];
//
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            fps[i] = fp;
//        }
//
//        LocalInterest imi = imkb.createInterest(as, fps);
//
//        Knowledge k = imkb.extract(imi, fp);
//
//        // Wissen extrahiert!
//
//        // Jetzt versuchen das extrahierte Wissen in eine andere WB einzusetzen
//
//        FSSharkKB remote = this.createFilledKB("remoteContent");
//        // Sonderfall: fps = 0, Hintergrundwissen = Effektives Interesse = Ausgangsinteresse
//        // Soll nur testen ob der Code logisch i.O. ist.
//        remote.assimilate(k, fps, imi);
//
//        Knowledge remoteK = remote.extract(imi, fp);
//        System.out.println("In remoteKnowledge sind: " + remoteK.getNumberContextPoints() + " Kontextpunkte");
//        ContextPoint remoteCP = remoteK.getCP(0);
//        Assert.assertNotNull(remoteCP);
//        Assert.assertEquals(2, remoteCP.getNumberInformation());
//        Enumeration infos = remoteCP.getInformation();
//        Information info = (Information) infos.nextElement();
//        String remoteContent = new String(info.getContentAsByte());
//        System.out.println("In Information 1 von Kontextpunkt ist content: " + remoteContent);
//        Assert.assertEquals("remoteContent", remoteContent); // der war schon vorhanden
//
//        Information info2 = (Information) infos.nextElement();
//        String remoteContent2 = new String(info2.getContentAsByte());
//        System.out.println("In Information 2 von Kontextpunkt ist content: " + remoteContent2);
//        Assert.assertEquals("Testinfo", remoteContent2); // der ist neu
//    }
//
//    /**
//     * Gutwilliger Extraktionstest der einen Kontextpunkt samt Hintergrundwissen und Information herausloest
//     * @throws java.lang.Exception
//     */
//    @Test
//    public void testExtract() throws Exception {
//        FSSharkKB imkb = this.createFilledKB("Testinfo");
//        AnchorSet as = new AnchorSet();
//        as.addAnchor("http://themenkonzept.de", ContextSpace.DIM_TOPIC);
//        as.addAnchor("http://peerkonzept.de", ContextSpace.DIM_ORIGINATOR);
//        // init FPs
//        FragmentationParameter fp = new FragmentationParameter(true, true, 1);
//        FragmentationParameter fps[] = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];
//
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            fps[i] = fp;
//        }
//
//        LocalInterest imi = imkb.createInterest(as, fps);
//
//        Knowledge k = imkb.extract(imi, fp);
//        XMLSerializer xml = new XMLSerializer();
//        System.out.println("Hintergrund des extrahierten Wissens:\n" + xml.serializeContextSpace(k.getContextMap()));
//
//        System.out.println("Anzahl CPs: " + k.getNumberContextPoints());
//        Assert.assertEquals(k.getNumberContextPoints(), 1);
//
//        System.out.println("Anzahl Informationen an CP: " + k.getCP(0).getNumberInformation());
//        Assert.assertEquals(k.getCP(0).getNumberInformation(), 1);
//
//        Enumeration infos = k.getCP(0).getInformation();
//        Assert.assertNotNull(infos);
//
//        while (infos.hasMoreElements()) {
//            Information info = (Information) infos.nextElement();
//            byte[] buf = info.getContentAsByte();
//            String result = new String(buf);
//            Assert.assertEquals(result, "Testinfo");
//        }
//
//    }
//
//    /**
//     * Erzeugt eine kleine FSKB mit 3 Themen, einem Peer und einem CP, der als
//     * Information den uebergebenen String enthaelt.
//     *
//     * @param content String, der in der Information des Kontextpunktes als Content angelegt wird
//     * @return FSryKnowledgeBase
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    private FSSharkKB createFilledKB(String content) throws SharkKBException {
//        FSSharkKB imk = new FSSharkKB(content);
//
//        ROSTSet stset = imk.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSemanticTag nc = null;
//
//        // Thema erzeugen
//        if (stset instanceof FSAssociatedSTSet) {
//            FSAssociatedSTSet sn = (FSAssociatedSTSet) stset;
//            String name = "Themenkonzept";
//            String sis[] = {"http://themenkonzept.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis);
//
//            name = "AnderesThema";
//            String sis2[] = {"http://anderesThema.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis2);
//
//            name = "AnderesThema2";
//            String sis3[] = {"http://anderesThema2.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis3);
//
//        }
//
//        // Peer erzeugen
//        ROSTSet peerStSet = imk.getSTSet(ContextSpace.DIM_ORIGINATOR);
//
//        if (peerStSet instanceof PeerAssociatedSTSet) {
//            FSPeerAssociatedSTSet psn = (FSPeerAssociatedSTSet) peerStSet;
//            String name = "Peerkonzept";
//            String sis[] = {"http://peerkonzept.de"};
//            String addr[] = {"socket://localhost:1725"};
//            PeerAssociatedSemanticTag pnc = psn.createPeerAssociatedSemanticTag(name, sis, addr);
//
//            String name2 = "Peerkonzept2";
//            String sis2[] = {"http://peerkonzept2.de"};
//            String addr2[] = {"socket://localhost:1726"};
//            PeerAssociatedSemanticTag pnc2 = psn.createPeerAssociatedSemanticTag(name2, sis2, addr2);
//
//
//        }
//
//        // Versuche Koordinaten zu erzeugen
//        ContextCoordinates co = new ContextCoordinates();
//        String topicSis[] = {"http://themenkonzept.de"};
//        String peerSis[] = {"http://peerkonzept.de"};
//
//        co.setSI(ContextSpace.DIM_TOPIC, topicSis);
//        co.setSI(ContextSpace.DIM_ORIGINATOR, peerSis);
//
//        String names[] = new String[ContextSpace.MAXDIMENSIONS];
//        names[ContextSpace.DIM_TOPIC] = "themenkonzept";
//        names[ContextSpace.DIM_ORIGINATOR] = "peerkonzept";
//
//        ContextPoint cp = imk.createContextPoint(co, names);
//
//        System.out.println("FSSharkKBTest.createContextPoint " + cp.getCoordinates().toString());
//
//        String info = content;
//        byte[] infoArray = info.getBytes();
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(infoArray);
//        cp.addInformation("Test"+content, bais, infoArray.length);
//
//        return imk;
//    }
//
//    /**
//     * Erzeugt eine kleine FSKB mit 3 Themen, einem Peer und einem CP, der als
//     * Information den uebergebenen String enthaelt.
//     *
//     * @param content String, der in der Information des Kontextpunktes als Content angelegt wird
//     * @return FSryKnowledgeBase
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    private FSSharkKB createFilledKB2(String content) throws SharkKBException {
//        FSSharkKB imk = new FSSharkKB(content);
//
//        ROSTSet stset = imk.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSemanticTag nc = null;
//
//        // Thema erzeugen
//        if (stset instanceof FSAssociatedSTSet) {
//            FSAssociatedSTSet sn = (FSAssociatedSTSet) stset;
//            String name = "Themenkonzept";
//            String sis[] = {"http://themenkonzept.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis);
//
//            name = "AnderesThema";
//            String sis2[] = {"http://anderesThema.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis2);
//
//            name = "AnderesThema2";
//            String sis3[] = {"http://anderesThema2.de"};
//            nc = sn.createAssociatedSemanticTag(name, sis3);
//
//        }
//
//        // Peer erzeugen
//        ROSTSet peerStSet = imk.getSTSet(ContextSpace.DIM_ORIGINATOR);
//
//        if (peerStSet instanceof PeerAssociatedSTSet) {
//            FSPeerAssociatedSTSet psn = (FSPeerAssociatedSTSet) peerStSet;
//            String name = "Peerkonzept2";
//            String sis[] = {"http://peerkonzept2.de"};
//            String addr[] = {"socket://localhost:1726"};
//            PeerAssociatedSemanticTag pnc = psn.createPeerAssociatedSemanticTag(name, sis, addr);
//
//
//        }
//
//
//        // Versuche Koordinaten zu erzeugen
//        ContextCoordinates co = new ContextCoordinates();
//        String topicSis[] = {"http://themenkonzept.de"};
//        String peerSis[] = {"http://peerkonzept.de"};
//
//        co.setSI(ContextSpace.DIM_TOPIC, topicSis);
//        co.setSI(ContextSpace.DIM_ORIGINATOR, peerSis);
//
//        String names[] = new String[ContextSpace.MAXDIMENSIONS];
//        names[ContextSpace.DIM_TOPIC] = "themenkonzept";
//        names[ContextSpace.DIM_ORIGINATOR] = "peerkonzept";
//
//        ContextPoint cp = imk.createContextPoint(co, names);
//
//        System.out.println("FSSharkKBTest.createContextPoint " + cp.getCoordinates().toString());
//
//        String info = content;
//        byte[] infoArray = info.getBytes();
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(infoArray);
//        cp.addInformation("Test", bais, infoArray.length);
//
//        return imk;
//    }
//
//    @Test
//    public void testSetOwner() throws Exception {
//        ROSTSet roOwner = this.kb.getSTSet(ContextSpace.DIM_ORIGINATOR);
//        PeerAssociatedSTSet owner = (PeerAssociatedSTSet) roOwner;
//        PeerAssociatedSemanticTag pnc = owner.createPeerAssociatedSemanticTag("Test", new String[]{"http://www.test.de"}, new String[]{"localhost:1234"});
//        kb.setOwner(pnc);
//        ROPeerSemanticTag result = kb.getOwner();
//        Assert.assertEquals(pnc, result);
//    }
//
//    /**
//     *
//     * Testet die Wegfindung aus <code>AbstractSemanticNet</code>
//     *
//     * @see net.sharkfw.knowledgeBase.FSry.AbstractSemanticNet#findWay(net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.FragmentationParameter)
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testFindWay() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Teste Wegfindung in AbstractSemanticNet. Test 1 \n");
//        FSAssociatedSTSet net = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        AssociatedSemanticTag nc1 = net.createAssociatedSemanticTag("name", new String[]{"http://www.si.de"});
//        AssociatedSemanticTag nc2 = net.createAssociatedSemanticTag("name1", new String[]{"http://www.si1.de"});
//        AssociatedSemanticTag nc3 = net.createAssociatedSemanticTag("name2", new String[]{"http://www.si2.de"});
//        AssociatedSemanticTag nc4 = net.createAssociatedSemanticTag("name3", new String[]{"http://www.si3.de"});
//        AssociatedSemanticTag nc5 = net.createAssociatedSemanticTag("name4", new String[]{"http://www.si4.de"});
//        AssociatedSemanticTag nc6 = net.createAssociatedSemanticTag("name5", new String[]{"http://www.si5.de"});
//        AssociatedSemanticTag nc7 = net.createAssociatedSemanticTag("name6", new String[]{"http://www.si6.de"});
//        AssociatedSemanticTag nc8 = net.createAssociatedSemanticTag("name7", new String[]{"http://www.si7.de"});
//
//        nc1.setPredicate("super", nc2);
//        nc2.setPredicate("super", nc3);
//        nc3.setPredicate("super", nc4);
//        nc4.setPredicate("super", nc5);
//        nc5.setPredicate("super", nc8);
//
//        nc4.setPredicate("sub", nc6); // Totes Ende, weil nc6 keine weiteren Beziehungen hat
//        nc8.setPredicate("sub", nc1); // Bleibt derzeit unberuecksichtigt!
//        nc1.setPredicate("super", nc8);
//
//        FragmentationParameter fp = new FragmentationParameter(true, true, 6);
//        AssociatedSTSet res = (AssociatedSTSet) net.findWay(nc8, nc1, fp);
//        XMLSerializer xml = new XMLSerializer();
//        Assert.assertNotNull(res);
//        System.out.println("Ergebnis kürzester Weg: " + xml.serializeROSTSet(res) + "\n");
//
//
//    }
//
//    /**
//     * Testet die Wegfindung mit erlaubten/verbotenen Assoziationen
//     *
//     * @see net.sharkfw.knowledgeBase.FSry.AbstractSemanticNet#findWay(net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.FragmentationParameter)
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testFindWay2() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Teste Wegfindung in AbstractSemanticNet. Test 2. Benutzt erlaubte Assoziationen \n");
//        FSAssociatedSTSet net = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        AssociatedSemanticTag nc1 = net.createAssociatedSemanticTag("name", new String[]{"http://www.si.de"});
//        AssociatedSemanticTag nc2 = net.createAssociatedSemanticTag("name1", new String[]{"http://www.si1.de"});
//        AssociatedSemanticTag nc3 = net.createAssociatedSemanticTag("name2", new String[]{"http://www.si2.de"});
//        AssociatedSemanticTag nc4 = net.createAssociatedSemanticTag("name3", new String[]{"http://www.si3.de"});
//        AssociatedSemanticTag nc5 = net.createAssociatedSemanticTag("name4", new String[]{"http://www.si4.de"});
//        AssociatedSemanticTag nc6 = net.createAssociatedSemanticTag("name5", new String[]{"http://www.si5.de"});
//        AssociatedSemanticTag nc7 = net.createAssociatedSemanticTag("name6", new String[]{"http://www.si6.de"});
//        AssociatedSemanticTag nc8 = net.createAssociatedSemanticTag("name7", new String[]{"http://www.si7.de"});
//
//        nc1.setPredicate("super", nc2);
//        nc2.setPredicate("super", nc3);
//        nc3.setPredicate("super", nc4);
//        nc4.setPredicate("super", nc5);
//        nc5.setPredicate("super", nc8);
//
//        //nc4.setPredicate("sub", nc6); // Totes Ende, weil nc6 keine weiteren Beziehungen hat
//        //nc8.setPredicate("sub", nc1); // Bleibt derzeit unberuecksichtigt!
//        nc1.setPredicate("a", nc8);
//
//        FragmentationParameter fp = new FragmentationParameter(true, true, 5);
//        AssociatedSTSet res = (AssociatedSTSet) net.findWay(nc8, nc1, fp);
//        XMLSerializer xml = new XMLSerializer();
//        Assert.assertNotNull(res);
//        // Diese Konzepte muessen enthalten sein. Andernfalls fliegt eine Exception
//        res.getSemanticTag(nc1.getSI());
//        res.getSemanticTag(nc2.getSI());
//        res.getSemanticTag(nc3.getSI());
//        res.getSemanticTag(nc4.getSI());
//        res.getSemanticTag(nc5.getSI());
//        res.getSemanticTag(nc8.getSI());
//        System.out.println("Ergebnis kürzester Weg mit erlaubten/verbotenen Assoziationen:\n" + xml.serializeROSTSet(res) + "\n");
//
//    }
//
//    /**
//     * Auch hier besteht ein Problem mit der Dublettenunterdrueckung
//     * Die TimeSTSet wird stellvertretend f. eine beliebige Ontologie genutzt
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testMergeWithSTSet() throws SharkKBException, SharkNotSupportedException {
//        System.out.println("Merge-Test mit TimeSTSet:\n");
//
//        InMemoTimeSTSet to = new InMemoTimeSTSet();
//        InMemoTimeSemanticTag tc1 = to.createTimeSemanticTag(System.currentTimeMillis(), System.currentTimeMillis() + 100000);
//        InMemoTimeSemanticTag tc2 = to.createTimeSemanticTag(System.currentTimeMillis(), System.currentTimeMillis() + 200000);
//        InMemoTimeSemanticTag tc3 = to.createTimeSemanticTag(System.currentTimeMillis(), System.currentTimeMillis() + 300000);
//
//        InMemoTimeSTSet to2 = new InMemoTimeSTSet();
//        to2.createAnyTimeSemanticTag();
//
//        to2.merge(to);
//
//        InMemoTimeSemanticTag toc = (InMemoTimeSemanticTag) to2.getSemanticTag(tc1.getSI());
//        InMemoTimeSemanticTag toc2 = (InMemoTimeSemanticTag) to2.getSemanticTag(tc2.getSI());
//        InMemoTimeSemanticTag toc3 = (InMemoTimeSemanticTag) to2.getSemanticTag(tc3.getSI());
//
//        Assert.assertEquals(tc1.getFrom(), toc.getFrom());
//        Assert.assertEquals(tc2.getFrom(), toc2.getFrom());
//        Assert.assertEquals(tc3.getFrom(), toc3.getFrom());
//
//        Assert.assertEquals(tc1.getTo(), toc.getTo());
//        Assert.assertEquals(tc2.getTo(), toc2.getTo());
//        Assert.assertEquals(tc3.getTo(), toc3.getTo());
//
//        // Sichtbare Ausgabe erzeugen
//        XMLSerializer xml = new XMLSerializer();
//        String to2String = xml.serializeROSTSet(to2);
//        System.out.println("Ergebnis merge to2:\n" + to2String);
//
//    }
//
//    /**
//     * Versuche eine <code>J2SESharkEngine</code> zu instanziieren, die die FS2DimSharkKB nutzt
//     * Erzeuge zusaetzlich einen KP und einen zweiten Peer, beginne einen Austausch zwischen beiden Peers.
//     * Es werden keine Asserts ausgefuehrt. Der Test darf nur kein Exceptions werfen. Das Austauschverhalten
//     * wird andernorts ueberprueft
//     */
//    @Test
//    public void createSharkEngineWithFSKB() throws SharkKBException, SharkProtocolNotSupportedException, SharkNotSupportedException, net.sharkfw.system.SharkNotSupportedException {
//        SharkKB newkb = createFilledKB("SharkEngineTest");
//        J2SESharkEngine se = new J2SESharkEngine(newkb);
//        Assert.assertNotNull(se);
//
//        AnchorSet as = new AnchorSet();
//        as.addAnchor(ContextSpace.INURL, ContextSpace.DIM_DIRECTION);
//        as.addAnchor(ContextSpace.OUTURL, ContextSpace.DIM_DIRECTION);
//        as.addAnchor("http://themenkonzept.de", ContextSpace.DIM_TOPIC);
//        as.addAnchor("http://peerkonzept.de", ContextSpace.DIM_ORIGINATOR);
//
//
//        FragmentationParameter fp = new FragmentationParameter(true, true, 1);
//        FragmentationParameter fps[] = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            fps[i] = fp;
//        }
//
//        //se.setKnowledgeFormat(KEPMessage.XML);
//        se.start(net.sharkfw.protocols.Protocols.TCP, 1725);
//        LocalInterest li = se.getSharkKB().createInterest(as, fps);
//        KnowledgePort kp = new KnowledgePort(se, li);//se.createKP(as, fps);
//        kp.setFP(fps);
//
//        Assert.assertTrue(kp.isIKP());
//        Assert.assertTrue(kp.isOKP());
//        Assert.assertNotNull(kp);
//
//        //kp.start();
//
//        SharkKB newkb2 = createFilledKB2("SharkEngineTest2");
//        J2SESharkEngine se2 = new J2SESharkEngine(newkb2);
//
//        AnchorSet as2 = new AnchorSet();
//        as2.addAnchor(ContextSpace.INURL, ContextSpace.DIM_DIRECTION);
//        as2.addAnchor(ContextSpace.OUTURL, ContextSpace.DIM_DIRECTION);
//        as2.addAnchor("http://themenkonzept.de", ContextSpace.DIM_TOPIC);
//        as2.addAnchor("http://peerkonzept2.de", ContextSpace.DIM_ORIGINATOR);
//        ROSTSet owner2 = newkb2.getSTSet(ContextSpace.DIM_ORIGINATOR);
//        PeerAssociatedSTSet psn = (PeerAssociatedSTSet) owner2;
//        PeerAssociatedSemanticTag pnc = psn.getPeerAssociatedSemanticTag("http://peerkonzept2.de");
//        //PeerDescription pd2 = new PeerDescription(pnc.getName(), pnc.getAddresses(), pnc.getSI());
//        // weiter
//
//
//        FragmentationParameter fp2 = new FragmentationParameter(true, true, 1);
//        FragmentationParameter fps2[] = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            fps2[i] = fp2;
//        }
//
//        //se2.setKnowledgeFormat(KEPMessage.XML);
//        se2.start(net.sharkfw.protocols.Protocols.TCP, 1726);
//        LocalInterest li2 = se2.getSharkKB().createInterest(as2, fps2);
//        KnowledgePort kp2 = new KnowledgePort(se2, li2);//se2.createKP(as2, fps2);
//        kp2.setFP(fps2);
//
//        se.publishAllKp(pnc);
//
//        // Was muss hier passiert sein?
//        XMLSerializer xml = new XMLSerializer();
//        System.out.println("Peer1 enthält:\n" + xml.serializeContextSpace(newkb));
//
//        System.out.println("Peer2 enthält:\n" + xml.serializeContextSpace(newkb2));
//
//
//
//
//    }
//
//    /**
//     * Versuche Kontextpunkt trotz Auslassung auf Rednerdimension zu finden. Der Kontextpunkt
//     * aus <code>createFilledKb()</code> ist nur auf TOPIC und OWNER beschraenkt. Demnach
//     * muss er findbar sein, wenn nach TOPIC und REDNER gesucht wird, solange OWNER bei der Suche
//     * uneingeschraenkt bleibt.
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    //@Test
//    public void testContextCoordinatesOnMoreGeneralContextpoint() throws SharkKBException {
//        SharkKB kb = this.createFilledKB("Content");
//        ROSTSet rospeaker = kb.getSTSet(ContextSpace.DIM_REMOTEPEER);
//        STSet speaker = (STSet) rospeaker;
//        speaker.createSemanticTag("Redner", new String[]{"http://redner.de"});
//
//        ContextCoordinates co = new ContextCoordinates();
//        co.setSI(ContextSpace.DIM_REMOTEPEER, new String[]{"http://redner.de"});
//        co.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://themenkonzept.de"});
//
//        ContextPoint cp = kb.getContextPoint(co);
//        Assert.assertNotNull(cp);
//    }
//
//    /**
//     * Fragmentierung liefert über einem PeerSemanticNet zwar PeerNetConcepts zurück, allerdings werden diese ohne
//     * Adresse erzeugt, da die Fragmentierung nur createNetConcept aufruft, und dort keine Adresse übergeben werden kann.
//     * @throws SharkKBException
//     * @throws SharkNotSupportedException
//     */
//    @Test
//    public void testTypeOfFragmentFromPeerSemanticNet() throws SharkKBException, SharkNotSupportedException {
//        System.out.println("Teste Fragmentierung auf PeerSemanticNet, um die Typsicherheit zu gewaehrleisten");
//        PeerAssociatedSTSet psn = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//        PeerAssociatedSemanticTag p1 = psn.createPeerAssociatedSemanticTag("PeerA", new String[]{"http://peer1.de"}, new String[]{"socket://localhost:7001"});
//        PeerAssociatedSemanticTag p2 = psn.createPeerAssociatedSemanticTag("PeerB", new String[]{"http://peer2.de"}, new String[]{"socket://localhost:7002"});
//        PeerAssociatedSemanticTag p3 = psn.createPeerAssociatedSemanticTag("PeerC", new String[]{"http://peer3.de"}, new String[]{"socket://localhost:7003"});
//
//        p1.setPredicate(AssociatedSTSet.SUPERASSOC, p2);
//        p2.setPredicate(AssociatedSTSet.SUPERASSOC, p3);
//
//        p3.setPredicate(AssociatedSTSet.SUBASSOC, p2);
//        p2.setPredicate(AssociatedSTSet.SUBASSOC, p1);
//
//        Vector sis = new Vector();
//        sis.add("http://peer2.de");
//
//        ROSTSet fragment = psn.fragment(sis.elements(), 1);
//
//        Assert.assertNotNull(fragment);
//        ROSemanticTag roc1 = fragment.getSemanticTag("http://peer1.de");
//        ROSemanticTag roc2 = fragment.getSemanticTag("http://peer2.de");
//
//        PeerAssociatedSemanticTag resultP1 = (PeerAssociatedSemanticTag) roc1;
//        PeerAssociatedSemanticTag resultP2 = (PeerAssociatedSemanticTag) roc2;
//
//        String[] addresses1 = resultP1.getAddresses();
//        String[] addresses2 = resultP2.getAddresses();
//
//        Assert.assertEquals("socket://localhost:7001", addresses1[0]);
//        Assert.assertEquals("socket://localhost:7002", addresses2[0]);
//
//        XMLSerializer xml = new XMLSerializer();
//        System.out.println("Fragment aus PeerSemanticNet:\n" + xml.serializeROSTSet(fragment));
//
//    }
//
//    /**
//     * Dublettenunterdrueckung prüfen.
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testMergeWithSemanticNet() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Merge-Test mit SemanticNet:\n");
//        AssociatedSTSet net = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//        AssociatedSemanticTag nc1 = net.createAssociatedSemanticTag("Test1", new String[]{"http://www.test1.de"});
//        AssociatedSemanticTag nc2 = net.createAssociatedSemanticTag("Test2", new String[]{"http://www.test2.de"});
//        AssociatedSemanticTag nc3 = net.createAssociatedSemanticTag("Test3", new String[]{"http://www.test3.de", "http://www.anders.de"});
//
//        nc1.setPredicate("predicate", nc2);
//        nc1.setPredicate("predicate", nc3);
//        nc3.setPredicate("assoziation", nc2);
//
//        AssociatedSTSet net2 = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//        AssociatedSemanticTag a2 = net2.createAssociatedSemanticTag("Ander", new String[]{"http://www.anders.de"});
//
//        net2.merge(net);
//
//        AssociatedSemanticTag a21 = net2.getAssociatedSemanticTag(nc1.getSI());
//        AssociatedSemanticTag a22 = net2.getAssociatedSemanticTag(nc2.getSI());
//        AssociatedSemanticTag a23 = net2.getAssociatedSemanticTag(nc3.getSI());
//
//        Enumeration a21Concepts = a21.getAssociatedTags("predicate");
//        Assert.assertNotNull(a21Concepts);
//        AssociatedSemanticTag test1 = (AssociatedSemanticTag) a21Concepts.nextElement();
//        Assert.assertNotNull(test1);
//
//        Enumeration a23Concepts = a23.getAssociatedTags("assoziation");
//        Assert.assertNotNull(a23Concepts);
//        AssociatedSemanticTag test2 = (AssociatedSemanticTag) a23Concepts.nextElement();
//        Assert.assertNotNull(test1);
//
//        // Sichtbare Ausgabe erzeugen
//        XMLSerializer xml = new XMLSerializer();
//        String net2String = xml.serializeROSTSet(net2);
//        Assert.assertFalse(net2String.contains("<name>Test3</name>"));
//        System.out.println("Ergebnis merge mit SemanticNet:\n" + net2String);
//
//    }
//
//    /**
//     * Dublettenunterdrueckung prüfen. Achtung: Sicher sein, dass auch "andersherum",
//     * bei der Aufnahme von Ontologie ohne Assoziationen, die lokalen Assoziationen erhalten bleiben.
//     * Die merge Operation soll vertauschbar sein
//     *
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testMergeWithSemanticNetAssocLoss() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Merge-Test mit SemanticNet:\n");
//        AssociatedSTSet net = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//        AssociatedSemanticTag nc1 = net.createAssociatedSemanticTag("Test1", new String[]{"http://www.test1.de"});
//        AssociatedSemanticTag nc2 = net.createAssociatedSemanticTag("Test2", new String[]{"http://www.test2.de"});
//        AssociatedSemanticTag nc3 = net.createAssociatedSemanticTag("Test3", new String[]{"http://www.test3.de", "http://www.anders.de"});
//
//        nc1.setPredicate("predicate", nc2);
//        nc1.setPredicate("predicate", nc3);
//        nc3.setPredicate("assoziation", nc2);
//
//        AssociatedSTSet net2 = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//        AssociatedSemanticTag a2 = net2.createAssociatedSemanticTag("Ander", new String[]{"http://www.anders.de"});
//
//        // merging the other way around
//        net.merge(net2);
//
//        AssociatedSemanticTag a21 = net.getAssociatedSemanticTag(nc1.getSI());
//        AssociatedSemanticTag a22 = net.getAssociatedSemanticTag(nc2.getSI());
//        AssociatedSemanticTag a23 = net.getAssociatedSemanticTag(nc3.getSI());
//
//        Enumeration a21Concepts = a21.getAssociatedTags("predicate");
//        Assert.assertNotNull(a21Concepts);
//        AssociatedSemanticTag test1 = (AssociatedSemanticTag) a21Concepts.nextElement();
//        Assert.assertNotNull(test1);
//
//        Enumeration a23Concepts = a23.getAssociatedTags("assoziation");
//        Assert.assertNotNull(a23Concepts);
//        AssociatedSemanticTag test2 = (AssociatedSemanticTag) a23Concepts.nextElement();
//        Assert.assertNotNull(test1);
//
//        // Sichtbare Ausgabe erzeugen
//        XMLSerializer xml = new XMLSerializer();
//        String net2String = xml.serializeROSTSet(net);
//        Assert.assertFalse(net2String.contains("<name>Ander</name>")); // The correct of that concept is "Test3".
//        System.out.println("Ergebnis merge mit SemanticNet:\n" + net2String);
//
//    }
//
//    private SharkKB getSenderKbWithKatzenContent(int port) throws SharkKBException, SharkDuplicateException {
//        File dir1 = new File("sender");
//        deleteDir(dir1);
//        SharkKB kb = new FSSharkKB("sender");
//        ROSTSet rotopics = kb.getSTSet(ContextSpace.DIM_TOPIC);
//
//        AssociatedSTSet topics = (AssociatedSTSet) rotopics;
//        AssociatedSemanticTag austausch = topics.createAssociatedSemanticTag("Austausch", new String[]{"http://austausch.de"});
//
//        AssociatedSemanticTag katze = topics.createAssociatedSemanticTag("Katzen", new String[]{"http://katzen.de"});
//
//        austausch.setPredicate(AssociatedSTSet.SUBASSOC, katze);
//        katze.setPredicate(AssociatedSTSet.SUPERASSOC, austausch);
//
//        //L.d("Austausch-Konzept erzeugt + Katzen-Konzept und SUB/SUPER Verbindung", TestUtil.class);
//
//        // Eigene Identität
//        ROSTSet owner = kb.getSTSet(ContextSpace.DIM_ORIGINATOR);
//        PeerAssociatedSTSet psn = (PeerAssociatedSTSet) owner;
//        psn.createPeerAssociatedSemanticTag("Sender", new String[]{"http://sender.de"}, new String[]{"socket://localhost:" + port});
//
//        ROSTSet peer = kb.getSTSet(ContextSpace.DIM_PEER);
//        PeerAssociatedSTSet psnPeer = (PeerAssociatedSTSet) peer;
//        psnPeer.createPeerAssociatedSemanticTag("Sender", new String[]{"http://sender.de"}, new String[]{"socket://localhost:" + port});
//
//        // Contextpoint erstellen
//        ContextCoordinates co = new ContextCoordinates();
//        co.setSI(ContextSpace.DIM_ORIGINATOR, new String[]{"http://sender.de"});
//        co.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://austausch.de"});
//
//        ContextPoint cp = kb.createContextPoint(co, null);
//        Information info = cp.addInformation(new Hashtable(), false);
//        String s = "Sendercontent";
//        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
//        info.fillContent(bais, s.getBytes().length);
//
//
//        // Contextpoint erstellen
//        ContextCoordinates co2 = new ContextCoordinates();
//        co2.setSI(ContextSpace.DIM_ORIGINATOR, new String[]{"http://sender.de"});
//        co2.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://katzen.de"});
//
//        ContextPoint cp2 = kb.createContextPoint(co2, null);
//        Information info2 = cp2.addInformation(new Hashtable(), false);
//        String s2 = "SenderKatzencontent";
//        ByteArrayInputStream bais2 = new ByteArrayInputStream(s2.getBytes());
//        info2.fillContent(bais2, s2.getBytes().length);
//
//        //L.d("Sender KB gefüllt mit sender.de und austausch.de und ContextPoint!", TestUtil.class);
//
//        return kb;
//    }
//
//    @Test
//    public void testANYConceptInExtraction() throws SharkKBException, SharkDuplicateException {
//        SharkKB lkb = getSenderKbWithKatzenContent(2000);
//
//
//        AnchorSet as = new AnchorSet();
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            as.addAnchor(ContextSpace.ANYURL, i);
//        }
//
//        LocalInterest interest = lkb.createInterest(as, TestUtil.getFps(true, true, 0));
//        Knowledge k = lkb.extract(interest, TestUtil.getFps(true, true, 0), TestUtil.getFps(true, true, 0));
//        Assert.assertNotNull(k);
//
//    }
//
//    /**
//     * Teste ob ANY auch in den ContextCoordinates richtig verarbeitet wird.
//     * Besonderheit hier:
//     * Die Kontextkoordinaten sind in 6 Dimensionen unbelegt und in einer mit ANY belegt.
//     *
//     * Was bedeuten unbelegte Kontextkoordinaten?
//     * Sollen bedeuten: Egal wo / Immer / Egal was etc. (?)
//     *
//     * @throws SharkKBException
//     * @throws SharkDuplicateException
//     */
//    @Test
//    public void testANYConceptInExtraction2() throws SharkKBException, SharkDuplicateException {
//        SharkKB lkb = getSenderKbWithKatzenContent(2000);
//        ROSTSet ro = lkb.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSTSet net = (AssociatedSTSet) ro;
//        net.createAssociatedSemanticTag(ContextSpace.ANY, ContextSpace.ANYSI);
//
//        ContextCoordinates coAny = new ContextCoordinates();
//        coAny.setSI(ContextSpace.DIM_TOPIC, ContextSpace.ANYSI);
//        ContextPoint cp = lkb.createContextPoint(coAny, null);
//        Information info = cp.addInformation(null, true);
//        String s = "Any-Content";
//        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
//        info.fillContent(bais, s.length());
//
//
//        AnchorSet as = new AnchorSet();
//        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
//            as.addAnchor(ContextSpace.ANYURL, i);
//        }
//
//        LocalInterest interest = lkb.createInterest(as, TestUtil.getFps(true, true, 0));
//        Knowledge k = lkb.extract(interest, TestUtil.getFps(true, true, 0), TestUtil.getFps(true, true, 0));
//        Assert.assertNotNull(k);
//        Assert.assertEquals(3, k.getNumberContextPoints());
//
//    }
//
//    /**
//     * Check if merging with FSPeerSemanticNet works as expected.
//     * TODO: Finish testing w/ Asserts here.
//     *
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test
//    public void mergePeerSemanticNet() throws SharkKBException {
//        PeerAssociatedSTSet psn1 = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        PeerAssociatedSemanticTag pnc1 = psn1.createPeerAssociatedSemanticTag("A", new String[]{"http://a.de"}, new String[]{"socket://localhost:1234"});
//        PeerAssociatedSemanticTag pnc2 = psn1.createPeerAssociatedSemanticTag("B", new String[]{"http://b.de"}, new String[]{"socket://localhost:5678"});
//        pnc1.setPredicate("connected", pnc2);
//        pnc2.setPredicate("connected", pnc1);
//
//        PeerAssociatedSTSet psn2 = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        PeerAssociatedSemanticTag pnc3 = psn2.createPeerAssociatedSemanticTag("C", new String[]{"http://a.de"}, new String[]{"socket://localhost:1234"});
//        PeerAssociatedSemanticTag pnc4 = psn2.createPeerAssociatedSemanticTag("D", new String[]{"http://d.de"}, new String[]{"socket://localhost:9012"});
//        pnc3.setPredicate("connected", pnc4);
//        pnc4.setPredicate("connected", pnc3);
//
//        psn1.merge(psn2);
//
//        PeerAssociatedSemanticTag res1 = psn1.getPeerAssociatedSemanticTag(pnc1.getSI());
//        Enumeration assocs = res1.getAssociatedTags("connected");
//        while (assocs.hasMoreElements()) {
//            PeerAssociatedSemanticTag one = (PeerAssociatedSemanticTag) assocs.nextElement();
//            System.out.println("Assoc zu: " + one.getName());
//        }
//    }
//
//    /**
//     * Check if merging with FSPeerSemanticNet works as expected.
//     * Checks also if a HIDDEN property is still around after copying of the original concept.
//     *
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test
//    public void mergePeerSemanticNetWithHiddenFlag() throws SharkKBException {
//        PeerAssociatedSTSet psn1 = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        PeerAssociatedSemanticTag pnc1 = psn1.createPeerAssociatedSemanticTag("A", new String[]{"http://a.de"}, new String[]{"socket://localhost:1234"});
//        PeerAssociatedSemanticTag pnc2 = psn1.createPeerAssociatedSemanticTag("B", new String[]{"http://b.de"}, new String[]{"socket://localhost:5678"});
//        pnc1.setPredicate("connected", pnc2);
//        pnc2.setPredicate("connected", pnc1);
//
//        PeerAssociatedSTSet psn2 = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        PeerAssociatedSemanticTag pnc3 = psn2.createPeerAssociatedSemanticTag("C", new String[]{"http://a.de"}, new String[]{"socket://localhost:1234"});
//        PeerAssociatedSemanticTag pnc4 = psn2.createPeerAssociatedSemanticTag("D", new String[]{"http://d.de"}, new String[]{"socket://localhost:9012"});
//        pnc3.setPredicate("connected", pnc4);
//        pnc4.setPredicate("connected", pnc3);
//        pnc4.setProperty(ROSemanticTag.HIDDEN, "value");
//
//        psn1.merge(psn2);
//
//        PeerAssociatedSemanticTag res1 = psn1.getPeerAssociatedSemanticTag(pnc1.getSI());
//        Enumeration assocs = res1.getAssociatedTags("connected");
//        while (assocs.hasMoreElements()) {
//            PeerAssociatedSemanticTag one = (PeerAssociatedSemanticTag) assocs.nextElement();
//            System.out.println("Assoc zu: " + one.getName());
//        }
//
//        PeerAssociatedSemanticTag res2 = psn1.getPeerAssociatedSemanticTag(pnc4.getSI());
//        Assert.assertNotNull(res2);
//        String propRes2 = res2.getProperty(ROSemanticTag.HIDDEN);
//        Assert.assertEquals(propRes2, "value");
//
//    }
//
//    /**
//     * Soll die Merge-Operation auf dem TimeSTSet prüfen.
//     * Nach dem Zusammenführen beider Ontologien, sollen die ursprünglichen Konzepte der to2
//     * erhalten sein und die Konzepte aus to1 zusätzlich angelegt worden sein.
//     *
//     * @throws SharkKBException
//     */
//    @Test
//    public void testMergeTimeSTSet() throws SharkKBException {
//
//        // create ontologies
//        InMemoTimeSTSet to1 = new InMemoTimeSTSet();
//        InMemoTimeSTSet to2 = new InMemoTimeSTSet();
//
//        int from_1 = 164738291;
//        int to_1 = 182368345;
//
//        int from_2 = 123456789;
//        int to_2 = 234567891;
//
//        int from_3 = 321456789;
//        int to_3 = 765123455;
//
//        // create concepts in both ontologien
//        to1.createTimeSemanticTag(from_1, to_1);
//        to1.createTimeSemanticTag(from_2, to_2);
//
//        to2.createTimeSemanticTag(from_3, to_3);
//
//        to2.merge(to1);
//
//        // try to find unification of all concepts inside to2
//        SemanticTag st1 = to2.getSemanticTag("sharkTime://" + from_1 + "," + to_1);
//        SemanticTag st2 = to2.getSemanticTag("sharkTime://" + from_2 + "," + to_2);
//        SemanticTag st3 = to2.getSemanticTag("sharkTime://" + from_3 + "," + to_3);
//
//        Assert.assertNotNull(st1);
//        Assert.assertNotNull(st2);
//        Assert.assertNotNull(st3);
//    }
//
//    /**
//     * Teste ob ANY auch in den <code>ContextCoordinates richtig</code> verarbeitet wird.
//     * Besonderheit hier:
//     * Die Kontextkoordinaten sind in allen 7 Dimensionen mit <code>null</code> belegt.
//     *
//     * Was bedeuten unbelegte Kontextkoordinaten?
//     * Sollen bedeuten: Egal wo / Immer / Egal was etc. (?)
//     *
//     * @throws SharkKBException
//     * @throws SharkDuplicateException
//     */
//    @Test
//    public void testANYConceptInExtractionUsingNull() throws SharkKBException, SharkDuplicateException {
//        SharkKB lkb = getSenderKbWithKatzenContent(2000);
//        ROSTSet ro = lkb.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSTSet net = (AssociatedSTSet) ro;
//        net.createAssociatedSemanticTag(ContextSpace.ANY, ContextSpace.ANYSI);
//
//        ContextCoordinates coAny = new ContextCoordinates();
//        coAny.setSI(ContextSpace.DIM_TOPIC, ContextSpace.ANYSI);
//        ContextPoint cp = lkb.createContextPoint(coAny, null);
//        Information info = cp.addInformation(null, true);
//        String s = "Any-Content";
//        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
//        info.fillContent(bais, s.length());
//
//
//        AnchorSet as = new AnchorSet();
////      for(int i = 0; i < ContextSpace.MAXDIMENSIONS; i++){
////        as.addAnchor(ContextSpace.ANYURL, i);
////      }
//
//        LocalInterest interest = lkb.createInterest(as, TestUtil.getFps(true, true, 0));
//        Knowledge k = lkb.extract(interest, TestUtil.getFps(true, true, 0), TestUtil.getFps(true, true, 0));
//        Assert.assertNotNull(k);
//        Assert.assertEquals(3, k.getNumberContextPoints());
//    }
//
//    /**
//     *
//     * Testet die Wegfindung aus <code>AbstractSemanticNet</code> für <code>PeerSemanticNet</code>
//     *
//     * @see net.sharkfw.knowledgeBase.FSry.AbstractSemanticNet#findWay(net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.NetConcept, net.sharkfw.knowledgeBase.FragmentationParameter)
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     * @throws net.sharkfw.knowledgeBase.SharkNotSupportedException
//     */
//    @Test
//    public void testFindWayPeerAssociatedSTSet() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Teste Wegfindung in AbstractSemanticNet. Test 1 \n");
//        FSPeerAssociatedSTSet net = new FSPeerAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        PeerAssociatedSemanticTag nc1 = net.createPeerAssociatedSemanticTag("name", new String[]{"http://www.si.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc2 = net.createPeerAssociatedSemanticTag("name1", new String[]{"http://www.si1.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc3 = net.createPeerAssociatedSemanticTag("name2", new String[]{"http://www.si2.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc4 = net.createPeerAssociatedSemanticTag("name3", new String[]{"http://www.si3.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc5 = net.createPeerAssociatedSemanticTag("name4", new String[]{"http://www.si4.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc6 = net.createPeerAssociatedSemanticTag("name5", new String[]{"http://www.si5.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc7 = net.createPeerAssociatedSemanticTag("name6", new String[]{"http://www.si6.de"}, new String[]{"socket://localhost:1234"});
//        AssociatedSemanticTag nc8 = net.createPeerAssociatedSemanticTag("name7", new String[]{"http://www.si7.de"}, new String[]{"socket://localhost:1234"});
//
//        nc1.setPredicate("super", nc2);
//        nc2.setPredicate("super", nc3);
//        nc3.setPredicate("super", nc4);
//        nc4.setPredicate("super", nc5);
//        nc5.setPredicate("super", nc8);
//
//        nc4.setPredicate("sub", nc6); // Totes Ende, weil nc6 keine weiteren Beziehungen hat
//        nc8.setPredicate("sub", nc1); // Bleibt derzeit unberuecksichtigt!
//        nc1.setPredicate("super", nc8);
//
//        FragmentationParameter fp = new FragmentationParameter(true, true, 6);
//        AssociatedSTSet res = (AssociatedSTSet) net.findWay(nc8, nc1, fp);
//        XMLSerializer xml = new XMLSerializer();
//        Assert.assertNotNull(res);
//        Assert.assertTrue(res instanceof PeerAssociatedSTSet); // We need to return a PeerSemanticNet, in order to allow access to addresses!
//        System.out.println("Ergebnis kürzester Weg: " + xml.serializeROSTSet(res) + "\n");
//
//        // Check if address information is available in result
//        PeerAssociatedSemanticTag resTag = (PeerAssociatedSemanticTag) res.getSemanticTag(nc1.getSI());
//        Assert.assertEquals(resTag.getAddresses()[0], nc1.getAddresses()[0]);
//    }
//
//    /**
//     * Check if the correct contextpoints are returned provided the correct coordinates
//     *
//     * @throws SharkKBException
//     */
//    @Test
//    public void testContextPointRetrieval1() throws SharkKBException {
//        SharkKB _kb = new FSSharkKB("retrieval");
//
//        /*
//         * Create at least one concept on each dimension
//         */
//        AssociatedSTSet topics = (AssociatedSTSet) _kb.getSTSet(ContextSpace.DIM_TOPIC);
//        AssociatedSemanticTag test1 = topics.createAssociatedSemanticTag("Test1", new String[]{"http://test1.de"});
//        AssociatedSemanticTag test2 = topics.createAssociatedSemanticTag("Test2", new String[]{"http://test2.de"});
//
//        PeerSTSet originator = (PeerSTSet) _kb.getSTSet(ContextSpace.DIM_ORIGINATOR);
//        PeerSemanticTag owner = originator.createPeerSemanticTag("Owner", new String[]{"http://owner.de"}, new String[]{"socket://localhost:1234"});
//
//        PeerSTSet peer = (PeerSTSet) _kb.getSTSet(ContextSpace.DIM_PEER);
//        PeerSemanticTag peer1 = peer.createPeerSemanticTag("Owner", new String[]{"http://owner.de"}, new String[]{"socket://localhost:1234"});
//
//        PeerSTSet remotepeer = (PeerSTSet) _kb.getSTSet(ContextSpace.DIM_REMOTEPEER);
//        PeerSemanticTag remote1 = remotepeer.createPeerSemanticTag("Remote1", new String[]{"http://remote1.de"}, new String[]{"socket://123.123.123:1234"});
//
//        GeoSTSet geo = (GeoSTSet) _kb.getSTSet(ContextSpace.DIM_LOCATION);
//        GeoSemanticTag geo1 = geo.createGeoSemanticTag(52.13, 13.15);
//
//        InMemoTimeSTSet time = (InMemoTimeSTSet) _kb.getSTSet(ContextSpace.DIM_TIME);
//        InMemoTimeSemanticTag time1 = time.createTimeSemanticTag(12345678, 77777777);
//
//        /*
//         * Create ContextCoordinates that vary in the number of set dimensions
//         */
//
//        ContextCoordinates co1 = new ContextCoordinates();
//        co1.setSI(ContextSpace.DIM_TOPIC, test1.getSI());
//        co1.setSI(ContextSpace.DIM_PEER, peer1.getSI());
//        co1.setSI(ContextSpace.DIM_REMOTEPEER, remote1.getSI());
//
//        ContextCoordinates co2 = new ContextCoordinates();
//        co2.setSI(ContextSpace.DIM_TOPIC, test1.getSI());
//
//        ContextCoordinates co3 = new ContextCoordinates();
//        co3.setSI(ContextSpace.DIM_TOPIC, test1.getSI());
//        co3.setSI(ContextSpace.DIM_ORIGINATOR, owner.getSI());
//
//        ContextCoordinates co4 = new ContextCoordinates();
//        co4.setSI(ContextSpace.DIM_TOPIC, test1.getSI());
//        co4.setSI(ContextSpace.DIM_TIME, time1.getSI());
//        co4.setSI(ContextSpace.DIM_REMOTEPEER, remote1.getSI());
//        co4.setSI(ContextSpace.DIM_PEER, peer1.getSI());
//        co4.setSI(ContextSpace.DIM_ORIGINATOR, owner.getSI());
//        co4.setSI(ContextSpace.DIM_LOCATION, geo1.getSI());
//
//        ContextPoint cp1 = _kb.createContextPoint(co1, null);
//        ContextPoint cp2 = _kb.createContextPoint(co2, null);
//        ContextPoint cp3 = _kb.createContextPoint(co3, null);
//        ContextPoint cp4 = _kb.createContextPoint(co4, null);
//
//        String t1 = "test1";
//        String t2 = "test2";
//        String t3 = "test3";
//        String t4 = "test4";
//
//        InputStream is1 = new ByteArrayInputStream(t1.getBytes());
//        cp1.addInformation(t1, is1, t1.getBytes().length);
//
//        InputStream is2 = new ByteArrayInputStream(t2.getBytes());
//        cp2.addInformation(t2, is2, t2.getBytes().length);
//
//        InputStream is3 = new ByteArrayInputStream(t3.getBytes());
//        cp3.addInformation(t3, is3, t3.getBytes().length);
//
//        InputStream is4 = new ByteArrayInputStream(t4.getBytes());
//        cp4.addInformation(t4, is4, t4.getBytes().length);
//
//        ContextPoint cpR2 = _kb.getContextPoint(co2);
//        ContextPoint cpR1 = _kb.getContextPoint(co1);
//        ContextPoint cpR4 = _kb.getContextPoint(co4);
//        ContextPoint cpR3 = _kb.getContextPoint(co3);
//
//        Assert.assertNotNull(cpR1);
//        Assert.assertNotNull(cpR2);
//        Assert.assertNotNull(cpR3);
//        Assert.assertNotNull(cpR4);
//
//        Assert.assertEquals(cp1, cpR1);
//        Assert.assertEquals(cp2, cpR2);
//        Assert.assertEquals(cp3, cpR3);
//        Assert.assertEquals(cp4, cpR4);
//
//        ContextCoordinates co5 = new ContextCoordinates();
//        co5.setSI(ContextSpace.DIM_TOPIC, test2.getSI());
//        co5.setSI(ContextSpace.DIM_TIME, time1.getSI());
//        co5.setSI(ContextSpace.DIM_REMOTEPEER, remote1.getSI());
//        co5.setSI(ContextSpace.DIM_PEER, peer1.getSI());
//        co5.setSI(ContextSpace.DIM_ORIGINATOR, owner.getSI());
//        co5.setSI(ContextSpace.DIM_LOCATION, geo1.getSI());
//
//        ContextPoint cp5 = _kb.createContextPoint(co5, null);
//
//        String t5 = "Test5";
//        InputStream is5 = new ByteArrayInputStream(t5.getBytes());
//        cp5.addInformation(t5, is5, t5.getBytes().length);
//
//        /*
//         * these coordinates will most likely return the cp5
//         * although not all dimensions are same
//         */
//        ContextCoordinates co6 = new ContextCoordinates();
//        co6.setSI(ContextSpace.DIM_TOPIC, test2.getSI());
//
//        ContextPoint cp6 = _kb.createContextPoint(co6, null);
//        String t6 = "Test6";
//        InputStream is6 = new ByteArrayInputStream(t6.getBytes());
//        cp6.addInformation(t6, is6, t6.getBytes().length);
//
//        /*
//         * Is it correct, that this contextpoint is returned?
//         */
//        ContextPoint cpR5 = _kb.getContextPoint(co5);
//        Assert.assertEquals(cp5, cpR5);
//
//        ContextPoint cpR6 = _kb.getContextPoint(co6);
//        Assert.assertEquals(cp6, cpR6);
//    }
//
//    /**
//     * Test if the fragmentation looks after hidden tags.
//     * Those tags must not be part of the result set of the fragmentation.
//     *
//     * @throws SharkKBException
//     * @throws SharkNotSupportedException
//     */
//    @Test
//    public void testFragmentationWithHiddenTags() throws SharkKBException, SharkNotSupportedException {
//
//        System.out.println("Test Fragmentation with 'hidden' tags\n");
//        FSAssociatedSTSet net = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//
//        AssociatedSemanticTag nc1 = net.createAssociatedSemanticTag("name", new String[]{"http://www.si.de"});
//        AssociatedSemanticTag nc2 = net.createAssociatedSemanticTag("name1", new String[]{"http://www.si1.de"});
//        AssociatedSemanticTag nc3 = net.createAssociatedSemanticTag("name2", new String[]{"http://www.si2.de"});
//        AssociatedSemanticTag nc4 = net.createAssociatedSemanticTag("name3", new String[]{"http://www.si3.de"});
//        AssociatedSemanticTag nc5 = net.createAssociatedSemanticTag("name4", new String[]{"http://www.si4.de"});
//        AssociatedSemanticTag nc6 = net.createAssociatedSemanticTag("name5", new String[]{"http://www.si5.de"});
//        AssociatedSemanticTag nc7 = net.createAssociatedSemanticTag("name6", new String[]{"http://www.si6.de"});
//        AssociatedSemanticTag nc8 = net.createAssociatedSemanticTag("name7", new String[]{"http://www.si7.de"});
//
//        // set invisibility
//        nc3.setProperty(ROSemanticTag.HIDDEN, "true");
//
//        nc1.setPredicate("super", nc2);
//        nc2.setPredicate("super", nc3);
//        nc3.setPredicate("super", nc4);
//        nc4.setPredicate("super", nc5);
//        nc5.setPredicate("super", nc8);
//
//        // configure the fragmentation
//        FragmentationParameter fp = new FragmentationParameter(true, true, 5);
//        Vector anchors = new Vector();
//        anchors.add(nc1.getSI()[0]);
//        Enumeration anchorEnum = anchors.elements();
//        Hashtable forbiddenProps = new Hashtable();
//        forbiddenProps.put(ROSemanticTag.HIDDEN, "true");
//        AssociatedSTSet res = (AssociatedSTSet) net.fragment(anchorEnum, fp.getDepth(), null, forbiddenProps);
//
//        // Check if all concepts are in place
//        res.getSemanticTag(nc1.getSI());
//        res.getSemanticTag(nc2.getSI());
//        res.getSemanticTag(nc4.getSI());
//        res.getSemanticTag(nc5.getSI());
//
//        // Nc3 must not be in the result set, because it was flagged to be hidden!
//        try {
//            res.getSemanticTag(nc3.getSI());
//            // provoke assertion failure
//            Assert.assertTrue(false);
//        } catch (SharkKBException skex) {
//            Assert.assertTrue(true);
//        }
//    }
//
//    @Test
//    public void testDuplicateSuppression() throws SharkKBException {
//        AssociatedSTSet stset = new FSAssociatedSTSet(FSSharkKB.getNextTempPath());
//        stset.createAssociatedSemanticTag("Test", new String[]{"http://www.test.de", "http://test.de"});
//        stset.createAssociatedSemanticTag("Test", new String[]{"http://test.de", "http://test.org"});
//
//        SemanticTag tag1 = stset.getSemanticTag("http://www.test.de");
//        SemanticTag tag2 = stset.getSemanticTag("http://test.org");
//
//        Assert.assertEquals(tag1, tag2);
//    }
//
//    /**
//     * Two contextpoints are created:
//     * 1. With all coords set to <code>null</code>
//     * 2. With all coords set to <code>null</code> except one dimension
//     *
//     * Test if <code>getContextPoint()</code> returns the concrete contextpoint instead
//     * of the more general (completely undefined) contextpoint.
//     *
//     * @throws SharkKBException
//     * @throws SharkDuplicateException
//     */
//    //@Test
//    public void allNullCoordTest() throws SharkKBException, SharkDuplicateException {
//        SharkKB kb = TestUtil.getSenderKb(5000);
//
//        ContextCoordinates co1 = new ContextCoordinates();
//        ContextPoint cp1 = kb.createContextPoint(co1, null);
//        String test1 = "test1";
//        InputStream is = new ByteArrayInputStream(test1.getBytes());
//        cp1.addInformation(test1, is, test1.getBytes().length);
//
//        ContextCoordinates co2 = new ContextCoordinates();
//        co2.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://austausch.de"});
//        ContextPoint cp2 = kb.createContextPoint(co2, null);
//        String test2 = "test2";
//        InputStream is2 = new ByteArrayInputStream(test2.getBytes());
//        cp2.addInformation(test2, is2, test2.getBytes().length);
//
//        ContextPoint cpResult = kb.getContextPoint(co2);
//        Assert.assertEquals(cpResult.getCoordinates(), co2);
//
//    }
//}
