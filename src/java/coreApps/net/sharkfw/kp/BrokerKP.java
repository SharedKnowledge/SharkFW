package net.sharkfw.kp;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.InterestStore;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * A Broker - This KP simple collects all inserts and exposes it gets hold of,
 * and contextualizes them to all already known ones.
 * 
 * @author mfi
 */
public class BrokerKP extends KnowledgePort {

    private InterestStore inInterests;
    private InterestStore outInterests;
    
    private FragmentationParameter[] fps;
    private final PropertyHolder ph;
    
    public final static String RECEIVING_INTEREST_LIST = "HubKP_ReceivingInterests";
    public final static String SENDING_INTEREST_LIST = "HubKP_SendingInterests";
    
    /**
     * @param se
     * @param validSeconds interests are kept defined amount of seconds
     */
    public BrokerKP(SharkEngine se, int validSeconds) {
        this(se, null, validSeconds);
    }
    
    public BrokerKP(SharkEngine se, PropertyHolder ph, int validSeconds) {
        super(se); 
        
        this.inInterests = new InterestStore(validSeconds*1000);
        this.outInterests = new InterestStore(validSeconds*1000);
        
        fps = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        FragmentationParameter fp = new FragmentationParameter();
        for(int i = 0; i < SharkCS.MAXDIMENSIONS; i++) {
            fps[i] = fp;
        }
        
        this.ph = ph;
        
        this.restore();
    }
    
    @Override
    protected void doInsert(Knowledge k, KEPConnection responseFactory) {
        // Do nothing. We don't process inserts. The hub only matches interests.
    }
    
    private void doProcess(SharkCS interest, KEPConnection kepConnection, 
            InterestStore storedInterests) throws SharkKBException, 
            SharkException {
        
        Iterator<SharkCS> interestIter = storedInterests.getInterests();
        
        while(interestIter.hasNext()) {
            SharkCS storedInterest = interestIter.next();

            // mutual interest?
            Interest mutualInterest = SharkCSAlgebra.contextualize(
                    storedInterest, interest, fps);

            if(mutualInterest != null) {
                kepConnection.expose(mutualInterest);
            }
        }
    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        
        try {
            // process interest
            if(interest.getDirection() == SharkCS.DIRECTION_IN || 
                    interest.getDirection() == SharkCS.DIRECTION_INOUT) {
                this.doProcess(interest, kepConnection, this.outInterests);
            }
            if(interest.getDirection() == SharkCS.DIRECTION_OUT || 
                    interest.getDirection() == SharkCS.DIRECTION_INOUT) {

                this.doProcess(interest, kepConnection, this.inInterests);
            }
            
            // finally save it
            if(interest.getDirection() == SharkCS.DIRECTION_IN || 
                    interest.getDirection() == SharkCS.DIRECTION_INOUT) {

                this.inInterests.addInterest(interest);

                // persist
                if(this.ph != null) {
                    String serialized = this.inInterests.serialize();
                    this.ph.setProperty(RECEIVING_INTEREST_LIST, serialized, false);
                }
            }

            if(interest.getDirection() == SharkCS.DIRECTION_OUT || 
                    interest.getDirection() == SharkCS.DIRECTION_INOUT) {

                this.outInterests.addInterest(interest);

                // persist
                if(this.ph != null) {
                    String serialized = this.outInterests.serialize();
                    this.ph.setProperty(SENDING_INTEREST_LIST, serialized, false);
                }
            }
        }
        catch(SharkException e) {
            L.l("failure while processing interest in HubKP: " + e.getMessage(), this);
        }
        
    }
    
    /**
     * @param ph 
     */
    private void restore() {
        if(this.ph != null) {
            try {
                String value = this.ph.getProperty(RECEIVING_INTEREST_LIST);
                
                if(value != null) {
                    if(this.inInterests != null) {
                        this.inInterests.restore(value);
                    }
                }
                
                value = this.ph.getProperty(SENDING_INTEREST_LIST);
                
                if(value != null) {
                    if(this.inInterests != null) {
                        this.inInterests.restore(value);
                    }
                }
            } catch (SharkKBException ex) {
                Logger.getLogger(BrokerKP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
