package net.sharkfw.knowledgeBase.geom;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.system.L;

/**
 * That class is just a placeholder. It allows defining a spatial algebra in
 * SharkCSAlgebra. The actual implementation with J2SE is made by means of Java
 * Topology Suite.
 *
 * That class will be used on every device which can't or don't want to use JTS.
 *
 * @author thsc
 */
public class SpatialAlgebra {

    /**
     * Well-known text (WKT)
     *
     * @param wkt
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public boolean isValidWKT(String wkt) throws SharkKBException {
        //see InMemoSharkGeometry createGeomByWKT();
        return true;
    }

    /**
     * Extended Well-Known Text (EWKT)
     *
     * @param ewkt
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public boolean isValidEWKT(String ewkt) throws SharkKBException {
        //see InMemoSharkGeometry createGeomByEWKT();
        return true;
    }

    ////////////////////////////////////////////////////////////////////////
    //           very optimistic dummy implementation start here.         //
    ////////////////////////////////////////////////////////////////////////
    public boolean identical(SpatialSemanticTag sTagA, SpatialSemanticTag sTagB) throws SharkKBException {
        return true;
    }
}
