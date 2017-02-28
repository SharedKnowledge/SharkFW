package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpASIPSpace implements ASIPSpace {

    private final FileDumpSharkKB kb;
    private final ASIPSpace space;

    public FileDumpASIPSpace(FileDumpSharkKB kb, ASIPSpace space) {
        this.kb = kb;
        this.space = space;
    }

    @Override
    public STSet getTopics() {
        return new FileDumpSTSet(kb, space.getTopics());
    }

    @Override
    public STSet getTypes() {
        return new FileDumpSTSet(kb, space.getTypes());
    }

    @Override
    public int getDirection() {
        return space.getDirection();
    }

    @Override
    public PeerSemanticTag getSender() {
        return new FileDumpPeerSemanticTag(kb, space.getSender());
    }

    @Override
    public PeerSTSet getReceivers() {
        return new FileDumpPeerSTSet(kb, space.getReceivers());
    }

    @Override
    public PeerSTSet getApprovers() {
        return new FileDumpPeerSTSet(kb, space.getApprovers());
    }

    @Override
    public TimeSTSet getTimes() {
        return new FileDumpTimeSTSet(kb, space.getTimes());
    }

    @Override
    public SpatialSTSet getLocations() {
        return new FileDumpSpatialSTSet(kb, space.getLocations());
    }
}
