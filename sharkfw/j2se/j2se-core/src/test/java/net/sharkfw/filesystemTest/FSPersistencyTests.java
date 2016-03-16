//package filesystemTest;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import net.sharkfw.knowledgeBase.inmemory.InMemoContextCoordinates;
//import net.sharkfw.knowledgeBase.ContextPoint;
//import net.sharkfw.knowledgeBase.ContextSpace;
//import net.sharkfw.knowledgeBase.internal.InternalSharkKB;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSTSet;
//import net.sharkfw.knowledgeBase.legacy.fs.FSAssociatedSemanticTag;
//import net.sharkfw.knowledgeBase.legacy.fs.FSSharkKB;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author rw
// */
//public class FSPersistencyTests {
//
//    public FSPersistencyTests() {
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
//    public void createSharkKB() {
//        try {
//            //Create KB
//            FSSharkKB kb = new FSSharkKB("P2PTubeData");
//            FSAssociatedSTSet topics = (FSAssociatedSTSet) kb.getSTSet(ContextSpace.DIM_TOPIC);
//            topics.createSemanticTag("Topic1", new String[]{"http://www.topic1.com"});
//            topics.createSemanticTag("Topic2", new String[]{"http://www.topic2.com"});
//            topics.createSemanticTag("Topic3", new String[]{"http://www.topic3.com"});
//
//            //Reload KB
//            FSSharkKB kb2 = new FSSharkKB("P2PTubeData");
//
//            FSAssociatedSTSet topicsNew = (FSAssociatedSTSet) kb.getSTSet(ContextSpace.DIM_TOPIC);
//
//            FSAssociatedSemanticTag topic1 = (FSAssociatedSemanticTag) topicsNew.getSemanticTag("http://www.topic1.com");
//            FSAssociatedSemanticTag topic2 = (FSAssociatedSemanticTag) topicsNew.getSemanticTag("http://www.topic2.com");
//
//            topic1 = (FSAssociatedSemanticTag) topicsNew.setSuperAssociation(topic1, topic2);
//            Assert.assertNotNull(topic1);
//            Assert.assertNotNull(topic2);
//
//            InMemoContextCoordinates co = new InMemoContextCoordinates();
//            //co.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://www.topic2.com"});
//
//            ContextPoint cp1 = kb2.createContextPoint(co, new String[]{"Test"});
//
//            ContextPoint cp = kb2.getContextPoint(co);
//            Assert.assertNotNull(cp);
//
//        } catch (SharkKBException ex) {
//            Logger.getLogger(FSPersistencyTests.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//
//    }
//
//    @Test
//    public void createSharkKBWithPassword() {
//        try {
//            String nameOfKB = "P2PTubeWithPass";
//            FSSharkKB kb = new FSSharkKB(nameOfKB, "password");
//            FSAssociatedSTSet topics = (FSAssociatedSTSet) kb.getSTSet(ContextSpace.DIM_TOPIC);
//            topics.createSemanticTag("Topic1", new String[]{"http://www.topic1.com"});
//            topics.createSemanticTag("Topic2", new String[]{"http://www.topic2.com"});
//
//            FSSharkKB kbWrongPass = new FSSharkKB(nameOfKB, "wrongpassword");
//            FSAssociatedSTSet topicsShouldBeNull = (FSAssociatedSTSet) kbWrongPass.getSTSet(ContextSpace.DIM_TOPIC);
//            Assert.assertNull(topicsShouldBeNull);
//
//            FSSharkKB kb2 = new FSSharkKB(nameOfKB, "password");
//            FSAssociatedSTSet topics2 = (FSAssociatedSTSet) kb2.getSTSet(ContextSpace.DIM_TOPIC);
//
//            System.out.println(kb2.getProperty(InternalSharkKB.OWNER));
//            Assert.assertEquals(nameOfKB, kb2.getProperty(InternalSharkKB.OWNER));
//
//            FSAssociatedSemanticTag topic1 = (FSAssociatedSemanticTag) topics2.getSemanticTag("http://www.topic1.com");
//            FSAssociatedSemanticTag topic2 = (FSAssociatedSemanticTag) topics2.getSemanticTag("http://www.topic2.com");
//
//            topic1 = (FSAssociatedSemanticTag) topics2.setSuperAssociation(topic1, topic2);
//
//            Assert.assertNotNull(topic1);
//            Assert.assertNotNull(topic2);
//
//            InMemoContextCoordinates co = new InMemoContextCoordinates();
//            //co.setSI(ContextSpace.DIM_TOPIC, new String[]{"http://www.topic2.com"});
//
//            ContextPoint cp1 = kb2.createContextPoint(co, new String[]{"Test"});
//
//            ContextPoint cp = kb2.getContextPoint(co);
//
//            Assert.assertNotNull(cp);
//
//        } catch (SharkKBException ex) {
//        }
//    }
//
//    /*@Test
//    public void testTMDbConnection() {
//    try {
//    FSSharkKB kb = new FSSharkKB("TMDB");
//    FSAssociatedSTSet topics = (FSAssociatedSTSet) kb.getSTSet(ContextSpace.DIM_TOPIC);
//    topics.createSemanticTag("Movies", new String[]{"http://www.themoviedb.org/movie/"});
//    topics.createSemanticTag("Categories", new String[]{"http://www.themoviedb.org/category/"});
//    topics.createSemanticTag("People", new String[]{"http://www.themoviedb.org/person/"});
//    topics.createSemanticTag("Companies", new String[]{"http://www.themoviedb.org/company/"});
//    topics.createSemanticTag("Countries", new String[]{"http://www.themoviedb.org/country/"});
//    TMDbProcess process = new TMDbProcess(kb);
//    //process.getMovies(7000, 100);
//
//    ContextCoordinates cc = new ContextCoordinates();
//    cc.setSI(ContextSpace.DIM_TOPIC, new String[] {"http://themoviedb.org/genre/animation"});
//    ContextPoint cp = kb.createContextPoint(cc, new String[] {"nothing"});
//    Assert.assertNotNull(cp);
//    cp = kb.getContextPoint(cc);
//    Assert.assertNotNull(cp);
//
//    AssociatedSemanticTag st = topics.getAssociatedSemanticTag("http://www.themoviedb.org/movie/500");
//    Assert.assertNotNull(st);
//
//    Enumeration enu = st.getAssociatedTags("Director");
//    Assert.assertNotNull(enu);
//    while(enu.hasMoreElements()) {
//    AssociatedSemanticTag assoc = (AssociatedSemanticTag) enu.nextElement();
//    System.out.println(assoc.getProperty("fullname"));
//    }
//    enu = st.getAssociatedTags(AssociatedSTSet.SUPERASSOC);
//    Assert.assertNotNull(enu);
//    int numberOfSuper  = 0;
//    AssociatedSemanticTag assoc = null;
//    while(enu.hasMoreElements()) {
//    assoc = (AssociatedSemanticTag) enu.nextElement();
//    System.out.println(assoc.getID());
//    numberOfSuper++;
//    }
//    Assert.assertEquals(numberOfSuper, 1);
//
//    //All subs of Movies, show who the director is.
//
//    enu = assoc.getAssociatedTags(AssociatedSTSet.SUBASSOC);
//    int i = 0;
//    while(enu.hasMoreElements()) {
//    String director = "";
//    boolean first = true;
//    AssociatedSemanticTag assocNew = (AssociatedSemanticTag) enu.nextElement();
//    Enumeration directors = assocNew.getAssociatedTags("Director");
//    if(directors==null) continue;
//    while(directors.hasMoreElements()) {
//    AssociatedSemanticTag directedBy = (AssociatedSemanticTag) directors.nextElement();
//    if(first) {
//    first = false;
//    director = directedBy.getName();
//    }
//    else {
//    director = director + " & " + directedBy.getName();
//    }
//    }
//    //System.out.println(assocNew.getName() + " was directed by " + director);
//    i++;
//    }
//    Assert.assertTrue(i > 0);
//    System.out.println(i);
//
//    } catch (SharkKBException ex) {
//    Logger.getLogger(FSPersistencyTests.class.getName()).log(Level.SEVERE, null, ex);
//    }
//    }*/
//}
