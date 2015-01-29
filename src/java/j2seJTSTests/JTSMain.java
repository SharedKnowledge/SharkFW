
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import static net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra.identical;
import static net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra.isIn;

/**
 *
 * @author s0542709, s0542541
 * @version 1.0
 *
 * This class provides some tests for SharkGeometry which uses the JTS-Library.
 * Hint: WKT = Well-known text; SRS = Spatial Reference System; SRID = Spatial
 * Reference System Identifier; SRS == SRID; WKT with SRS/SRID = Extended
 * Well-Known Text (EWKT);
 */
public class JTSMain {

    //Spatial Reference System = 4326 == WGS84
    static String srs = "4326";
    int srs_int = Integer.parseInt(srs);
    static String string_Point_Berlin_HTW_WH_G, string_Point_Berlin_HTW_TA,
            string_LineString_Berlin_HTW_WH_G, string_LineString_Berlin_HTW_WH_Complete,
            string_Polygon_Berlin_HTW_WH_G, string_Polygon_Berlin_HTW_WH_Complete,
            string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G, string_Multipoint_Berlin_HTW_WH_G,
            string_Multipoint_Berlin_HTW_WH_Complete, string_Multilinestring_Berlin_HTW_WH_G,
            string_Multilinestring_Berlin_HTW_WH_Complete;

    static SharkGeometry Point_Berlin_HTW_WH_G, Point_Berlin_HTW_WH_G_EWKT,
            Point_Berlin_HTW_TA,
            LineString_Berlin_HTW_WH_G, LineString_Berlin_HTW_WH_G_EWKT,
            LineString_Berlin_HTW_WH_Complete, LineString_Berlin_HTW_WH_Complete_EWKT,
            Polygon_Berlin_HTW_WH_G, Polygon_Berlin_HTW_WH_G_EWKT,
            Polygon_Berlin_HTW_WH_Complete, Polygon_Berlin_HTW_WH_Complete_EWKT,
            Polygon_Berlin_HTW_WH_Complete_WITHOUT_G, Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT,
            Multipoint_Berlin_HTW_WH_G, Multipoint_Berlin_HTW_WH_G_EWKT, Multipoint_Berlin_HTW_WH_Complete,
            Multilinestring_Berlin_HTW_WH_G, Multilinestring_Berlin_HTW_WH_G_EWKT,
            Multilinestring_Berlin_HTW_WH_Complete, Multilinestring_Berlin_HTW_WH_Complete_EWKT;

