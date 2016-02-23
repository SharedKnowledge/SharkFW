package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.ASIPSpace;

/**
 * Sets of fragmentation parameters are used to calculate in
 * an information space.
 * 
 * @author thsc
 */
public class FPSet {
    private final FragmentationParameter[] fps = 
            new FragmentationParameter[ASIPSpace.MAXDIMENSIONS];
    
    private FPSet() {}
    
    /**
     * Returns a set which has no constraints in neither dimension
     * @return 
     */
    public static FPSet getZeroFPSet() {
        FPSet fpSet = new FPSet();

        for(int i = 0; i < ASIPSpace.MAXDIMENSIONS; i++) {
            fpSet.setFP(FragmentationParameter.getZeroFP(), i);
        }
        
        return fpSet;
    }

    private void setFP(FragmentationParameter fp, int i) {
        fps[i] = fp;
    }
    
    public FragmentationParameter getFP(int dimension) {
        return this.fps[dimension];
    }
}
