package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpASIPSpace implements ASIPSpace {

    protected final DumpSharkKB kb;
    private final ASIPSpace space;

    public DumpASIPSpace(DumpSharkKB kb, ASIPSpace space) {
        this.kb = kb;
        this.space = space;
    }

    @Override
    public STSet getTopics() {
        return new DumpSTSet(kb, space.getTopics());
    }

    @Override
    public STSet getTypes() {
        return new DumpSTSet(kb, space.getTypes());
    }

    @Override
    public int getDirection() {
        return space.getDirection();
    }

    @Override
    public PeerSemanticTag getSender() {
        return new DumpPeerSemanticTag(kb, space.getSender());
    }

    @Override
    public PeerSTSet getReceivers() {
        return new DumpPeerSTSet(kb, space.getReceivers());
    }

    @Override
    public PeerSTSet getApprovers() {
        return new DumpPeerSTSet(kb, space.getApprovers());
    }

    @Override
    public TimeSTSet getTimes() {
        return new DumpTimeSTSet(kb, space.getTimes());
    }

    @Override
    public SpatialSTSet getLocations() {
        return new DumpSpatialSTSet(kb, space.getLocations());
    }
}
