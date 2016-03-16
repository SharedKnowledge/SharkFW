package net.sharkfw.knowledgeBase.inmemory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 *
 * @author thsc
 */
public class InMemoInformationSpace implements ASIPInformationSpace, ASIPSpace {
    private STSet topics;
    private STSet types;
    private PeerSTSet approvers;
    private PeerSTSet senders;
    private PeerSTSet receivers;
    private TimeSTSet times;
    private SpatialSTSet locations;
    private int direction;
    
    private List<Information> infoList = new ArrayList<>();

    InMemoInformationSpace() {}
    
    InMemoInformationSpace(STSet topics, STSet types,
            PeerSTSet approvers, PeerSTSet senders, PeerSTSet receivers,
            TimeSTSet times, SpatialSTSet locations, int direction) {
        
        this.topics = topics;
        this.types = types;
        this.approvers = approvers;
        this.senders = senders;
        this. receivers = receivers;
        this.times = times;
        this.locations = locations;
        this. direction = direction;
    }
    
    InMemoInformationSpace(STSet topics, STSet types,
            PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receivers,
            TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        
        this.topics = topics;
        this.types = types;
        this.approvers = approvers;
        this.senders = InMemoSharkKB.createInMemoPeerSTSet();
        this.senders.merge(sender);
        this. receivers = receivers;
        this.times = times;
        this.locations = locations;
        this. direction = direction;
    }
    
    InMemoInformationSpace(SemanticTag topic, SemanticTag type,
            PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver,
            TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        
        this.topics = InMemoSharkKB.createInMemoSTSet();
        this.topics.merge(topic);
        
        this.types = InMemoSharkKB.createInMemoSTSet();
        this.types.merge(type);
        
        this.approvers = InMemoSharkKB.createInMemoPeerSTSet();
        this.approvers.merge(approver);
        
        this.senders = InMemoSharkKB.createInMemoPeerSTSet();
        this.senders.merge(sender);
        
        this.receivers = InMemoSharkKB.createInMemoPeerSTSet();
        this.receivers.merge(receiver);
        
        this.locations = InMemoSharkKB.createInMemoSpatialSTSet();
        this.locations.merge(location);
        
        this.times = InMemoSharkKB.createInMemoTimeSTSet();
        this.times.merge(time);
        
        this.direction = direction;
    }
    
    InMemoInformationSpace(Information info) throws SharkKBException {
        this(info.getASIPSpace());
        this.addInformation(info);
    }
    
    InMemoInformationSpace(ASIPSpace space) throws SharkKBException {
        this(
                InMemoSharkKB.createInMemoCopy(space.getTopics()),
                InMemoSharkKB.createInMemoCopy(space.getTypes()),
                InMemoSharkKB.createInMemoCopy(space.getApprovers()),
                InMemoSharkKB.createInMemoCopy(space.getSender()),
                InMemoSharkKB.createInMemoCopy(space.getReceivers()),
                InMemoSharkKB.createInMemoCopy(space.getTimes()),
                InMemoSharkKB.createInMemoCopy(space.getLocations()),
                space.getDirection()
        );
    }
    
    final void addInformation(Information info) {
        this.infoList.add(info);
        
        // extends space!!! TODO
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return this;
    }

//    @Override
//    public void setASIPSpace(ASIPSpace space) throws SharkKBException {
//        this.topics = InMemoSharkKB.createInMemoCopy(space.getTopics());
//        this.types = InMemoSharkKB.createInMemoCopy(space.getTypes());
//        this.approvers = InMemoSharkKB.createInMemoCopy(space.getApprovers());
//        this.senders = InMemoSharkKB.createInMemoPeerSTSet();
//        this.senders.merge(space.getSender());
//        this.receivers = InMemoSharkKB.createInMemoCopy(space.getReceivers());
//        this.times = InMemoSharkKB.createInMemoCopy(space.getTimes());
//        this.locations = InMemoSharkKB.createInMemoCopy(space.getLocations());
//        this.direction = space.getDirection();
//    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet getTopics() {
        return this.topics;
    }

    @Override
    public STSet getTypes() {
        return this.types;
    }

    @Override
    public int getDirection() {
        return this.direction;
    }

    @Override
    public PeerSemanticTag getSender() {
        if(this.senders == null) return null;
        
        Enumeration<PeerSemanticTag> stTags = this.senders.peerTags();
        if(stTags == null) return null;
        
        if(!stTags.hasMoreElements()) return null;
        
        return stTags.nextElement();
    }

    @Override
    public PeerSTSet getReceivers() {
        return this.receivers;
    }

    @Override
    public PeerSTSet getApprovers() {
        return this.approvers;
    }

    @Override
    public TimeSTSet getTimes() {
        return this.times;
    }

    @Override
    public SpatialSTSet getLocations() {
        return this.locations;
    }
    
}
