package net.sharkfw.knowledgeBase.geom.jts;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 *
 * @author s0542709, s0542541
 * @version
 *
 *
 */
public class SpatialAlgebra {

    /**
     * checks with JTS-Library if Well-known text is valid
     *
     * @param wkt
     * @throws SharkKBException
     */
    public static void checkWKTwithJTS(String wkt) throws SharkKBException {
        WKTReader reader = new WKTReader();
        Geometry jts_Geometry;

        try {
            jts_Geometry = reader.read(wkt);
        } catch (ParseException ex) {
            throw new SharkKBException("WKT parsing problem");
        }

        IsValidOp validOp = new IsValidOp(jts_Geometry);
        if (!validOp.isValid()) {
            throw new SharkKBException("WKT is not valid");
        }
    }

    /**
     * checks with JTS-Library if two Shark-SpatialSemanticTags are identical
     *
     * @param a
     * @param b
     * @return true if both SpatialSemanticTags identical
     * @throws SharkKBException
     */
    public static boolean identical(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
        WKTReader reader = new WKTReader();
        Geometry jts_Geometry1 = null;
        Geometry jts_Geometry2 = null;

        // TODO check if GeometryCollection ...!!!
        try {
            jts_Geometry1 = reader.read(a.getGeometry().getWKT());
            jts_Geometry2 = reader.read(b.getGeometry().getWKT());
        } catch (ParseException ex) {
            throw new SharkKBException("WKT parsing problem");
        }
        return jts_Geometry2.equals(jts_Geometry1);
    }

    /**
     * checks with JTS-Library if a Shark-SpatialSTSet and a SpatialSemanticTag
     * are identical
     *
     * @param a
     * @param b
     * @return true if both SpatialSemanticTags identical
     * @throws SharkKBException
     */
    public static boolean identical(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        return false;
    }

    /**
     * checks with JTS-Library if two Shark-SpatialSTSets are identical
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean identical(SpatialSTSet a, SpatialSTSet b) {
        return false;
    }

    /**
     * checks if a SpatialSemanticTag is in a nother SpatialSemanticTag
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public static boolean isIn(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {

        // Was ist mit geometry collection!!!!
        WKTReader reader = new WKTReader();
        Geometry jts_Geometry1 = null;
        Geometry jts_Geometry2 = null;

        // TODO check if GeometryCollection ...!!!
        try {
            jts_Geometry1 = reader.read(a.getGeometry().getWKT());
            jts_Geometry2 = reader.read(b.getGeometry().getWKT());
        } catch (ParseException ex) {
            throw new SharkKBException("WKT parsing problem");
        }

        return jts_Geometry2.coveredBy(jts_Geometry1);
    }

    /**
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public static boolean isIn(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        Enumeration<SpatialSemanticTag> spatialSemanticTagEnumeration = a.spatialTags();
        WKTReader reader = new WKTReader();
        //Geometry[] jts_Geometry1 = new Geometry[a.number()];
        LinkedList jts_Geometry1 = new LinkedList();
        Geometry jts_Geometry2 = null;
        boolean is_in = false;

        try {
            jts_Geometry2 = reader.read(b.getGeometry().getWKT());
        } catch (ParseException ex) {
            throw new SharkKBException("WKT parsing problem with SpatialSemanticTag");
        }

        while (spatialSemanticTagEnumeration.hasMoreElements()) {
            Geometry tempGeometry;
            try {
                tempGeometry = reader.read(spatialSemanticTagEnumeration.nextElement().getGeometry().getWKT());
            } catch (ParseException ex) {
                throw new SharkKBException("WKT parsing problem with SpatialSTSet ");
            }

            // TODO check if GeometryCollection ...!!!
            // Wenn GeometryCollection dann überprüfe jede darin befindliche Geometry auf intersect and save them
            if (tempGeometry instanceof GeometryCollection) {
                for (int i = 0; i < tempGeometry.getLength(); i++) {
                    if (tempGeometry.getGeometryN(i).intersects(jts_Geometry2)) {
                        //Wenn die Teilgeometry == gesuchte, dann fertig
                        if (tempGeometry.getGeometryN(i).contains(jts_Geometry2)) {
                            return true;
                        } else {
                            jts_Geometry1.add(tempGeometry.getGeometryN(i));
                        }
                    }
                }
            } else {
                if (tempGeometry.intersects(jts_Geometry2)) {
                    //Wenn die Teilgeometry == gesuchte, dann fertig
                    if (tempGeometry.contains(jts_Geometry2)) {
                        return true;
                    } else {
                        jts_Geometry1.add(tempGeometry);
                    }
                }
            }

        }//end while

        //Ergebnis => Eine Liste von Elementen die sich in irgend einer Art brühren / schneiden
        if (jts_Geometry1.size() == 0) {
            return false;
        }

        //TODO detailierter überprüfen ob is in!!!
        return is_in;
    }

    /**
     * Geometry A lies in the interior of Geometry B
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isIn(SpatialSTSet a, SpatialSTSet b) {
        return false;
    }

    /**
     *
     * @param fragment
     * @param source
     * @param anchor
     * @return
     */
    public static SpatialSTSet fragment(SpatialSTSet fragment, SpatialSTSet source, SemanticTag anchor) {
        return null;
    }

}
