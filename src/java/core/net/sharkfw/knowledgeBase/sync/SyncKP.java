package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
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
import net.sharkfw.protocols.Protocols;
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
    private final String SYNCHRONIZATION_NAME = "SyncKP_synchronization_token";
    private final String SYNCHRONIZATION_SERIALIZED_CC_PROPERTY = "SyncKP_serialized_ccs";
    private final String SYNCHRONIZATION_PROTOCOL_STATE = "SyncKP_serialized_state";
    private final String SYNCHRONIZATION_OFFER = "SyncKP_synchronization_offer";
    private final String SYNCHRONIZATION_REQUEST = "SyncKP_synchronization_request";
    
    // Fragmentation parameter for sending knowledge
    FragmentationParameter _topicsFP = null, _peersFP = null;
    
    long _retryTimeout;
    
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
     * @param retryTimeout Retry timeout in seconds
    * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public SyncKP(SharkEngine engine, SyncKB kb, int retryTimeout) throws SharkKBException {
        super(engine, kb);
        _kb = kb;
        _engine = engine;
        _kb.addListener(this);
        
        this._retryTimeout = retryTimeout * 1000;
        this._topicsFP = new FragmentationParameter();
        this._peersFP = new FragmentationParameter();
        
        // We need to have an owner of the kb
        if (_kb.getOwner() == null) {
            L.e("SharkKB for SyncKP needs to have an owner set! Can't create SyncKP.");
            return;
        }
        
        // Create a sync queue for all known peers
        PeerSTSet peersToSyncWith = _kb.getPeerSTSet();
        peersToSyncWith.removeSemanticTag(_kb.getOwner());
        _timestamps = new TimestampList(peersToSyncWith, _kb);
        
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

    public long getRetryTimeout() {
        return _retryTimeout / 1000;
    }

    public void setRetryTimeout(long retryTimeout) {
        this._retryTimeout = retryTimeout * 1000;
    }
    
    public void setTopicsFP(FragmentationParameter topicsFP) {
        this._topicsFP = topicsFP;
    }

    public void setPeersFP(FragmentationParameter peersFP) {
        this._peersFP = peersFP;
    }

    public FragmentationParameter getTopicsFP() {
        return _topicsFP;
    }

    public FragmentationParameter getPeersFP() {
        return _peersFP;
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
    
    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        handleWifiDirect(interest, kepConnection);
        
        try {
            // Retrieve the general sync KP synchronization identification tag
            SemanticTag synchronizationTag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
            
            // Check if the other peer is a sync KP
            if (synchronizationTag != null) {
                
                // Abort if still within retry timeout for this peer
                if (System.currentTimeMillis() < 
                        (_timestamps.getTimestamp(kepConnection.getSender()).getTime() + _retryTimeout)
                    ) {
                    return;
                }
                
                // Find out in which state of the protocol we are
                String state = synchronizationTag.getProperty(SYNCHRONIZATION_PROTOCOL_STATE);
                
                // Is the serialized CC property set? If not, Send back a list of CCs we have to offer to this peer
                // ------------------ DEFAULT ------------------
                if (state == null) {
                    List<SyncContextPoint> possibleCCsForPeer = retrieve(_timestamps.getTimestamp(kepConnection.getSender()));
                    this.setStepOffer(possibleCCsForPeer);
                    SharkCS myInterest = this.getInterest();
                    kepConnection.expose(myInterest, kepConnection.getSender().getAddresses());
                }
                // Is that an offer? Than analyze which CPs I need and send a modified CC list back
                // ------------------ OFFER ------------------
                else if (state.equals(SYNCHRONIZATION_OFFER)) {
                    List<SyncContextPoint> ccs = ContextCoordinatesSerializer.deserializeContextCoordinatesList(
                                        synchronizationTag.getProperty(SYNCHRONIZATION_SERIALIZED_CC_PROPERTY));
                    
                    Iterator<SyncContextPoint> i = ccs.iterator();
                    
                    while(i.hasNext()){
                        SyncContextPoint cc = i.next();
                        ContextPoint cp = _kb.getContextPoint(cc.getContextCoordinates());
                        // TODO: ADD AGAIN
                        if(cp != null && 
                            Integer.parseInt(cp.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME)) >= 
                                Integer.parseInt(cc.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME))){
                            i.remove();
                        }
                    }
                    this.setStepRequest(ccs);     
                    kepConnection.expose(this.getInterest(), kepConnection.getSender().getAddresses());
                }
                // Is that a request? Then send knowledge
                // ------------------ REQUEST ------------------
                else if (state.equals(SYNCHRONIZATION_REQUEST)) {
                    List<SyncContextPoint> ccs = ContextCoordinatesSerializer.deserializeContextCoordinatesList(
                                        synchronizationTag.getProperty(SYNCHRONIZATION_SERIALIZED_CC_PROPERTY));
                    
                    InMemoSharkKB tempKB = new InMemoSharkKB();
                    Knowledge k = tempKB.asKnowledge();
                    // Form a knowledge of each of the requested context points to send back
                    for(SyncContextPoint element : ccs){
                        // Retrieve the context point we want to add to knowledge
                        ContextPoint cp = _kb.getContextPoint(element.getContextCoordinates());
                        // Send topic and peer tags that belong to this context point 
                        // according to the set fragmentation parameters
                        STSet topics = _kb.getTopicSTSet().fragment(cp.getContextCoordinates().getTopic(), _topicsFP);
                        STSet peers = _kb.getPeerSTSet().fragment(cp.getContextCoordinates().getPeer(), _peersFP);

                        // Merge it all into the knowledge that will be send
                        k.getVocabulary().getTopicSTSet().merge(topics);
                        k.getVocabulary().getPeerSTSet().merge(peers);
                        k.addContextPoint(cp);
                    }
                    
                    this.setStepDefault();
                    
                    _timestamps.resetTimestamp(kepConnection.getSender());
//                    kepConnection.insert(k, kepConnection.getSender().getAddresses());
                    kepConnection.insert(k, (String)null);
                    this.notifyInsertSent(this, k);
                }
                // Or something unexpected?
                else {
                    L.d("Error in SyncKP Synchronization Protocol: Received unknown property value for protocol state: " + state);
                }
            }
        }
        catch (SharkException e) {
            L.e(e.getMessage());
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
                // the received context poin, assimilate it into our knowledge base
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
    
    private void handleWifiDirect(SharkCS interest, KEPConnection kepConnection){
        try{
            if (SharkCSAlgebra.isAny(interest)) {
                L.d("Empty interest received. Sending interest back.");
                // Create a topic ST set for wifi direct identification
                STSet identificationTopic = InMemoSharkKB.createInMemoSTSet();
                identificationTopic.merge(InMemoSharkKB.createInMemoSemanticTag("Wifi direct identification", Protocols.WIFI_DIRECT_CONNECTION_TOPIC));
                // Create a Peer Semantic tag set for the peer which contains the owner
                PeerSTSet identificationPeer = InMemoSharkKB.createInMemoPeerSTSet();
                identificationPeer.merge(kb.getOwner());

                Interest i = InMemoSharkKB.createInMemoInterest(identificationTopic, null, identificationPeer, null, null, null, SharkCS.DIRECTION_INOUT);
                // Send it
                kepConnection.expose(i);
            }
            else if (interest.getTopics().getSemanticTag(Protocols.WIFI_DIRECT_CONNECTION_TOPIC) != null && interest.getPeers() != null) {
                L.d("Received wifi direct identification interest. Sending my real interest back.");
                kb.getPeerSTSet().merge(interest.getPeers());
                kepConnection.expose(this.getInterest());
            }
        } catch(SharkException e){
            L.d("Error in WifiDirect handler: " + e.getMessage());
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
    
    protected void setTimestamps(TimestampList s) {
        _timestamps = s;
    }
    
    protected TimestampList getTimestamps() {
        return _timestamps;
    }
    
    protected void resetSyncQueue() throws SharkKBException {
        _timestamps = new TimestampList(_kb.getPeerSTSet(), _kb);
    }
    
    protected List<SyncContextPoint> retrieve(Date d) throws SharkKBException {
        List<SyncContextPoint> toSync = new ArrayList<>();
        Enumeration<ContextPoint> all = _kb.getAllContextPoints();
        
        while(all.hasMoreElements()){
            ContextPoint element = all.nextElement();
            String date = element.getProperty(SyncContextPoint.TIMESTAMP_PROPERTY_NAME);
            
            if(date == null){ 
                continue;
            }
            
            Date compare = new Date(Long.parseLong(date));
            
            if(compare.after(d)){
                SyncContextPoint t = new SyncContextPoint(InMemoSharkKB.createInMemoContextPoint(element.getContextCoordinates()));
                // TODO: ADD AGAIN
                //t.setProperty(SyncContextPoint.VERSION_PROPERTY_NAME, 
                //                element.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
                toSync.add(t);
            }
        }
        
        return toSync;
    }
    
    protected void setStepDefault() throws SharkKBException{
        this.getInterest().getTopics().getSemanticTag(SYNCHRONIZATION_NAME).removeProperty(
                SYNCHRONIZATION_PROTOCOL_STATE
        );
    }
    
    protected void setStepRequest(List<SyncContextPoint> requestCCs) throws SharkKBException{
        this.getInterest().getTopics().getSemanticTag(SYNCHRONIZATION_NAME).setProperty(
                SYNCHRONIZATION_PROTOCOL_STATE, SYNCHRONIZATION_REQUEST
        );
        String serializedRequestCCs = ContextCoordinatesSerializer.serializeContextCoordinatesList(requestCCs);
        this.getInterest().getTopics().getSemanticTag(SYNCHRONIZATION_NAME)
                          .setProperty(SYNCHRONIZATION_SERIALIZED_CC_PROPERTY, serializedRequestCCs);
    }
    
    protected void setStepOffer(List<SyncContextPoint> offerCCs) throws SharkKBException{
        this.getInterest().getTopics().getSemanticTag(SYNCHRONIZATION_NAME).setProperty(
                SYNCHRONIZATION_PROTOCOL_STATE, SYNCHRONIZATION_OFFER
        );
        String serializedOfferCCs = ContextCoordinatesSerializer.serializeContextCoordinatesList(offerCCs);
        this.getInterest().getTopics().getSemanticTag(SYNCHRONIZATION_NAME)
                          .setProperty(SYNCHRONIZATION_SERIALIZED_CC_PROPERTY, serializedOfferCCs);
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
