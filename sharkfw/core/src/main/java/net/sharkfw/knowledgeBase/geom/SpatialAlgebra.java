package net.sharkfw.knowledgeBase.geom;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;

/**
 * That class is just a placeholder. It allows defining a spatial algebra in
 * SharkCSAlgebra. The actual implementation with J2SE is made by means of Java
 * Topology Suite (JTS).
 *
 * That class will be used on every device which can't or don't want to use JTS.
 *
 * @author thsc, Fabian Schmöker (s0542541), Tino Herrmann (s0542709)
 * @version 1.0
 */
public class SpatialAlgebra {

    /**
     * Checks if the passed Well-known text is valid.
     *
     * @param wkt     String which contains Well-known text
     * 
     * @throws SharkKBException   Is thrown if syntax of WKT is incorrect.
     * @return Returns true if semantics of WKT is valid - otherwise false.
     */
    public boolean isValidWKT(String wkt) throws SharkKBException {
        //see InMemoSharkGeometry createGeomByWKT();
        return true;
    }

    /**
     * Checks if the passed Extended Well-known text is valid.
     *
     * @param ewkt      String which contains Extended Well-known text
     * 
     * @throws SharkKBException   Is thrown if syntax of WKT (not EWKT!) is incorrect.
     * @return Returns true if semantics of WKT (not EWKT!) is valid - otherwise false.
     */
    public boolean isValidEWKT(String ewkt) throws SharkKBException {
        //see InMemoSharkGeometry createGeomByEWKT();
        return true;
    }

    ////////////////////////////////////////////////////////////////////////
    //           very optimistic dummy implementation start here.         //
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Checks if two Shark-SpatialSemanticTags
     * are identical. In that case, it checkes whether the geometries of both
     * passed SpatialSemanticTags are cover the same area.
     *
     * @param a     SpatialSemanticTag which is to be checked
     * @param b     SpatialSemanticTag which is to be checked
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if both SpatialSemanticTags are identical - otherwise false.
     */    
    public boolean identical(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
        return true;
    }

    /**
     * Checks if a Shark-SpatialSTSet and a
     * SpatialSemanticTag are identical. In that case, it checkes whether
     * the geometries of passed SpatialSemanticTag and SpatialSTSet are cover
     * the same area.
     * 
     * @param a     SpatialSemanticTag which is to be checked
     * @param b     SpatialSTSet which is to be checked
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if SpatialSemanticTag and SpatialSTSet are identical
     *         - otherwise false.
     */    
    public boolean identical(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        return true;
    }

    /**
     * Checks if two Shark-SpatialSTSets
     * are identical. In that case, it checkes whether the geometries of both
     * passed SpatialSTSets are cover the same area.
     *
     * @param a     SpatialSTSet which is to be checked
     * @param b     SpatialSTSetg which is to be checked
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if both SpatialSTSets are identical - otherwise false.
     */    
    public boolean identical(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
        return true;
    }

    /**
     * Checks if a SpatialSemanticTag is in 
     * another SpatialSemanticTag. In that case, it checkes whether the 
     * geometries of SpatialSemanticTag b are in the geometries of
     * SpatialSemantic a.
     * 
     * @param a     SpatialSemanticTag which is to be checked whether geometries
     *              are covered by geometries of SpatialSemanticTag b
     * @param b     SpatialSemanticTag which is to be checked whether geometries
     *              are cover geometries of SpatialSemanticTag a
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if SpatialSemanticTag b is in SpatialSemanticTag a
     *         - otherwise false.
     */    
    public boolean isIn(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
        return true;
    }

    /**
     * Checks if a SpatialSemanticTag is in an
     * SpatialSTSet. In that case, it checkes whether the geometries of
     * SpatialSemanticTag b are in the geometries of SpatialSTSet a.
     * 
     * @param a     SpatialSTSet which is to be checked whether geometries
     *              are covered by geometries of SpatialSemanticTag b
     * @param b     SpatialSemanticTag which is to be checked whether geometries
     *              are cover geometries of SpatialSTSet a
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if SpatialSemanticTag b is in SpatialSTSet a
     *         - otherwise false.
     */    
    public boolean isIn(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        return true;
    }

    /**
     * Checks if a SpatialSTSet is in another
     * SpatialSTSet. In that case, it checkes whether the geometries of
     * SpatialSTSet b are in the geometries of SpatialSTSet a.
     * 
     * @param a     SpatialSTSet which is to be checked whether geometries
     *              are covered by geometries of SpatialSTSet b
     * @param b     SpatialSTSet which is to be checked whether geometries
     *              are cover geometries of SpatialSTSet a
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns true if SpatialSTSet b is in SpatialSTSet a
     *         - otherwise false.
     */    
    public boolean isIn(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
        return true;
    }

    /**
     * Takes a fragment from a SpatialSTSet. In this
     * case, it takes a geometrie or geometrie collection as fragment from the
     * passed SpatialSTSet source which matches with geometrie of SpatialSemanticTag
     * anchor and merged it to SpatialSTSet fragment.
     *
     * @param fragment      SpatialSTSet which will merged with the matched
     *                      fragment geometrie
     * @param source        SpatialSTSet from which takes a fragment
     * @param anchor        SpatialSemanticTag which will searched for a match
     *                      to fragment
     * 
     * @throws SharkKBException   Is thrown if an error has occurred during proccessing.
     * @return Returns the result SpatialSTSet fragment. If the passed SpatialSTSet
     *         fragment was null, then an empty SpatialSTSet will returned.
     */    
    public SpatialSTSet fragment(SpatialSTSet fragment, SpatialSTSet source, SpatialSemanticTag anchor) throws SharkKBException {
        return null;
    }

}
