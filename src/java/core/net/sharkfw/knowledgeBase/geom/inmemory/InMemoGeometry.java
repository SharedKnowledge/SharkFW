package net.sharkfw.knowledgeBase.geom.inmemory;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.geom.Geometry;

/**
 *
 * @author thsc
 */
public class InMemoGeometry implements Geometry {
    private String ewkt;
    
    private InMemoGeometry(String ewkt) {
        this.ewkt = ewkt;
    }

    /**
     * 
     * @param wkt
     * @return
     * @throws SharkKBException wrong format
     */
    public static Geometry createGeomByWKT(String wkt) throws SharkKBException {
        // TODO: add default SRS!
        return new InMemoGeometry(wkt);
    }

    /**
     * 
     * @param ewkt
     * @return
     * @throws SharkKBException wrong format
     */
    public static Geometry createGeomByEWKT(String ewkt) throws SharkKBException {
        // TODO: Test format!
        return new InMemoGeometry(ewkt);
    }
    
//    public static Point createPoint(Double lon, Double lat) {
//        return new InMemoPoint(lon, lat);
//    }
//
    public static String createSpatialSI(Geometry geom) {
        return Geometry.SHARK_POINT_SI_PREFIX + geom.getEWKT();
    }
//
//    public static String createSpatialSI(String longitude, String latitude) {
//        return InMemoPoint.SHARK_POINT_SI_PREFIX + longitude + "/" + latitude;
//    }

    @Override
    public String getWKT() {
        // TODO - withdraw leading srs from string
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEWKT() {
        return this.getEWKT();
    }
}