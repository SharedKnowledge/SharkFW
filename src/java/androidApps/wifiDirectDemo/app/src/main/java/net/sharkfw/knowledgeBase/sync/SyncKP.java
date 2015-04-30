package net.sharkfw.knowledgeBase.sync;

import com.shark.demo.shark_demo.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
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
 * The SyncKP realizes a KnowledgePort that tries to propagate all changes in the underlying
 * knowledge base to all known peers (peers that are in the knowledge base).
 * That way a synchronization between this and every other Sync KP happens. 
 * 
 * The identification of a Sync KP happens with a semantic tag with the subject identifier "SharkKP_synchronization",
 * so this subject identifier may not be used in a knowledge base that is assigned to a Sync KP.
 * 
 * The identification of context points that need to be sent to other peers uses the time when a peer was
 * last met and retrieves all context points from the knowledge base that were updated since.
 * That means when knowledge was sent to a peer once he will never receive that knowledge again
 * unless the context point was updated in the meantine. 
 * To explicitly send all knowledge again to a single peer or even send all knowledge
 * again to all peers, there is the syncAllKnowledge method.
 * 
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
     * Fragmentation parameter can be set via the setTopicsFP or setPeersFP method.
     * @param engine
     * @param kb The underlying kb that will synchronize changes made to it with others
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
        try {
            peersToSyncWith.removeSemanticTag(_kb.getOwner());
        } catch (SharkKBException e) {
            L.e("Created a Sync knowledge port with no owner set for the database. Please set an owner.");
            throw e;
        }
        _timestamps = new TimestampList(peersToSyncWith, _kb);
        
        // Create the semantic Tag which is used to identify a SyncKP
        STSet syncTag;
        try {
            syncTag = InMemoSharkKB.createInMemoSTSet();
            syncTag.createSemanticTag(SYNCHRONIZATION_NAME, SYNCHRONIZATION_NAME);
        } catch (SharkKBException e) {
            L.d("Tag SharkKP_synchronization which is used by SyncKP already exists!");
            throw e;
        } 
        // And an interest with me as the peer dimension set
        PeerSTSet ownerPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        ownerPeerSTSet.merge(_kb.getOwner());
        _syncInterest = InMemoSharkKB.createInMemoInterest(syncTag, null, ownerPeerSTSet, null, null, null, SharkCS.DIRECTION_OUT);
        this.setInterest(_syncInterest);
    }

    /**
     * Getter for the current retry timeout.
     * @return the retry timeout in seconds
     */
    public long getRetryTimeout() {
        return _retryTimeout / 1000;
    }

    /**
     * Setter for the current retry timeout.
     * @param retryTimeout 
     */
    public void setRetryTimeout(long retryTimeout) {
        this._retryTimeout = retryTimeout * 1000;
    }
    
    /**
     * Setter for the topics fragmentation parameter that will allow
     * the user of the SyncKB to synchronize non-contextualized data.
     * @param topicsFP 
     */
    public void setTopicsFP(FragmentationParameter topicsFP) {
        this._topicsFP = topicsFP;
    }

    /**
     * Setter for the peer fragmentation parameter that will allow
     * the user of the SyncKB to synchronize non-contextualized data.
     * @param peersFP 
     */
    public void setPeersFP(FragmentationParameter peersFP) {
        this._peersFP = peersFP;
    }

    /**
     * Getter for the topics fragmentation parameter
     * @return the topics fragmentation parameter
     */
    public FragmentationParameter getTopicsFP() {
        return _topicsFP;
    }

    /**
     * Getter for the peer fragmentation parameter
     * @return the peer fragmentation parameter
     */
    public FragmentationParameter getPeersFP() {
        return _peersFP;
    }
    
    /**
     * Explicitly sync the entire knowledge base again with all peers.
     * Will cause huge traffic with big knowledge bases.
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
//        handleWifiDirect(interest, kepConnection);
        MainActivity.log("I came: " + L.contextSpace2String(interest));
        L.e(" --------------------- SYNC doExpose started");
        try {
            // Retrieve the general sync KP synchronization identification tag
            SemanticTag synchronizationTag = interest.getTopics().getSemanticTag(SYNCHRONIZATION_NAME);
            MainActivity.log("SyncKP doExpose: " + L.semanticTag2String(synchronizationTag));

            // Check if the other peer is a sync KP
            if (synchronizationTag != null) {
                
                // Find out in which state of the protocol we are
                String state = synchronizationTag.getProperty(SYNCHRONIZATION_PROTOCOL_STATE);
                MainActivity.log("SyncKP doExpose state is " + state);

                // Is the serialized CC property set? If not, Send back a list of CCs we have to offer to this peer
                // ------------------ DEFAULT ------------------
                if (state == null) {
                    MainActivity.log("SyncKP expose default step: " + state);
                    // Abort if still within retry timeout for this peer
                    if (System.currentTimeMillis() < 
                             (_timestamps.getTimestamp(kepConnection.getSender()).getTime() + _retryTimeout)
                       ) {
                        return;
                    }
                    List<SyncContextPoint> possibleCCsForPeer = retrieve(_timestamps.getTimestamp(kepConnection.getSender()));
                    this.setStepOffer(possibleCCsForPeer);
                    L.e(_kb.getOwner().getName() + " sent offer expose.");
                    kepConnection.expose(this.getInterest());
                    MainActivity.log("SyncKP sent offer expose (from default step).");
                    this.notifyExposeSent(this, this.getInterest());
                }
                // Is that an offer? Than analyze which CPs I need and send a modified CC list back
                // ------------------ OFFER ------------------
                else if (state.equals(SYNCHRONIZATION_OFFER)) {
                    MainActivity.log("SyncKP expose offer step: " + state);
                    List<SyncContextPoint> remoteCCs = ContextCoordinatesSerializer.deserializeContextCoordinatesList(
                                        synchronizationTag.getProperty(SYNCHRONIZATION_SERIALIZED_CC_PROPERTY));
                    
                    Iterator<SyncContextPoint> remoteCCIterator = remoteCCs.iterator();
                    
                    while(remoteCCIterator.hasNext()){
                        SyncContextPoint remoteCP = remoteCCIterator.next();
                        ContextPoint localCP = _kb.getContextPoint(remoteCP.getContextCoordinates());
                        // TODO: ADD AGAIN
                        if(localCP != null && 
                            Integer.parseInt(localCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME)) >= 
                                Integer.parseInt(remoteCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME))){
                            remoteCCIterator.remove();
                        }
                    }
                    this.setStepRequest(remoteCCs);     
                    L.e(_kb.getOwner().getName() + " sent request expose.");
//                    kepConnection.expose(this.getInterest(), kepConnection.getSender().getAddresses());
                    kepConnection.expose(this.getInterest());
                    MainActivity.log("Sent request expose (from offer step).");
                    this.notifyExposeSent(this, this.getInterest());
                }
                // Is that a request? Then send knowledge
                // ------------------ REQUEST ------------------
                else if (state.equals(SYNCHRONIZATION_REQUEST)) {
                    MainActivity.log("SyncKP expose request step: " + state);
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
                        if(cp.getContextCoordinates().getTopic() != null){
                            STSet topics = _kb.getTopicSTSet().fragment(cp.getContextCoordinates().getTopic(), _topicsFP);
                            k.getVocabulary().getTopicSTSet().merge(topics);
                        }
                        if(cp.getContextCoordinates().getPeer() != null){
                            STSet peers = _kb.getPeerSTSet().fragment(cp.getContextCoordinates().getPeer(), _peersFP);
                            k.getVocabulary().getPeerSTSet().merge(peers);
                        }
                        k.addContextPoint(cp);
                    }
                    
                    this.setStepDefault();
                    
                    _timestamps.resetTimestamp(kepConnection.getSender());
                    L.e(_kb.getOwner().getName() + " sent insert.");
                    kepConnection.insert(k, kepConnection.getSender().getAddresses());
//                    kepConnection.insert(k, (String) null);
                    MainActivity.log("Sent insert (from request step).");
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
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(SyncKP.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        MainActivity.log("SyncKP insert reached.");
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
            MainActivity.log("Syncing done! Yay");
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
    
    protected void resetPeerTimestamps() {
        PeerSTSet p;
        try {
            p = _kb.getPeerSTSet();
        } catch (SharkKBException e) {
            L.e("Could not getPeerSTSet from knowledge base while resetting timestamp list in syncKP."
                    + " Using an empty PeerSTSet for new peer timestamp list.");
            p = InMemoSharkKB.createInMemoPeerSTSet();
        }
        _timestamps = new TimestampList(p, _kb);
    }
    
    protected List<SyncContextPoint> retrieve(Date d) throws SharkKBException {
        List<SyncContextPoint> toSync = new ArrayList<>();
        
        Enumeration<ContextPoint> all;
        try {
             all = _kb.getAllContextPoints();
        } catch (SharkKBException e) {
            L.e("Could not get context points from knowledge base used by sync KP!");
            throw e;
        }
        
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
                t.setProperty(SyncContextPoint.VERSION_PROPERTY_NAME, 
                                element.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
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
