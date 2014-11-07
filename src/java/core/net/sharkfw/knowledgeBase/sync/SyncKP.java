package net.sharkfw.knowledgeBase.sync;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.KnowledgeSerializer;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

public class SyncKP extends KnowledgePort{

//	public SyncKP(SharkEngine se) {
//		super(se);
//		// TODO Auto-generated constructor stub
//	}

    /**
     * syncs with nobody
     * @param syncerEngine
     * @param _syncer 
     */
    public SyncKP(SharkEngine syncerEngine, SyncKB _syncer) {
        super(syncerEngine, _syncer);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * ATTENTION!!!! Port will keep this kb with ANY other
     * peer..
     */
    public void syncWithAnyPeer() {
    }
    
    public SharkCS getInterest() {
        // hier ein Interesse senden.
        //
        this.getKB().getOwner(); // das ist das Peer!
        return null;
    }
    
    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
            // TODO Auto-generated method stub

    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        // TODO Auto-generated method stub
        XMLSerializer xmlSerializer = new XMLSerializer();
        
        try {
            xmlSerializer.deserializeSharkCS(null);
        } catch (SharkKBException ex) {
            Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
