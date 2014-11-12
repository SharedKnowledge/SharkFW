package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.SharkException;

public class SyncKP extends KnowledgePort{

//	public SyncKP(SharkEngine se) {
//		super(se);
//		// TODO Auto-generated constructor stub
//	}

    protected SyncKB _kb;
    protected SharkEngine _engine;
    private static String SYNCHRONIZATION_NAME = "SharkKP_synchronization";
    
    /**
     * syncs with nobody
     * @param engine
     * @param kb 
     */
    public SyncKP(SharkEngine engine, SyncKB kb) {
        super(engine, kb);
        this._kb = kb;
        this._engine = engine;
        this.setInterest(InMemoSharkKB.createInMemoInterest(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT));
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
        try {
            Enumeration<ContextPoint> cps = knowledge.contextPoints();
            while (cps.hasMoreElements()) {
                ContextPoint remoteCP = cps.nextElement();
                ContextPoint ownCP = _kb.getContextPoint(remoteCP.getContextCoordinates());
                int ownCPVersion = (ownCP == null) ? Integer.parseInt(ownCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME)) : 0;
                
                int remoteCPVersion = Integer.parseInt(remoteCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                if (remoteCPVersion > ownCPVersion) {
                    _kb.createContextPoint(remoteCP.getContextCoordinates());
                    _kb.replaceContextPoint(remoteCP);
                }
            }
        } catch (SharkKBException ex) {
            Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        try{
            SemanticTag tag;
            tag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
        
            XMLSerializer xmlSerializer = new XMLSerializer();

            SharkCS peerCS;

            peerCS = xmlSerializer.deserializeSharkCS(tag.getProperty(SYNCHRONIZATION_NAME));

            if (peerCS.getDirection() == SharkCS.DIRECTION_OUT) {
                Enumeration<SemanticTag> peerTopics;
                STSet ownTopics;

                peerTopics = peerCS.getTopics().tags();
                ownTopics = kb.getTopicSTSet();

                SharkCS ownInterest = InMemoSharkKB.createInMemoInterest(null, null, null, null, null, null, SharkCS.DIRECTION_IN);

                // move to SharkCSAlgebra as STSetIntersection?
                while (peerTopics.hasMoreElements()) {
                    SemanticTag peerTag = peerTopics.nextElement();
                    if (SharkCSAlgebra.isIn(ownTopics, peerTag)) {
                            ownInterest.getTopics().merge(peerTag);
                    }
                }

                String property;

                property = xmlSerializer.serializeSharkCS(ownInterest);

                tag.setProperty(SYNCHRONIZATION_NAME, property);
                kepConnection.expose(interest, kepConnection.getSender().getAddresses());
                this.notifyExposeSent(this, interest);
                
            } else if(peerCS.getDirection() == SharkCS.DIRECTION_IN) {
                
                Knowledge k = SharkCSAlgebra.extract(_kb, peerCS);
                kepConnection.insert(k, kepConnection.getSender().getAddresses());
                this.notifyInsertSent(this, k);

            }    
        } catch(SharkException ex){
                Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
