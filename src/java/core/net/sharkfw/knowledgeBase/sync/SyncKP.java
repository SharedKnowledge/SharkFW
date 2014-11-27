package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

public class SyncKP extends KnowledgePort implements KnowledgeBaseListener  {

    protected SyncKB _kb;
    protected SharkEngine _engine;
    protected Interest _syncInterest;
    private SyncQueue _syncQueue;
    private final String SYNCHRONIZATION_NAME = "SharkKP_synchronization";
    
    // Flags for syncing
    private boolean _syncOnInsertByOwner;
    private boolean _syncOnInsertByOther;
    
    // List of peers to sync with
    
    /**
     * This SyncKP will sync with all peers when new information is inserted into the Knowledge Base
     * @param engine
     * @param kb
     * @param syncOnInsertByOwner Sync when new information is inserted into the Knowledge Base by the user
     * @param syncOnInsertByOther Sync when new information is added to the Knowledge base by others (via p2p)
     */
    public SyncKP(SharkEngine engine, SyncKB kb, boolean syncOnInsertByOwner, boolean syncOnInsertByOther) throws SharkKBException {
        super(engine, kb);
        _kb = kb;
        _engine = engine;
        _kb.addListener(this);
        
        _syncOnInsertByOwner = syncOnInsertByOwner;
        _syncOnInsertByOther = syncOnInsertByOther;
        
        // Create a sync queue for all known peers
        _syncQueue = new SyncQueue(_kb.getPeerSTSet());
        
        // Create the semantic Tag which is used to identify a SyncKP
        STSet syncTag;
        try {
            syncTag = InMemoSharkKB.createInMemoSTSet();
            syncTag.createSemanticTag(SYNCHRONIZATION_NAME, SYNCHRONIZATION_NAME);
        } catch (SharkKBException e) {
            L.d("Tag SharkKP_synchronization which is used by SyncKP already exists!");
            return;
        } 
        _syncInterest = InMemoSharkKB.createInMemoInterest(syncTag, null, null, null, null, null, SharkCS.DIRECTION_OUT);
    }
    /**
     * This SyncKP will sync with all peers when new information is inserted into the Knowledge Base
     * @param engine
     * @param kb 
     */
    public SyncKP(SharkEngine engine, SyncKB kb) throws SharkKBException {
        this(engine, kb, true, false);
   }
    
    public void setSyncOnKBChange(boolean value) {
        _syncOnInsertByOwner = value;
    }
    public void setSyncOnInsert(boolean value) {
        _syncOnInsertByOther = value;
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
        return _syncInterest;
    }
    
    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        try {
            // Perform a check if the other KP is a Sync KP too
            SemanticTag tag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
            if (tag != null) {
                // Create a knowledge of all ContextPoints which need to be synced with that other peer
                Knowledge k = InMemoSharkKB.createInMemoKnowledge();
                for (ContextCoordinates cc : _syncQueue.pop(kepConnection.getSender())) {
                    k.addContextPoint(_kb.getContextPoint(cc));
                }
                // And send it as a response
                kepConnection.insert(k, kepConnection.getSender().getAddresses());
            }
        } catch (SharkException e) {
            L.e(e.getMessage());
            return;
        }
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
            L.e(ex.getMessage());
        }
    }
    
    @Override
    public void contextPointAdded(ContextPoint cp) {
        
        // Check if sync on KB insert by owner flag is set and CP was added by this user
        if (_syncOnInsertByOwner && cp.getContextCoordinates().getOriginator().equals(_kb.getOwner())) {
            try {
                _syncQueue.push(cp.getContextCoordinates());
            } catch (SharkKBException e) {
                L.e(e.getMessage());
            }
        }
        // Or if knowledge was inserted by others and sync on insert by others flag is set
        else if (_syncOnInsertByOther && !(cp.getContextCoordinates().getOriginator().equals(_kb.getOwner()))) {
            try {
                _syncQueue.push(cp.getContextCoordinates());
            } catch (SharkKBException e) {
                L.e(e.getMessage());
            }
        }
    }

    @Override
    public void cpChanged(ContextPoint cp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void topicAdded(SemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected void setSyncQueue(SyncQueue s) {
        _syncQueue = s;
    }
    
//    @Override
//    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
//        try {
//            Enumeration<ContextPoint> cps = knowledge.contextPoints();
//            while (cps.hasMoreElements()) {
//                ContextPoint remoteCP = cps.nextElement();
//                ContextPoint ownCP = _kb.getContextPoint(remoteCP.getContextCoordinates());
//                int ownCPVersion = (ownCP == null) ? Integer.parseInt(ownCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME)) : 0;
//                
//                int remoteCPVersion = Integer.parseInt(remoteCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
//                if (remoteCPVersion > ownCPVersion) {
//                    _kb.createContextPoint(remoteCP.getContextCoordinates());
//                    _kb.replaceContextPoint(remoteCP);
//                }
//            }
//        } catch (SharkKBException ex) {
//            Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
//        try{
//            SemanticTag tag;
//            tag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
//        
//            XMLSerializer xmlSerializer = new XMLSerializer();
//
//            SharkCS peerCS;
//
//            peerCS = xmlSerializer.deserializeSharkCS(tag.getProperty(SYNCHRONIZATION_NAME));
//
//            if (peerCS.getDirection() == SharkCS.DIRECTION_OUT) {
//                Enumeration<SemanticTag> peerTopics;
//                STSet ownTopics;
//
//                peerTopics = peerCS.getTopics().tags();
//                ownTopics = kb.getTopicSTSet();
//
//                SharkCS ownInterest = InMemoSharkKB.createInMemoInterest(null, null, null, null, null, null, SharkCS.DIRECTION_IN);
//
//                // move to SharkCSAlgebra as STSetIntersection?
//                while (peerTopics.hasMoreElements()) {
//                    SemanticTag peerTag = peerTopics.nextElement();
//                    if (SharkCSAlgebra.isIn(ownTopics, peerTag)) {
//                            ownInterest.getTopics().merge(peerTag);
//                    }
//                }
//
//                String property;
//
//                property = xmlSerializer.serializeSharkCS(ownInterest);
//
//                tag.setProperty(SYNCHRONIZATION_NAME, property);
//                kepConnection.expose(interest, kepConnection.getSender().getAddresses());
//                this.notifyExposeSent(this, interest);
//                
//            } else if(peerCS.getDirection() == SharkCS.DIRECTION_IN) {
//                
//                Knowledge k = SharkCSAlgebra.extract(_kb, peerCS);
//                kepConnection.insert(k, kepConnection.getSender().getAddresses());
//                this.notifyInsertSent(this, k);
//
//            }    
//        } catch(SharkException ex){
//                Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
