package net.sharkfw.knowledgeBase.geom;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.system.L;

/**
 * That class is just a placeholder. It allows 
 * defining a spatial algebra in SharkCSAlgebra.
 * The actual implementation with 
 * J2SE is made by means of Java Topology Suite.
 * 
 * That class will be used on every device which
 * can't or don't want to use JTS.
 * 
 * @author thsc
 */
public class SpatialAlgebra {
    
    private static SpatialAlgebra spatialAlgebra = null;
    
    public static SpatialAlgebra getSpatialAlgebra() {
        if(SpatialAlgebra.spatialAlgebra != null) {
            return SpatialAlgebra.spatialAlgebra;
        }
        
        try {
            Class spatialAlgebraClass = Class.forName("net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra");
            SpatialAlgebra.spatialAlgebra = (SpatialAlgebra) spatialAlgebraClass.newInstance();
            
        } catch (Exception ex) {
            // class cannot be found or instantiated
            L.d("cannot instanciate JTS spatial algebra - that platform probably does not support JTS. That's ok.", ex.getMessage());
        }
        
        if(SpatialAlgebra.spatialAlgebra == null) {
            // set up default dummy implementation
            SpatialAlgebra.spatialAlgebra = new SpatialAlgebra();
        }
        
        return SpatialAlgebra.spatialAlgebra;
    }

    ////////////////////////////////////////////////////////////////////////
    //           very optimistic dummy implementation start here.         //
    ////////////////////////////////////////////////////////////////////////
    
    public boolean identical(SpatialSemanticTag sTagA, SpatialSemanticTag sTagB) {
        return true;
    }
}
