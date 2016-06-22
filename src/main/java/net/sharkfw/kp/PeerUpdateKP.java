package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

/**
 * That KP takes peer semantic tags from each incomming
 * KEP request and updates or adds entries to its local
 * vocabulary.
 *
 * @author thsc
 */
public class PeerUpdateKP extends KnowledgePort {
    private final boolean addPST;
    private final boolean removesAddresses;

    /**
     * @param se
     * @param addPST          if true: PST are added. False: Only existing peers info get an update
     * @param removeAddresses removes addresses if their are no longer present in
     *                        received PST
     */
    public PeerUpdateKP(SharkEngine se, boolean addPST, boolean removeAddresses) {
        super(se);
        this.addPST = addPST;
        this.removesAddresses = removeAddresses;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
