package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author thsc
 */
public abstract class InMemoSharkCS implements SharkCS {
    
    protected InMemoSharkCS() {
        
    }
    
    @Override
    public STSet getSTSet(int dim) throws SharkKBException {
        switch(dim) {
            case SharkCS.DIM_TOPIC : 
                return this.getTopics();
                
            case SharkCS.DIM_ORIGINATOR : 
                return this.getOriginators();
                        
            case SharkCS.DIM_PEER : 
                return this.getPeers();
                
            case SharkCS.DIM_REMOTEPEER : 
                return this.getRemotePeers();
                
            case SharkCS.DIM_TIME :
                return this.getTimes();
                
            case SharkCS.DIM_LOCATION : 
                return this.getLocations();
                
            case SharkCS.DIM_DIRECTION : 
                return this.getDirections();
        }
        
        throw new SharkKBException("unknown dimension in Shark Context Space: " + dim);
    }
    
    private InMemoPeerSTSet originators = null;
    private PeerSTSet getOriginators() {
        if(this.originators != null) return this.originators;
        
        if(this.getOriginator() == null) return null;
        
        this.originators = new InMemoPeerSTSet();
        try {
            this.originators.add(this.getOriginator());
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.originators;
    }
    
    private InMemoSTSet directions = null;
    private STSet getDirections() {
        if(this.directions != null) return this.directions;
        
        this.directions = new InMemoSTSet();
        
        String name, si;

        switch(this.getDirection()) {
            case SharkCS.DIRECTION_IN: {
                name = "direction in";
                si = SharkCS.INURL;
                break;
            }
                
            case SharkCS.DIRECTION_OUT: {
                name = "direction out";
                si = SharkCS.OUTURL;
                break;
            }
                
            case SharkCS.DIRECTION_INOUT: {
                name = "direction in out";
                si = SharkCS.INOUTURL;
                break;
            }
                
            default: {
                name = "no direction";
                si = SharkCS.NO_DIRECTION_URL;
            }
        }
        
        
        SemanticTag directionTag = InMemoSharkKB.createInMemoSemanticTag(name, new String[]{si});
        
        try {
            this.directions.add(this.getOriginator());
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.directions;
    }
}
