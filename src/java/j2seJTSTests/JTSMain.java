
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author s0542709, s0542541
 * @version
 *
 * This class provides some tests for SharkGeometry which uses the JTS-Library.
 * Hint: - Well-known text (WKT) with SRS = Extended Well-Known Text (EWKT) -
 * Spatial Reference System Identifier (SRID) == (SRS) Spatial Reference System
 */
public class JTSMain {

    @Test
    public void createSharkGeometryByGeographicalWKT_Point() throws SharkKBException {
        String testWKT = "POINT (52.45606650054853 13.523988202214241)";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_Point() throws SharkKBException {
        String testWKT = "POINT (52.45606650054853 13.523988202214241)";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_LINESTRING() throws SharkKBException {
        String testWKT = "LINESTRING (52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865)";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_LINESTRING() throws SharkKBException {
        String testWKT = "LINESTRING (52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865)";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_LINESTRING_SRS_atEnd() throws SharkKBException {
        String testWKT = "LINESTRING (52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865)";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT(testWKT + "; SRID=" + testSRS);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_POLYGON() throws SharkKBException {
        String testWKT = "POLYGON((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_POLYGON() throws SharkKBException {
        String testWKT = "POLYGON((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_POLYGON_2() throws SharkKBException {
        String testWKT = "POLYGON((52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942, "
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, 52.45860862010212 13.526121228933334),"
                + "(52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_POLYGON_2() throws SharkKBException {
        String testWKT = "POLYGON((52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942, "
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, 52.45860862010212 13.526121228933334),"
                + "(52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_MULTIPOINT() throws SharkKBException {
        String testWKT = "MULTIPOINT ((52.45606650054853 13.523988202214241), (52.45549525426796 13.525406420230865),"
                + "(52.45515160397337 13.525016158819199), (52.45582827785886 13.523717299103737), (52.45606650054853 13.523988202214241))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_MULTIPOINT() throws SharkKBException {
        String testWKT = "MULTIPOINT ((52.45606650054853 13.523988202214241), (52.45549525426796 13.525406420230865),"
                + "(52.45515160397337 13.525016158819199), (52.45582827785886 13.523717299103737), (52.45606650054853 13.523988202214241))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_MULTILINESTRING() throws SharkKBException {
        String testWKT = "MULTILINESTRING ((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865),"
                + "(52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_MULTILINESTRING() throws SharkKBException {
        String testWKT = "MULTILINESTRING ((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865),"
                + "(52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_MULTIPOLYGON() throws SharkKBException {
        String testWKT = "MULTIPOLYGON ("
                + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
                + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
                + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_MULTIPOLYGON() throws SharkKBException {
        String testWKT = "MULTIPOLYGON ("
                + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
                + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
                + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void createSharkGeometryByGeographicalWKT_GEOMETRYCOLLECTION() throws SharkKBException {
        String testWKT = "MULTIPOLYGON ("
                + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
                + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
                + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
    }

    @Test
    public void createSharkGeometryByGeographicalEWKT_GEOMETRYCOLLECTION() throws SharkKBException {
        String testWKT = "MULTIPOLYGON ("
                + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
                + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
                + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
        int testSRS = 4326;
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
        Assert.assertEquals(testWKT, geom1.getWKT());
        Assert.assertEquals(testSRS, geom1.getSRS());
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POINTS() throws SharkKBException {
        String testWKT = "POINT (52.45606650054853 13.523988202214241)";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
        // TODO geom1.is
        Assert.assertEquals(true, tag1.identical(tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_MULTIPOLYGON() throws SharkKBException {
        String testWKT = "MULTIPOLYGON ("
                + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
                + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
                + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
        // TODO geom1.is
        Assert.assertEquals(true, tag1.identical(tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_GEOMETRYCOLLECTION() throws SharkKBException {
        String testWKT = "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT);
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
        // TODO geom1.is
        Assert.assertEquals(true, tag1.identical(tag1));
    }
    
        @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_GEOMETRYCOLLECTION_TAG_False() throws SharkKBException {
        String testWKT1 = "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))";
        String testWKT2 = "POINT (52.45606650054853 13.523988202214241)";
        SharkGeometry geom1 = InMemoSharkGeometry.createGeomByWKT(testWKT1);
        SharkGeometry geom2 = InMemoSharkGeometry.createGeomByWKT(testWKT2);
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
        SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom2);
        // TODO geom1.is
        Assert.assertEquals(false, tag1.identical(tag2));
    }
    

// TODO add test methods here.
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
}
