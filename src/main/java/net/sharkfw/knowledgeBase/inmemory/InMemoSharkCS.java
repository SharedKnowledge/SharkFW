package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * Shark 3.0 is going to support LASP instead of KEP. Using 
 * SharkCS becomes depreated. That class is the root of 
 * all context space implementations and will support both
 * protcols for a while, propably until version Shark 4.
 * @author thsc
 */
public abstract class InMemoSharkCS implements SharkCS, ASIPSpace {

    /* That's a technical mapping not a semantical. E.g.
    approver is actually an originator from a semantically
    perspective. Cardinality of approvers fits to peer, though.
    
    WASP        KEP
    topic       topic
    type        --
    approver    peer
    sender      originator
    receiver    remotePeer
    location    location
    time        time
    direction   direction
    */
    
    // flag if this object acts as KEP or LASP kepInterest
    private final boolean isASIP;
    
    boolean isASIPInterest() {
        return this.isASIP;
    }
    
    protected InMemoSharkCS() {
        this.isASIP = false; // is a KEP object
    }
    
    protected InMemoSharkCS(boolean isLASP) {
        this.isASIP = isLASP; // can be KEP or LASP
    }
    
    @Override
    public STSet getSTSet(int dim) throws SharkKBException {
        switch(dim) {
            // KEP
//            case ASIPSpace.DIM_TOPIC:
//                return this.getTopics();
//                
//            // see comments
//            case ASIPSpace.DIM_ORIGINATOR:
//                return this.getOriginators();
//                                        
//            case ASIPSpace.DIM_PEER:
//                return this.getPeers();
//                
//            case ASIPSpace.DIM_REMOTEPEER:
//                return this.getRemotePeers();
//                
//            case ASIPSpace.DIM_TIME:
//                return this.getTimes();
//                
//            case ASIPSpace.DIM_LOCATION:
//                return this.getLocations();
//                
//            case ASIPSpace.DIM_DIRECTION:
//                return this.getDirections();

            ////////////////////////////////////////////////////////
            //                      ASIP                          //
            ////////////////////////////////////////////////////////
                
            case ASIPSpace.DIM_TOPIC:
                return this.getTopics();
                
            case ASIPSpace.DIM_TYPE : 
                return this.getTypes();
                
            case ASIPSpace.DIM_APPROVERS : 
                return this.getApprovers();
                
            case ASIPSpace.DIM_SENDER : 
                return this.getSenders();
                
            case ASIPSpace.DIM_RECEIVER : 
                return this.getReceivers();
                
            case ASIPSpace.DIM_TIME : 
                return this.getTimes();
                
            case ASIPSpace.DIM_LOCATION : 
                return this.getLocations();
                
            case ASIPSpace.DIM_DIRECTION : 
                return this.getLocations();
        }
        
        throw new SharkKBException("unknown dimension in Shark Context Space: " + dim);
    }
    
    /* sender has cardinality of 0..1
      sometime we need it as peer semantic tag set
    We keep that structure here: viola
    */
    private InMemoPeerSTSet senders = null;
    private PeerSTSet getSenders() {
        if(this.senders != null) return this.senders;
        
        if(this.getSender() == null) return null;
        
        this.senders = new InMemoPeerSTSet();
        try {
            this.senders.add(this.getOriginator());
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.senders;
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
            case ASIPSpace.DIRECTION_IN: {
                name = "direction in";
                si = SharkCS.INURL;
                break;
            }
                
            case ASIPSpace.DIRECTION_OUT: {
                name = "direction out";
                si = SharkCS.OUTURL;
                break;
            }
                
            case ASIPSpace.DIRECTION_INOUT: {
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
