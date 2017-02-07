package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;

/**
 *
 * @author thsc
 */
class DumpASIPInterest implements ASIPInterest {
    private final DumpPersistentSharkKB dumpKB;
    private final ASIPInterest interest;

    public DumpASIPInterest(DumpPersistentSharkKB dumpKB, ASIPInterest interest) {
        this.dumpKB = dumpKB;
        this.interest = interest;
    }

    @Override
    public void setTopics(STSet topics) {
        this.interest.setTopics(topics);
        this.dumpKB.storageSaveKnowledge();
    }

    @Override
    public void setTypes(STSet types) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDirection(int direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSender(PeerSemanticTag originator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReceivers(PeerSTSet remotePeers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setApprovers(PeerSTSet peers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTimes(TimeSTSet times) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLocations(SpatialSTSet location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet getTopics() {
        try {
            return this.dumpKB.getTopicSTSet();
        } catch (SharkKBException ex) {
            // TODO
        }
        
        return null;
    }

    @Override
    public STSet getTypes() {
        try {
            return this.dumpKB.getTypeSTSet();
        } catch (SharkKBException ex) {
            // TODO
        }
        
        return null;
    }

    @Override
    public int getDirection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getSender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet getReceivers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet getApprovers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSTSet getTimes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSTSet getLocations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
