package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import net.sharkfw.kep.format.XMLSerializer;
import java.util.List;
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
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * The SyncKP realizes a KnowledgePort that constantly tracks changes in the assigned knowledge
 * base and tries to propagate them to all known peers (peers that are in the knowledge base).
 * That way a synchronization between this and every other Sync KP happens. 
 * 
 * The identification of a Sync KP happens with a semantic tag with the subject identifier "SarkKP_synchronization",
 * so this subject identifier may not be used in a knowledge base that is assigned to a Sync KP.
 * 
 * Peers in the knowledge base will only be propagated the future changes in the knowledge base!
 * Means, when a knowledge base already contains knowledge and a peer is added AFTER that, the peer 
 * will NOT receive the entire knowledge, only the changes that happened after she or he was added.
 * If you want to get this peer "up to date", use the syncAllKnowledge method
 * 
 * The Sync KP offers a flag for snowballing, which enables the forwarding of knowledge. Peers who
 * receive a new or updated Context Point from someone using a SyncKP will send this again to all known 
 * peers, when this feature is activated. And they will continue to send it to everyone and so on.
 * Because a Context Point is not assimilated when it already exists in the knowledge base
 * (with the current or a higher version), this feature will not create an endless loop of
 * sending between peers. It might cause a traffic spike though.
 * @author simon
 */
public class SyncKP extends KnowledgePort implements KnowledgeBaseListener {

    protected SyncKB _kb;
    protected SharkEngine _engine;
    
    private Interest _syncInterest;
    private TimestampList _timestamps;
    private final String SYNCHRONIZATION_NAME = "SharkKP_synchronization";
    private final String SYNCHRONIZATION_SERIALIZEDCCPROPERTY = "SyncKP_serialized_ccs";
    
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
        _timestamps = new TimestampList(bucketSet);
        
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
     * Activate snowballing, that forwards all changes inserted by other SyncKPs again to all known peers.
     * @param flag If set to true, all ContextPoints that are added to the Knowledge Base, even if it was
     *  added by this sync KP, will be synchronized with others - which may cause traffic spikes 
     */
    public void setSnowballing(boolean flag) {
        _snowballing = flag;
    }
    
    /**
     * Explicitly sync the entire knowledge base again with all peers.
     * Will cause a huge traffic with big knowledge bases.
     */
    public void syncAllKnowledge() {
        _timestamps.setAllTimestampsNull();
    }
    
    /**
     * Explicitly sync the entire knowledge base again with a peer.
     * This can be used when a new peer is added to the knowledge base to get her or him "up to date".
     * @param peer The peer the knowledge base will be completely synced with
     */
    public void syncAllKnowledge(PeerSemanticTag peer) {
        _timestamps.setTimestampNull(peer);
    }
    
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        try {
            // Retrieve the general sync KP synchronization identification tag
            SemanticTag synchronizationTag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
            
            // Check if the other peer is a sync KP
            if (synchronizationTag != null) {
                // Is there a serialized list of context coordinates on this tag?
                String serializedCCList = synchronizationTag.getProperty(SYNCHRONIZATION_SERIALIZEDCCPROPERTY);
                if (serializedCCList != null) {
                    
                }
                // Else we send back a serialized list of CCs
                else {
                    XMLSerializer x = new XMLSerializer();
//                    x.deserializeSharkCS(serializedCCList)
                }
            }
        }
        catch (SharkException e) {
            L.e(e.getMessage());
        }
    }
    
//    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
//        try {
//            // Perform a check if the other KP is a Sync KP too
//            SemanticTag tag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
//            if (tag != null) {
//                // Create a knowledge of all ContextPoints which need to be synced with that other peer
//                Knowledge k = InMemoSharkKB.createInMemoKnowledge();
//                PeerSemanticTag sender = kepConnection.getSender();
//                for (ContextCoordinates cc : _timestamps.popCoordinatesFromBucket(sender)) {
//                    k.addContextPoint(_kb.getContextPoint(cc));
//                }
//                // tell the engine to allow sending empty cps
//                boolean defaultBehaviour = _engine.getAllowSendingEmptyContextPoints();
//                _engine.setAllowSendingEmptyContextPoints(true);
//                // And send it as a response
//                kepConnection.insert(k, (String) null);
//                // now back to default behaviour
//                _engine.setAllowSendingEmptyContextPoints(defaultBehaviour);
//                this.notifyInsertSent(this, k);
//            }
//        } catch (SharkException e) {
//            L.e(e.getMessage());
//        }
//    }
    
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
                // the received context poin, assimilate it into our knowledge base
                if (remoteCPVersion > ownCPVersion) {
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
    
   
    @Override
    public void peerAdded(PeerSemanticTag tag) {
        _timestamps.newPeer(tag);
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        _timestamps.removePeer(tag);
    }
    
    protected void setSyncQueue(TimestampList s) {
        _timestamps = s;
    }
    
    protected TimestampList getSyncBucketList() {
        return _timestamps;
    }
    
    protected void resetSyncQueue() throws SharkKBException {
        _timestamps = new TimestampList(_kb.getPeerSTSet());
    }
    
    protected List<ContextCoordinates> retrieve(Date d) throws SharkKBException {
        List<ContextCoordinates> toSync = new ArrayList<>();
        Enumeration<ContextPoint> all = _kb.getAllContextPoints();
        
        while(all.hasMoreElements()){
            ContextPoint element = all.nextElement();
            String date = element.getProperty(SyncContextPoint.TIMESTAMP_PROPERTY_NAME);
            
            if(date == null){ 
                continue;
            }
            
            Date compare = new Date(Integer.parseInt(date));
            
            if(compare.before(d)){
                toSync.add(element.getContextCoordinates());
            }
        }
        
        return toSync;
    }

    @Override
    public void topicAdded(SemanticTag tag) {
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

    @Override
    public void contextPointAdded(ContextPoint cp) {
    }

    @Override
    public void cpChanged(ContextPoint cp) {
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
    }
    
    
}
