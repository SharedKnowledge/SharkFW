package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpASIPInterest extends FileDumpASIPSpace implements ASIPInterest {

    private final ASIPInterest interest;

    public FileDumpASIPInterest(FileDumpSharkKB kb, ASIPInterest space) {
        super(kb, space);
        interest = space;
    }

    @Override
    public void setTopics(STSet topics) {
        interest.setTopics(topics);
        kb.persist();
    }

    @Override
    public void setTypes(STSet types) {
        interest.setTypes(types);
        kb.persist();
    }

    @Override
    public void setDirection(int direction) {
        interest.setDirection(direction);
        kb.persist();
    }

    @Override
    public void setSender(PeerSemanticTag originator) {
        interest.setSender(originator);
        kb.persist();
    }

    @Override
    public void setReceivers(PeerSTSet remotePeers) {
        interest.setReceivers(remotePeers);
        kb.persist();
    }

    @Override
    public void setApprovers(PeerSTSet peers) {
        interest.setApprovers(peers);
        kb.persist();
    }

    @Override
    public void setTimes(TimeSTSet times) {
        interest.setTimes(times);
        kb.persist();
    }

    @Override
    public void setLocations(SpatialSTSet location) {
        interest.setLocations(location);
        kb.persist();
    }
}
