///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package ontology;
//
//import java.util.Vector;
//import net.sharkfw.knowledgeBase.ContextSpace;
//import net.sharkfw.knowledgeBase.FragmentationParameter;
//import net.sharkfw.knowledgeBase.internal.InternalGeoSemanticTagOld;
//import net.sharkfw.knowledgeBase.internal.InternalGeoSTSetOld;
//import net.sharkfw.knowledgeBase.SemanticTag;
//import net.sharkfw.knowledgeBase.internal.InternalROGeoSTSetOld;
//import net.sharkfw.knowledgeBase.internal.InternalROSTSet;
//import net.sharkfw.knowledgeBase.ROSemanticTag;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.inmemory.location.InMemoGeoSTSet;
//import net.sharkfw.system.L;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// * @author Jacob Zschunke
// */
//public class InMemoGeoSTSetTest {
//
//    public InMemoGeoSTSetTest() {
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
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of createConcept method, of class InMemoGeoSTSet.
//     */
//    @Test
//    public void testCreateConcept() throws Exception
//    {
//        String name = "52.457818;13.525153";
//        String[] si = new String[] {"http://52.457818;13.525153"};
//        InMemoGeoSTSet instance = new InMemoGeoSTSet();
//        SemanticTag expResult = instance.createSemanticTag(name, si);;
//        SemanticTag result = instance.getSemanticTag(si);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of createGeoConcept method, of class InMemoGeoSTSet.
//     */
//    @Test
//    public void testCreateGeoConcept() throws Exception
//    {
//        System.out.println("createGeoConcept");
//        InMemoGeoSTSet instance = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld expResult = instance.createGeoSemanticTag(52.457818,13.525153);
//        InternalGeoSemanticTagOld result = instance.getGeoSemanticTag(52.457818,13.525153);
//        assertEquals(expResult, result);
//    }
//
//    @Test
//    public void testIsInRange() throws SharkKBException
//    {
//        InMemoGeoSTSet instance = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld gc1 = instance.createGeoSemanticTag(52.457818,13.525153);
//        InternalGeoSemanticTagOld gc2 = instance.createGeoSemanticTag(52.458785,13.523952);
//        InternalGeoSemanticTagOld gc3 = instance.createGeoSemanticTag(52.448403,13.498052);
//        assertTrue(instance.isInRange(gc1, gc2, 200000.0));
//        assertFalse(instance.isInRange(gc1, gc3, 10.0));
//    }
//
//    @Test
//    public void testFragment() throws SharkKBException
//    {
//        InMemoGeoSTSet instance = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld gc1 = instance.createGeoSemanticTag(52.457818,13.525153);
//        InternalGeoSemanticTagOld gc2 = instance.createGeoSemanticTag(52.458785,13.523952);
//        InternalGeoSemanticTagOld gc3 = instance.createGeoSemanticTag(52.448403,13.498052);
//        Vector v = new Vector();
//        v.add(gc1.getSI()[0]);
//        InternalROGeoSTSetOld geoTology = instance.fragment(v.elements(), 200000.0);
//        InternalGeoSemanticTagOld result = geoTology.getGeoSemanticTag(gc2.getLatitude(), gc2.getLongitude());
//        if(gc2.getLatitude() == result.getLatitude() && gc2.getLongitude() == result.getLongitude())
//        {
//            assertTrue(true);
//        }
//    }
//
//    @Test
//    public void testNormalFragment() throws SharkKBException
//    {
//        InMemoGeoSTSet instance = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld gc1 = instance.createGeoSemanticTag(52.457818,13.525153);
//        InternalGeoSemanticTagOld gc2 = instance.createGeoSemanticTag(52.458785,13.523952);
//        InternalGeoSemanticTagOld gc3 = instance.createGeoSemanticTag(52.448403,13.498052);
//        Vector v = new Vector();
//        v.add(gc1.getSI()[0]);
//        InternalROSTSet stSet = instance.fragment(v.elements(), 500000);
//        SemanticTag oc = stSet.getSemanticTag(gc2.getSI());
//        assertNotNull(oc);
//    }
//
//    /**
//     * This test shall check if it is able to find a way between two concepts.
//     * The <code>FragmentationParameter</code> contains the depth, which represents the
//     * maximum distance that is allowed. If both concepts are within that distance,
//     * an STSet shall be returned, containing both concepts.
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test
//    public void testFindWayPositive() throws SharkKBException {
//        L.d("testFindWayPositive() started", this);
//        InternalGeoSTSetOld geo = new InMemoGeoSTSet();
//        // create concepts close to each other
//        InternalGeoSemanticTagOld gc1 = geo.createGeoSemanticTag(52.040719,8.595638);
//        InternalGeoSemanticTagOld gc2 = geo.createGeoSemanticTag(52.458785,13.523952);
//        L.d("Created concepts in original STSet", this);
//
//        FragmentationParameter otp = new FragmentationParameter(true, true, 500000);
//        InternalROSTSet result = geo.findWay(gc1, gc2, otp);
//        Assert.assertNotNull(result);
//        L.d("Success: Returned STSet is not null.", this);
//
//        ROSemanticTag gc21 = result.getSemanticTag(gc2.getSI());
//        ROSemanticTag gc11 = result.getSemanticTag(gc1.getSI());
//        Assert.assertNotNull(gc21);
//        Assert.assertNotNull(gc11);
//        L.d("Success: Both tags were found in the resulting STSet", this);
//
//    }
//
//        /**
//     * This test shall check if it is able to find a way between two concepts.
//     * The <code>FragmentationParameter</code> contains the depth, which represents the
//     * maximum distance that is allowed. If both concepts are within that distance,
//     * an STSet shall be returned, containing both concepts.
//     *
//     * In this case the depth is only 50km but the actual distance is around 390km
//     * therefore this test must fail!
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test(expected=SharkKBException.class)
//    public void testFindWayNegative() throws SharkKBException {
//        L.d("testFindWayNegative() started", this);
//        InternalGeoSTSetOld geo = new InMemoGeoSTSet();
//        // create concepts not so close to each other
//        InternalGeoSemanticTagOld gc1 = geo.createGeoSemanticTag(52.040719,8.595638);
//        InternalGeoSemanticTagOld gc2 = geo.createGeoSemanticTag(52.458785,13.523952);
//        L.d("Created concepts in original STSet", this);
//
//        FragmentationParameter otp = new FragmentationParameter(true, true, 50000);
//        InternalROSTSet result = geo.findWay(gc1, gc2, otp);
//        Assert.assertNotNull(result);
//        L.d("Success: Returned STSet is not null.", this);
//
//        // Provoke Exception by trying to retrieve concepts from the supposedly empty STSet
//        ROSemanticTag gc21 = result.getSemanticTag(gc2.getSI());
//        ROSemanticTag gc11 = result.getSemanticTag(gc1.getSI());
//    }
//
//    /**
//     * This test shall try to merge two GeoOntologies into one
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test
//    public void testMerge() throws SharkKBException {
//        L.d("testMerge() started", this);
//
//        InternalGeoSTSetOld geo1 = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld gc1 = geo1.createGeoSemanticTag(52.040719,8.595638);
//        InternalGeoSemanticTagOld gc2 = geo1.createGeoSemanticTag(52.458785,13.523952);
//        L.d("Created first STSet + Tags", this);
//
//        InternalGeoSTSetOld geo2 = new InMemoGeoSTSet();
//        InternalGeoSemanticTagOld gc3 = geo1.createGeoSemanticTag(52.1,8.6);
//        InternalGeoSemanticTagOld gc4 = geo1.createGeoSemanticTag(52.2,8.7);
//        L.d("Created second STSet + Tags", this);
//
//        geo1.merge(geo2);
//        L.d("Called merge on first STSet", this);
//        // now geo1 should containt the union of geo1 and geo2
//
//        geo1.getSemanticTag(gc1.getSI());
//        geo1.getSemanticTag(gc2.getSI());
//        geo1.getGeoSemanticTag(gc3.getSI());
//        geo1.getSemanticTag(gc4.getSI());
//        L.d("Success: All concepts found in geo1", this);
//    }
//
//    /**
//     * Try to fragment a <code>GeoSTSet</code> using ANY as an anchor to return a
//     * copy of the actual STSet. Check for correct type and check for the presence
//     * of all concepts. Associations need not be tested because the are implicitly
//     * present, depending on the coordinates of the single concepts.
//     *
//     *
//     * @throws net.sharkfw.knowledgeBase.SharkKBException
//     */
//    @Test
//    public void testFragmentWithAny() throws SharkKBException {
//        L.d("testFragmentWithAny() started.", this);
//        InternalGeoSTSetOld go = new InMemoGeoSTSet();
//
//        InternalGeoSemanticTagOld gc1 = go.createGeoSemanticTag(52.040719,8.595638);
//        InternalGeoSemanticTagOld gc2 = go.createGeoSemanticTag(52.458785,13.523952);
//        InternalGeoSemanticTagOld gc3 = go.createGeoSemanticTag(52.1,8.6);
//        InternalGeoSemanticTagOld gc4 = go.createGeoSemanticTag(52.2,8.7);
//
//        Vector anchors = new Vector();
//        anchors.add(ContextSpace.ANYURL);
//        L.d("Created tags and set anchor", this);
//
//        InternalROSTSet fragment = go.fragment(anchors.elements(), 1.0);
//        Assert.assertNotNull(fragment);
//        Assert.assertTrue(fragment instanceof InternalGeoSTSetOld);
//        L.d("fragment() successfull, returntype of fragment is GeoSTSet", this);
//
//        // check for presence of all concepts
//        InternalROGeoSTSetOld result = (InternalROGeoSTSetOld) fragment;
//        result.getGeoSemanticTag(gc1.getLatitude(), gc1.getLongitude());
//        result.getGeoSemanticTag(gc2.getLatitude(), gc2.getLongitude());
//        result.getGeoSemanticTag(gc3.getLatitude(), gc3.getLongitude());
//        result.getGeoSemanticTag(gc4.getLatitude(), gc4.getLongitude());
//        L.d("All tags present in fragment", this);
//
//    }
//
//        @Test
//        public void testNormalFragmentWithExactMatch() throws SharkKBException
//        {
//            InMemoGeoSTSet instance = new InMemoGeoSTSet();
//            InternalGeoSemanticTagOld gc1 = instance.createGeoSemanticTag(10.00001,10.00001);
//            //GeoSemanticTag gc1 = instance.createGeoSemanticTag(10.0,10.0); <- doens't work
//
//            Vector v = new Vector();
//            v.add(gc1.getSI()[0]);
//            InternalROSTSet stSet = instance.fragment(v.elements(), 0);
//            SemanticTag oc = stSet.getSemanticTag(gc1.getSI());
//            Assert.assertNotNull(oc);
//
//        }
//
//        @Test
//        public void testANYConceptCreation() throws SharkKBException {
//          InternalGeoSTSetOld set = new InMemoGeoSTSet();
//          InternalGeoSemanticTagOld tag = (InternalGeoSemanticTagOld) set.createSemanticTag(ContextSpace.ANY, ContextSpace.ANYSI);
//          double lat = tag.getLatitude();
//          double lon = tag.getLongitude();
//
//          Assert.assertTrue(lat != 0.0);
//          Assert.assertTrue(lon != 0.0);
//
//          Assert.assertTrue(lat == 1000.0);
//          Assert.assertTrue(lon == 1000.0);
//        }
//}