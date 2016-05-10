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
    
    private static FPSet zeroSet = null;
    
    /**
     * Returns a set which has no constraints in neither dimension
     * @return 
     */
    public static FPSet getZeroFPSet() {
        if(FPSet.zeroSet == null) {
            FPSet.zeroSet = new FPSet();
            for(int i = 0; i < ASIPSpace.MAXDIMENSIONS; i++) {
                FPSet.zeroSet.setFP(FragmentationParameter.getZeroFP(), i);
            }
        }
        
        return FPSet.zeroSet;
    }

    FPSet(FragmentationParameter topicsFP, FragmentationParameter typesFP, 
            FragmentationParameter approversFP, 
            FragmentationParameter sendersFP, 
            FragmentationParameter receiversFP, 
            FragmentationParameter timesFP, 
            FragmentationParameter locationsFP, 
            FragmentationParameter directionFP) {
        
        FPSet fpSet = new FPSet();
        
        fpSet.setFP(topicsFP, ASIPSpace.DIM_TOPIC);
        fpSet.setFP(typesFP, ASIPSpace.DIM_TYPE);
        fpSet.setFP(approversFP, ASIPSpace.DIM_APPROVERS);
        fpSet.setFP(sendersFP, ASIPSpace.DIM_SENDER);
        fpSet.setFP(receiversFP, ASIPSpace.DIM_RECEIVER);
        fpSet.setFP(timesFP, ASIPSpace.DIM_TIME);
        fpSet.setFP(locationsFP, ASIPSpace.DIM_LOCATION);
        fpSet.setFP(directionFP, ASIPSpace.DIM_DIRECTION);
    }
    
    private int dimension2index(int dim) {
        switch(dim) {
            case ASIPSpace.DIM_TOPIC: return 0;
            case ASIPSpace.DIM_TYPE: return 1;
            case ASIPSpace.DIM_APPROVERS: return 2;
            case ASIPSpace.DIM_SENDER: return 3;
            case ASIPSpace.DIM_RECEIVER: return 4;
            case ASIPSpace.DIM_TIME: return 5;
            case ASIPSpace.DIM_LOCATION: return 6;
        }

        // direction than
        return 7;
    }

    private void setFP(FragmentationParameter fp, int dimension) {
        int index = this.dimension2index(dimension);
        fps[index] = fp;
    }

    /**
     * @param dimension
     * @return fragmentation parameter of requested dimension. Method
     * return fragmentation parameters in any case.
     */
    public FragmentationParameter getFP(int dimension) {
        int index = this.dimension2index(dimension);
        
        if(this.fps[index] == null) {
            return FragmentationParameter.getZeroFP();
        } 
        
        return this.fps[index];
    }
}
