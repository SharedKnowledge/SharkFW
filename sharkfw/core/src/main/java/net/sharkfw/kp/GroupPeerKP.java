package net.sharkfw.kp;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

/**
 * Implements a group peer - see my PhD thesis for details.
 * TODO
 * 
 * @author thsc
 */
public class GroupPeerKP extends KnowledgePort {

    public GroupPeerKP(SharkEngine se) {
        super(se);
    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
