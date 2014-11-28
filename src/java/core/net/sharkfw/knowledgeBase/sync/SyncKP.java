package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSTSet;
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
    private SyncBucketList _syncBuckets;
    private final String SYNCHRONIZATION_NAME = "SharkKP_synchronization";
    
    // Flags for syncing
    private boolean _syncOnInsertByNotSyncKP;
    private boolean _syncOnInsertBySyncKP;
    
    // List of peers to sync with
    
    /**
     * This SyncKP will sync with all peers when new information is inserted into the Knowledge Base
     * @param engine
     * @param kb
     * @param syncOnInsertByOwner Sync when new information is inserted into the Knowledge Base by the user
     * @param syncOnInsertByOther Sync when new information is added to the Knowledge base by others (via p2p)
     */
    public SyncKP(SharkEngine engine, SyncKB kb, boolean syncOnInsertByNotSyncKP, boolean syncOnInsertBySyncKP) throws SharkKBException {
        super(engine, kb);
        _kb = kb;
        _engine = engine;
        _kb.addListener(this);
        
        _syncOnInsertByNotSyncKP = syncOnInsertByNotSyncKP;
        _syncOnInsertBySyncKP = syncOnInsertBySyncKP;
        
        // Create a sync queue for all known peers
        _syncBuckets = new SyncBucketList(_kb.getPeerSTSet());
        
        // Create the semantic Tag which is used to identify a SyncKP
        STSet syncTag;
        try {
            syncTag = InMemoSharkKB.createInMemoSTSet();
            syncTag.createSemanticTag(SYNCHRONIZATION_NAME, SYNCHRONIZATION_NAME);
        } catch (SharkKBException e) {
            L.d("Tag SharkKP_synchronization which is used by SyncKP already exists!");
            return;
        } 
        // And an interest with me as the peer dimension set
        // We need to have an owner of the kb
        if (_kb.getOwner() == null) {
            L.e("SharkKB for SyncKP needs to have an owner! Cant create SyncKP.");
            return;
        }
        PeerSTSet ownerPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        ownerPeerSTSet.merge(_kb.getOwner());
        _syncInterest = InMemoSharkKB.createInMemoInterest(syncTag, null, ownerPeerSTSet, null, null, null, SharkCS.DIRECTION_OUT);
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
        _syncOnInsertByNotSyncKP = value;
    }
    public void setSyncOnInsert(boolean value) {
        _syncOnInsertBySyncKP = value;
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
                PeerSemanticTag sender = kepConnection.getSender();
                for (ContextCoordinates cc : _syncBuckets.popFromBucket(sender)) {
                    k.addContextPoint(_kb.getContextPoint(cc));
                }
                // And send it as a response
                kepConnection.insert(k, (String) null);
                this.notifyInsertSent(this, k);
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
                int ownCPVersion = (ownCP == null) ? 0 : Integer.parseInt(ownCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                
                int remoteCPVersion = Integer.parseInt(remoteCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                if (remoteCPVersion > ownCPVersion) {
                    _kb.createContextPoint(remoteCP.getContextCoordinates());
                    _kb.replaceContextPoint(remoteCP);
                }
            }
            this.notifyKnowledgeReceived(knowledge);
        } catch (SharkKBException ex) {
            L.e(ex.getMessage());
        }
    }
    
    @Override
    public void contextPointAdded(ContextPoint cp) {
        
        // Check if sync on KB insert by owner flag is set and CP was added by this user
        if (_syncOnInsertByNotSyncKP && cp.getContextCoordinates().getOriginator().equals(_kb.getOwner())) {
            try {
                _syncBuckets.addToBuckets(cp.getContextCoordinates());
            } catch (SharkKBException e) {
                L.e(e.getMessage());
            }
        }
        // Or if knowledge was inserted by others and sync on insert by others flag is set
        else if (_syncOnInsertBySyncKP && !(cp.getContextCoordinates().getOriginator().equals(_kb.getOwner()))) {
            try {
                _syncBuckets.addToBuckets(cp.getContextCoordinates());
            } catch (SharkKBException e) {
                L.e(e.getMessage());
            }
        }
    }

    @Override
    public void cpChanged(ContextPoint cp) {
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
    }
    
    @Override
    public void topicAdded(SemanticTag tag) {
        
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        
    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {
        
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        
    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
        
    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
        
    }
    
    protected void setSyncQueue(SyncBucketList s) {
        _syncBuckets = s;
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