    @BeforeClass
    public static void setUpClass() throws SharkKBException {
        //Geometries with geographical coordinates 
        string_Point_Berlin_HTW_WH_G = "POINT (52.45606650054853 13.523988202214241)";
        string_Point_Berlin_HTW_TA = "POINT (52.49363039643433 13.522881120443344)";
        string_LineString_Berlin_HTW_WH_G = "LINESTRING (52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865)";
        string_LineString_Berlin_HTW_WH_Complete = "LINESTRING (52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942,"
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, 52.45860862010212 13.526121228933334)";
        string_Polygon_Berlin_HTW_WH_G = "POLYGON((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        string_Polygon_Berlin_HTW_WH_Complete = "POLYGON((52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942, "
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, 52.45860862010212 13.526121228933334))";
        string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G = "POLYGON ((52.45860862010212 13.526121228933334, "
                + "52.45684499901483 13.528932183980942, 52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, "
                + "52.45860862010212 13.526121228933334), (52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865, "
                + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241))";
        string_Multipoint_Berlin_HTW_WH_G = "MULTIPOINT ((52.45606650054853 13.523988202214241), (52.45549525426796 13.525406420230865),"
                + "(52.45515160397337 13.525016158819199), (52.45582827785886 13.523717299103737))";
        string_Multipoint_Berlin_HTW_WH_Complete = "MULTIPOINT (52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942, "
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178)";
        string_Multilinestring_Berlin_HTW_WH_G = "MULTILINESTRING ((52.45549525426796 13.525406420230865, 52.45606650054853 13.523988202214241,"
                + "52.45582827785886 13.523717299103737, 52.45515160397337 13.525016158819199, 52.45549525426796 13.525406420230865))";
        string_Multilinestring_Berlin_HTW_WH_Complete = "MULTILINESTRING ((52.45860862010212 13.526121228933334, 52.45684499901483 13.528932183980942,"
                + "52.45441290345384 13.526121228933334, 52.45586433148305 13.523031324148178, 52.45860862010212 13.526121228933334))";

        Point_Berlin_HTW_WH_G = InMemoSharkGeometry.createGeomByWKT(string_Point_Berlin_HTW_WH_G);
        Point_Berlin_HTW_WH_G_EWKT = InMemoSharkGeometry.createGeomByEWKT("SRID=" + srs + "; " + string_Point_Berlin_HTW_WH_G);
        Point_Berlin_HTW_TA = InMemoSharkGeometry.createGeomByWKT(string_Point_Berlin_HTW_TA);
        LineString_Berlin_HTW_WH_G = InMemoSharkGeometry.createGeomByWKT(string_LineString_Berlin_HTW_WH_G);
        LineString_Berlin_HTW_WH_G_EWKT = InMemoSharkGeometry.createGeomByEWKT("SRID=" + srs + "; " + string_LineString_Berlin_HTW_WH_G);
        LineString_Berlin_HTW_WH_Complete = InMemoSharkGeometry.createGeomByWKT(string_LineString_Berlin_HTW_WH_Complete);
        LineString_Berlin_HTW_WH_Complete_EWKT = InMemoSharkGeometry.createGeomByEWKT(string_LineString_Berlin_HTW_WH_Complete + "; SRID=" + srs);
        Polygon_Berlin_HTW_WH_G = InMemoSharkGeometry.createGeomByWKT(string_Polygon_Berlin_HTW_WH_G);
        Polygon_Berlin_HTW_WH_G_EWKT = InMemoSharkGeometry.createGeomByEWKT("SRID=" + srs + "; " + string_Polygon_Berlin_HTW_WH_G);
        Polygon_Berlin_HTW_WH_Complete = InMemoSharkGeometry.createGeomByWKT(string_Polygon_Berlin_HTW_WH_Complete);
        Polygon_Berlin_HTW_WH_Complete_EWKT = InMemoSharkGeometry.createGeomByEWKT(string_Polygon_Berlin_HTW_WH_Complete + "; SRID=" + srs);
        Polygon_Berlin_HTW_WH_Complete_WITHOUT_G = InMemoSharkGeometry.createGeomByWKT(string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G);
        Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT = InMemoSharkGeometry.createGeomByEWKT(string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G + "; SRID=" + srs);
        Multipoint_Berlin_HTW_WH_G = InMemoSharkGeometry.createGeomByWKT(string_Multipoint_Berlin_HTW_WH_G);
        Multipoint_Berlin_HTW_WH_G_EWKT = InMemoSharkGeometry.createGeomByEWKT("SRID=" + srs + "; " + string_Multipoint_Berlin_HTW_WH_G);
        Multipoint_Berlin_HTW_WH_Complete = InMemoSharkGeometry.createGeomByWKT(string_Multipoint_Berlin_HTW_WH_Complete);
        Multilinestring_Berlin_HTW_WH_G = InMemoSharkGeometry.createGeomByWKT(string_Multilinestring_Berlin_HTW_WH_G);
        Multilinestring_Berlin_HTW_WH_G_EWKT = InMemoSharkGeometry.createGeomByEWKT(string_Multilinestring_Berlin_HTW_WH_G + "; SRID=" + srs);
        Multilinestring_Berlin_HTW_WH_Complete = InMemoSharkGeometry.createGeomByWKT(string_Multilinestring_Berlin_HTW_WH_Complete);
        Multilinestring_Berlin_HTW_WH_Complete_EWKT = InMemoSharkGeometry.createGeomByEWKT(string_Multilinestring_Berlin_HTW_WH_Complete + "; SRID=" + srs);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /*@Test
    public void createAnyTag() throws SharkKBException {
        SpatialSemanticTag any = InMemoSharkKB.createInMemoSpatialSemanticTag(null);
        Assert.assertEquals(true, identical(any, any));
        Assert.assertEquals(true, isIn(any, any));
    }*/

    @Test
    public void createdSharkGeometryByGeographicalWKT_Point() throws SharkKBException {
        Assert.assertEquals(string_Point_Berlin_HTW_WH_G, Point_Berlin_HTW_WH_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_Point() throws SharkKBException {
        Assert.assertEquals(string_Point_Berlin_HTW_WH_G, Point_Berlin_HTW_WH_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, Point_Berlin_HTW_WH_G_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_LINESTRING() throws SharkKBException {
        Assert.assertEquals(string_LineString_Berlin_HTW_WH_G, LineString_Berlin_HTW_WH_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_LINESTRING() throws SharkKBException {
        Assert.assertEquals(string_LineString_Berlin_HTW_WH_G, LineString_Berlin_HTW_WH_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, LineString_Berlin_HTW_WH_G_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_LINESTRING_2() throws SharkKBException {
        Assert.assertEquals(string_LineString_Berlin_HTW_WH_Complete, LineString_Berlin_HTW_WH_Complete.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_LINESTRING_2() throws SharkKBException {
        Assert.assertEquals(string_LineString_Berlin_HTW_WH_Complete, LineString_Berlin_HTW_WH_Complete_EWKT.getWKT());
        Assert.assertEquals(srs_int, LineString_Berlin_HTW_WH_Complete_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_POLYGON() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_G, Polygon_Berlin_HTW_WH_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_POLYGON() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_G, Polygon_Berlin_HTW_WH_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, Polygon_Berlin_HTW_WH_G_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_POLYGON_2() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_Complete, Polygon_Berlin_HTW_WH_Complete.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_POLYGON_2() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_Complete, Polygon_Berlin_HTW_WH_Complete_EWKT.getWKT());
        Assert.assertEquals(srs_int, Polygon_Berlin_HTW_WH_Complete_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_POLYGON_3() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G, Polygon_Berlin_HTW_WH_Complete_WITHOUT_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_POLYGON_3() throws SharkKBException {
        Assert.assertEquals(string_Polygon_Berlin_HTW_WH_Complete_WITHOUT_G, Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_MULTIPOINT() throws SharkKBException {
        Assert.assertEquals(string_Multipoint_Berlin_HTW_WH_G, Multipoint_Berlin_HTW_WH_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_MULTIPOINT() throws SharkKBException {
        Assert.assertEquals(string_Multipoint_Berlin_HTW_WH_G, Multipoint_Berlin_HTW_WH_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, Multipoint_Berlin_HTW_WH_G_EWKT.getSRS());
    }

    @Test
    public void createdSharkGeometryByGeographicalWKT_MULTILINESTRING() throws SharkKBException {
        Assert.assertEquals(string_Multilinestring_Berlin_HTW_WH_G, Multilinestring_Berlin_HTW_WH_G.getWKT());
    }

    @Test
    public void createdSharkGeometryByGeographicalEWKT_MULTILINESTRING() throws SharkKBException {
        Assert.assertEquals(string_Multilinestring_Berlin_HTW_WH_G, Multilinestring_Berlin_HTW_WH_G_EWKT.getWKT());
        Assert.assertEquals(srs_int, Multilinestring_Berlin_HTW_WH_G_EWKT.getSRS());
    }

    /*

     @Test
     public void createSharkGeometryByGeographicalWKT_MULTIPOLYGON() throws SharkKBException {
     String testWKT = "MULTIPOLYGON ("
     + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
     + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
     + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
     + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
     SharkGeometry geom1 = InMemoGeometry.createGeomByWKT(testWKT);
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
     SharkGeometry geom1 = InMemoGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
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
     SharkGeometry geom1 = InMemoGeometry.createGeomByWKT(testWKT);
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
     SharkGeometry geom1 = InMemoGeometry.createGeomByEWKT("SRID=" + testSRS + "; " + testWKT);
     Assert.assertEquals(testWKT, geom1.getWKT());
     Assert.assertEquals(testSRS, geom1.getSRS());
     }*/
    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POINTS() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Point_Berlin_HTW_WH_G);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POINTS_2_False() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Point_Berlin_HTW_WH_G_EWKT);
        SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(Point_Berlin_HTW_TA);
        Assert.assertEquals(false, identical(tag1, tag2));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_LINESTRING() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(LineString_Berlin_HTW_WH_G);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_LINESTRING_2_False() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(LineString_Berlin_HTW_WH_G_EWKT);
        SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(LineString_Berlin_HTW_WH_Complete_EWKT);
        Assert.assertEquals(false, identical(tag1, tag2));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POLYGON() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Polygon_Berlin_HTW_WH_G);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POLYGON_2() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_POLYGON_3_False() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Polygon_Berlin_HTW_WH_Complete_EWKT);
        SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(Polygon_Berlin_HTW_WH_Complete_WITHOUT_G_EWKT);
        Assert.assertEquals(false, identical(tag1, tag2));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_MULTIPOINTS() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Multipoint_Berlin_HTW_WH_G);
        identical(tag1, tag1);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_MULTIPOINTS_2() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Multipoint_Berlin_HTW_WH_G_EWKT);
        Assert.assertEquals(true, identical(tag1, tag1));
    }

    @Test
    public void checkSpatialAlgebraIdenticalSemanticTags_MULTIPOINTS_3_False() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Multipoint_Berlin_HTW_WH_G_EWKT);
        SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(Multipoint_Berlin_HTW_WH_Complete);
        Assert.assertEquals(false, identical(tag1, tag2));
    }

    /*
     @Test
     public void checkSpatialAlgebraIdenticalSemanticTags_MULTIPOLYGON() throws SharkKBException {
     String testWKT = "MULTIPOLYGON ("
     + "((52.49363039643433 13.522881120443344, 52.49429668066099 13.525370210409164,"
     + "52.49203649908915 13.526872247457504, 52.49220634335951 13.522559255361557, 52.49363039643433 13.522881120443344)),"
     + "((52.45606650054853 13.523988202214241, 52.45549525426796 13.525406420230865,"
     + "52.45515160397337 13.525016158819199, 52.45582827785886 13.523717299103737, 52.45606650054853 13.523988202214241)))";
     SharkGeometry geom1 = InMemoGeometry.createGeomByWKT(testWKT);
     SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
     // TODO geom1.is
     Assert.assertEquals(true, tag1.identical(tag1));
     }

     @Test
     public void checkSpatialAlgebraIdenticalSemanticTags_GEOMETRYCOLLECTION() throws SharkKBException {
     String testWKT = "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))";
     SharkGeometry geom1 = InMemoGeometry.createGeomByWKT(testWKT);
     SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
     // TODO geom1.is
     Assert.assertEquals(true, tag1.identical(tag1));
     }

     @Test
     public void checkSpatialAlgebraIdenticalSemanticTags_GEOMETRYCOLLECTION_TAG_False() throws SharkKBException {
     String testWKT1 = "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))";
     String testWKT2 = "POINT (52.45606650054853 13.523988202214241)";
     SharkGeometry geom1 = InMemoGeometry.createGeomByWKT(testWKT1);
     SharkGeometry geom2 = InMemoGeometry.createGeomByWKT(testWKT2);
     SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom1);
     SpatialSemanticTag tag2 = InMemoSharkKB.createInMemoSpatialSemanticTag(geom2);
     // TODO geom1.is
     Assert.assertEquals(false, tag1.identical(tag2));
     }*/
    @Test
    public void checkSpatialAlgebraIsInSemanticTags_POINTS() throws SharkKBException {
        SpatialSemanticTag tag1 = InMemoSharkKB.createInMemoSpatialSemanticTag(Point_Berlin_HTW_WH_G);
        Assert.assertEquals(true, isIn(tag1, tag1));
    }

}
