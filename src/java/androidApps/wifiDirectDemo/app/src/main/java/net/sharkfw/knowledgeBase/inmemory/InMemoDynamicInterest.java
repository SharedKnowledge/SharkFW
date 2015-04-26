package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author thsc
 */
public class InMemoDynamicInterest implements DynamicInterest {
    
    private final SharkKB kb;
    private final SharkCS initialInterest;
    private Interest interest;
    private final FragmentationParameter[] fp;
    
    public InMemoDynamicInterest(SharkKB kb, Interest initialInterest,
            FragmentationParameter[] fp) throws SharkKBException {
        
        super();
        
        // TODO copy initial interest and make change type to SharkCS
        this.kb = kb;
        this.initialInterest = initialInterest;
        this.fp = fp;
        
        this.refresh();
    }

    @Override
    public SharkCS getInitialInterest() {
        return this.initialInterest;
    }

    @Override
    public FragmentationParameter[] getFragmentationParameter() {
        return this.fp;
    }

    @Override
    public SharkKB getSharkKB() {
        return this.kb;
    }

    @Override
    public SharkCS getInterest() throws SharkKBException {
        // TODO: shouldn't refresh with every call. Check whether KB has changed.
        this.refresh();
        
        return this.interest;
    }

    @Override
    public void refresh() throws SharkKBException {
        this.interest = SharkCSAlgebra.contextualize(
                this.kb.asSharkCS(), this.initialInterest, this.fp);
    }

    @Override
    public void setTopics(STSet topics) {
        this.interest.setTopics(topics);
    }

    @Override
    public void setDirection(int direction) {
        this.interest.setDirection(direction);
    }

    @Override
    public void setOriginator(PeerSemanticTag originator) {
        this.interest.setOriginator(originator);
    }

    @Override
    public void setRemotePeers(PeerSTSet remotePeers) {
        this.interest.setRemotePeers(remotePeers);
    }

    @Override
    public void setPeers(PeerSTSet peers) {
        this.interest.setPeers(peers);
    }

    @Override
    public void setTimes(TimeSTSet times) {
        this.interest.setTimes(times);
    }

    @Override
    public void setLocations(SpatialSTSet location) {
        this.interest.setLocations(location);
    }

    @Override
    public Interest contextualize(SharkCS context, FragmentationParameter[] fp) throws SharkKBException {
        return this.interest.contextualize(context, fp);
    }

    @Override
    public boolean isAny(int dim) {
        return this.interest.isAny(dim);
    }

    @Override
    public STSet getTopics() {
        return this.interest.getTopics();
    }

    @Override
    public int getDirection() {
        return this.interest.getDirection();
    }

    @Override
    public PeerSemanticTag getOriginator() {
        return this.interest.getOriginator();
    }

    @Override
    public PeerSTSet getRemotePeers() {
        return this.interest.getRemotePeers();
    }

    @Override
    public PeerSTSet getPeers() {
        return this.interest.getPeers();
    }

    @Override
    public TimeSTSet getTimes() {
        return this.interest.getTimes();
    }

    @Override
    public SpatialSTSet getLocations() {
        return this.interest.getLocations();
    }

    @Override
    public STSet getSTSet(int dim) throws SharkKBException {
        return this.interest.getSTSet(dim);
    }
}
