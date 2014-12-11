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
    
    private Interest _syncInterest;
    private SyncBucketList _syncBuckets;
    private final String SYNCHRONIZATION_NAME = "SharkKP_synchronization";
    
    // Flags for syncing
    private boolean _snowballing;
    
    // Keep the context coordinates of the last context point we inserted (for syncOnInsertByNotSyncKP)
    private ContextCoordinates _lastInsertedCC;
    
    /**
     * This SyncKP will keep the assigned Knowledge Base synchronized with all peers.
     * When activating the syncOnInsertByNotSyncKP flag, the Sync KP will just tell every peer it knows about
     *  every new ContextPoints that were added for example by the application - but not about new ContextPoints it
     *  learned from another Sync KP
     * When activating the syncOnInsertBySyncKP flag, sync KPs will act like a snowball system - 
     *  upon receiving a context point from another sync KP we also sync it again with everyone we know, and they
     *  might sync it again and again.. which might cause a traffic spike but quickly distributes information to everyone
     * @param engine
     * @param kb
     * @param snowballing Always sync when new information is added to the Knowledge Base even if it was
     *  added by this sync KP - which may cause traffic spikes 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public SyncKP(SharkEngine engine, SyncKB kb, boolean snowballing) throws SharkKBException {
        super(engine, kb);
        _kb = kb;
        _engine = engine;
        _kb.addListener(this);
        
        _snowballing = snowballing;
        
        // We need to have an owner of the kb
        if (_kb.getOwner() == null) {
            L.e("SharkKB for SyncKP needs to have an owner set! Can't create SyncKP.");
            return;
        }
        
        // Create a sync queue for all known peers
        PeerSTSet bucketSet = _kb.getPeerSTSet();
        bucketSet.removeSemanticTag(_kb.getOwner());
        _syncBuckets = new SyncBucketList(bucketSet);
        
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
        PeerSTSet ownerPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        ownerPeerSTSet.merge(_kb.getOwner());
        _syncInterest = InMemoSharkKB.createInMemoInterest(syncTag, null, ownerPeerSTSet, null, null, null, SharkCS.DIRECTION_OUT);
        this.setInterest(_syncInterest);
    }
    /**
     * This SyncKP will sync with all peers when new information is inserted into the Knowledge Base
     * @param engine
     * @param kb 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public SyncKP(SharkEngine engine, SyncKB kb) throws SharkKBException {
        this(engine, kb, false);
   }
    
    /**
     * 
     * @param flag If set to true, all ContextPoints that are added to the Knowledge Base, even if it was
     *  added by this sync KP, will be synchronized with others - which may cause traffic spikes 
     */
    public void setSnowballing(boolean flag) {
        _snowballing = flag;
    }
    
    public void syncAllKnowledge() throws SharkKBException {
        Enumeration<ContextPoint> cps = _kb.getAllContextPoints();
        while(cps.hasMoreElements()){
            _syncBuckets.addToBuckets(cps.nextElement().getContextCoordinates());
        }
    }
    
    public void syncAllKnowledge(PeerSemanticTag peer) throws SharkKBException {
        Enumeration<ContextPoint> cps = _kb.getAllContextPoints();
        while(cps.hasMoreElements()){
            _syncBuckets.addToBuckets(cps.nextElement().getContextCoordinates(), peer);
        }
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
                // Get own and received context point
                ContextPoint remoteCP = cps.nextElement();
                ContextPoint ownCP = _kb.getContextPoint(remoteCP.getContextCoordinates());
                // Set version of our own CP to it's version or null if we don't have that context point
                int ownCPVersion = (ownCP == null) ? 0 : Integer.parseInt(ownCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                // Get version of the received context point
                int remoteCPVersion = Integer.parseInt(remoteCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                
                // Now compare. If our context point's version is 0 or lower than the version of
                // the received context point and it comprises information, assimilate it into our 
                // knowledge base
                if (remoteCPVersion > ownCPVersion && remoteCP.getInformation() != null) {
                    _lastInsertedCC = remoteCP.getContextCoordinates();
                    _kb.createContextPoint(remoteCP.getContextCoordinates());
                    _kb.replaceContextPoint(remoteCP);
                }
            }
            this.notifyKnowledgeReceived(knowledge);
        } catch (SharkKBException ex) {
            L.e(ex.getMessage());
        }
    }
    
    /**
     * STILL A HUGE TODO HERE - syncOnInsertByNotSyncKP should check if that CP was added by THIS KP
     * @param cp 
     */
    @Override
    public void contextPointAdded(ContextPoint cp) {
        try {
            if ( _lastInsertedCC == null
                    || !_lastInsertedCC.equals(cp.getContextCoordinates())
                    || (_lastInsertedCC.equals(cp.getContextCoordinates()) && _snowballing)
                ) {
                    _syncBuckets.addToBuckets(cp.getContextCoordinates());
            }
        } catch (SharkKBException e) {
            L.e(e.getMessage());
        }
    }

    @Override
    public void cpChanged(ContextPoint cp) {
        try {
            _syncBuckets.addToBuckets(cp.getContextCoordinates());
        } catch (SharkKBException ex) {
            L.d("SyncKPListener received empty CP: " + ex.getMessage());
        }
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
        // ?
    }
    
    @Override
    public void topicAdded(SemanticTag tag) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        _syncBuckets.appendPeer(tag);
    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        _syncBuckets.removePeer(tag);
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
        // Ignored because we only track peers and context points
    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
        // Ignored because we only track peers and context points
    }
    
    protected void setSyncQueue(SyncBucketList s) {
        _syncBuckets = s;
    }
    protected SyncBucketList getSyncBucketList() {
        return _syncBuckets;
    }
    protected void resetSyncQueue() throws SharkKBException {
        _syncBuckets = new SyncBucketList(_kb.getPeerSTSet());
    }
}
