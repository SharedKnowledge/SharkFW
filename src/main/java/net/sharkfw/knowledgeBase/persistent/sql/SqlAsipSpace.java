package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

public class SqlAsipSpace implements ASIPSpace {
    @Override
    public STSet getTopics() {
        return null;
    }

    @Override
    public STSet getTypes() {
        return null;
    }

    @Override
    public int getDirection() {
        return 0;
    }

    @Override
    public PeerSemanticTag getSender() {
        return null;
    }

    @Override
    public PeerSTSet getReceivers() {
        return null;
    }

    @Override
    public PeerSTSet getApprovers() {
        return null;
    }

    @Override
    public TimeSTSet getTimes() {
        return null;
    }

    @Override
    public SpatialSTSet getLocations() {
        return null;
    }
}
