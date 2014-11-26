package net.sharkfw.knowledgeBase.geom.inmemory;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author thsc
 */
public class InMemoGeometry implements SharkGeometry {

    // TODO: Test format!!!
    //Well-Known Text
    private String wkt;
    //epsg code of spatial reference system
    private int srs;

    private InMemoGeometry(String wkt, int srs) {
        this.wkt = wkt;
        this.srs = srs;
    }

    /**
     *
     * @param wkt
     * @return InMemoGeometry
     * @throws SharkKBException wrong format
     */
    public static SharkGeometry createGeomByWKT(String wkt) throws SharkKBException {
        // TODO: add default SRS!
        String validErr = null;

        // empty geometries are always valid!
        if (wkt.isEmpty()) {
            throw new SharkKBException("WKT is empty");
        } else {
            
        }
        return new InMemoGeometry(wkt, 4326);
    }

    /**
     *
     * @param ewkt
     * @return
     * @throws SharkKBException wrong format
     */
    public static SharkGeometry createGeomByEWKT(String ewkt) throws SharkKBException {
        String wkt = null;
        int srs = 0;

        if (ewkt.isEmpty()) {
            throw new SharkKBException("WKT is empty");
        } else {
            
        }
        return new InMemoGeometry(wkt, srs);
    }

    /**
     *
     * @param geom
     * @return
     */
    public static String createSpatialSI(SharkGeometry geom) {
        return SharkGeometry.SHARK_POINT_SI_PREFIX + geom.getEWKT();
    }

    /**
     *
     * @return
     */
    @Override
    public String getWKT() {
        return this.wkt;
    }

    /**
     *
     * @return
     */
    @Override
    public String getEWKT() {
        return ("SRID=" + getSRS() + ";" + getWKT());
    }

    /**
     *
     * @return
     */
    @Override
    public int getSRS() {
        return this.srs;
    }
    
    
    //    public static Point createPoint(Double lon, Double lat) {
    //        return new InMemoPoint(lon, lat);
    //    }
    //
    //
    //    public static String createSpatialSI(String longitude, String latitude) {
    //        return InMemoPoint.SHARK_POINT_SI_PREFIX + longitude + "/" + latitude;
    //    }
}
