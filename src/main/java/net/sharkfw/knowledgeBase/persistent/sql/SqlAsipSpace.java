package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.time;

public class SqlAsipSpace implements ASIPSpace {

    private STSet topics;
    private STSet types;
    private PeerSTSet approvers;
    private PeerSTSet receivers;
    private PeerSemanticTag sender;
    private SpatialSTSet locations;
    private TimeSTSet times;
    private int direction = DIRECTION_INOUT;

    public SqlAsipSpace() {
        topics = InMemoSharkKB.createInMemoSTSet();
        types= InMemoSharkKB.createInMemoSTSet();
        approvers = InMemoSharkKB.createInMemoPeerSTSet();
        receivers = InMemoSharkKB.createInMemoPeerSTSet();
        locations = InMemoSharkKB.createInMemoSpatialSTSet();
        times = InMemoSharkKB.createInMemoTimeSTSet();
    }

    public void addTag(SemanticTag tag, int type) throws SharkKBException {
        switch (type){
            case DIM_TOPIC:
                topics.merge(tag);
                break;
            case DIM_TYPE:
                types.merge(tag);
                break;
            case DIM_APPROVERS:
                approvers.merge((PeerSemanticTag) tag);
                break;
            case DIM_SENDER:
                sender = (PeerSemanticTag) tag;
                break;
            case DIM_RECEIVER:
                receivers.merge((PeerSemanticTag) tag);
                break;
            case DIM_TIME:
                times.merge((TimeSemanticTag) tag);
                break;
            case DIM_LOCATION:
                locations.merge((SpatialSemanticTag) tag);
                break;
        }
    }

    @Override
    public STSet getTopics() {
        return topics;
    }

    @Override
    public STSet getTypes() {
        return types;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public PeerSemanticTag getSender() {
        return sender;
    }

    @Override
    public PeerSTSet getReceivers() {
        return receivers;
    }

    @Override
    public PeerSTSet getApprovers() {
        return approvers;
    }

    @Override
    public TimeSTSet getTimes() {
        return times;
    }

    @Override
    public SpatialSTSet getLocations() {
        return locations;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
