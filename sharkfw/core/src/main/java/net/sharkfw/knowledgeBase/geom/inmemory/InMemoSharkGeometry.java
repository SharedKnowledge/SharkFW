package net.sharkfw.knowledgeBase.geom.inmemory;

import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.SpatialAlgebra;
//import net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra; //use this for JTS!

/**
 * *
 * @author thsc, s0542709, s0542541
 * @version 1.0
 *
 * This class provides some tests for SharkGeometry which uses the JTS-Library.
 * Hint: - Well-known text (WKT) with SRS = Extended Well-Known Text (EWKT) -
 * Spatial Reference System Identifier (SRID) == (SRS) Spatial Reference System
 * default value = 4326 == WGS84; SRS bis 8 stellen
 * http://spatialreference.org/ref/epsg/?page=88
 *
 */
public class InMemoSharkGeometry implements SharkGeometry {

    private final String wkt;
    private final int srs;

    private InMemoSharkGeometry(String wkt, int srs) {
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
        wkt = wkt.replace(";", "").trim();
        SpatialAlgebra spatialAlgebra = SharkCSAlgebra.getSpatialAlgebra();
        
        if (!spatialAlgebra.isValidWKT(wkt)) {
            throw new SharkKBException("WKT not valid!");
        }
        return new InMemoSharkGeometry(wkt, 4326);
    }

    /**
     *
     * @param ewkt
     * @return
     * @throws SharkKBException wrong format
     */
    public static SharkGeometry createGeomByEWKT(String ewkt) throws SharkKBException {
        ewkt = ewkt.toUpperCase();
        String wkt = null;
        int srs = 0;

        try {
            int postionSRSstart = ewkt.indexOf("SRID");
            int positionSRSend = ewkt.indexOf(";", postionSRSstart);
            if (positionSRSend == -1) {
                positionSRSend = ewkt.length();
            }
            srs = Integer.parseInt(ewkt.substring(postionSRSstart + 5, positionSRSend));
            wkt = (ewkt.substring(0, postionSRSstart)) + (ewkt.substring(positionSRSend, ewkt.length()));
            wkt = wkt.replace(";", "").trim();
            if (!SharkCSAlgebra.getSpatialAlgebra().isValidWKT(wkt)) {
                throw new SharkKBException("WKT not valid!");
            }
        } catch (Exception e) {
            throw new SharkKBException("SRID parsing problem, check syntax restriction ");
        }
        SharkCSAlgebra.getSpatialAlgebra().isValidEWKT(ewkt);
        return new InMemoSharkGeometry(wkt, srs);
    }

    /**
     *
     * @param geom
     * @return SpatialSI
     */
    public static String createSpatialSI(SharkGeometry geom) {
        return SharkGeometry.SHARK_POINT_SI_PREFIX + geom.getEWKT();
    }

    /**
     *
     * @return WKT
     */
    @Override
    public String getWKT() {
        return this.wkt;
    }

    /**
     *
     * @return EWKT
     */
    @Override
    public String getEWKT() {
        return ("SRID=" + getSRS() + ";" + getWKT());
    }

    /**
     *
     * @return SRS
     */
    @Override
    public int getSRS() {
        return this.srs;
    }

}
