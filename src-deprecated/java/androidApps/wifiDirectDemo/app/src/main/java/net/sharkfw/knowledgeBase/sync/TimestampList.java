package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

/**
 *
 * @author s0539710
 */
/**
 * A class that holds the timestamp of the last meeting between this and any other peer.
 * 
 * With this timestamp the knowledge that needs to be synced with another peer can be retrieved 
 * from the knowledge base.
 * @author simon
 */
class TimestampList {
    protected static String LIST_PROPERTY = "internal_timestamp_list";
    protected List<PeerTimestamp> _timestamps;
    protected SyncKB _kb;
        
    /**
     * The standard constructor. 
     *
     * It needs a KB for persistance. All info will be saved there.
     */
    public TimestampList(SyncKB kb) {
        _kb = kb;
        retrieve();
    }
    
    /**
     * Constructor with optional initial peer list.
     *
     * A list needs to be supplied that will add already-known peers
     * to the list
     */
    public TimestampList(PeerSTSet peersToSyncWith, SyncKB kb) {
        _kb = kb;
        retrieve();
        // Add all possible peers to queue
        Enumeration<PeerSemanticTag> peerEnum = peersToSyncWith.peerTags();
        while (peerEnum.hasMoreElements()) {
            _timestamps.add(new PeerTimestamp(peerEnum.nextElement()));
        }
    }
    
    /**
     * Add a peer that will be synced with in the future
     * @param peer 
     */
    public void newPeer(PeerSemanticTag peer) {
        _timestamps.add(new PeerTimestamp(peer));
        persist();
    }
    
    public void removePeer(PeerSemanticTag peer) {
        PeerTimestamp waldo = findPeerTimestamp(peer);
        if (waldo == null) {
            L.w("Tried to remove an unexisting peer from SyncKP Peer timestamps.");
        }
        else {
            _timestamps.remove(waldo);
            persist();
        }
    }
    
    /**
     * Returns all peers currently in the peer timestamp list
     * @return all peers
     * @throws SharkKBException 
     */
    public PeerSTSet getPeers() {
        PeerSTSet myPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        for (PeerTimestamp s : _timestamps) {
            try {
                myPeerSTSet.merge(s.getPeer());
            } catch (SharkKBException e) {
                L.e("Could not merge peer " + s.getPeer() + " into a peerSTSet "
                + "while trying to return all peers in current TimestampList.");
            }
        }
        return myPeerSTSet;
    }
    
    /** 
     * Get the timestamp when a peer was last encountered.
     * @param peer
     * @return Timestamp of last encounter
     */
    public Date getTimestamp(PeerSemanticTag peer) {
        PeerTimestamp p = findPeerTimestamp(peer);
        if (p == null) return new Date(0);
        else return p.getDate();
    }
    
    /**
     * Resets the time of last meeting with this peer to now.
     * @param peer 
     */
    public void resetTimestamp(PeerSemanticTag peer) {
        PeerTimestamp p = findPeerTimestamp(peer);
        if (p != null) {
            p.resetDate();
            persist();
        }
    }
    
    /**
     * Sets the time of last meeting with this peer to 01,01,1970!
     * This will probably sync ALL knowledge with this peer again, so use with caution.
     * @param peer 
     */
    public void setTimestampNull(PeerSemanticTag peer) {
        PeerTimestamp p = findPeerTimestamp(peer);
        if (p != null) {
            p.setTimestampNull();
            persist();
        }
    }
    
    /**
     * Sets the time of last meeting with ALL peers to 01,01,1970!
     * This will probably sync ALL knowledge with ALL peers again, so use with caution.
     * @param peer 
     */
    public void setAllTimestampsNull() {
        for (PeerTimestamp p : _timestamps) {
            p.setTimestampNull();
        }
        persist();
    }
    
    //Internal finder method.
    private PeerTimestamp findPeerTimestamp(PeerSemanticTag p) {
        // Find the peer in our list
        Iterator<PeerTimestamp> i = _timestamps.iterator();
        PeerTimestamp waldo;
        // Lets look for waldo
        while(i.hasNext()) {
            waldo = i.next();
            if(waldo.getPeer().equals(p))
                return waldo;
        }
        
        return null;
    }
    
    //Persistance function. Is called whenever the list changes.
    protected void persist() {
        try {
            _kb.setProperty(LIST_PROPERTY, serializeTimestamps());
        } catch (SharkKBException ex) {
            // TODO
        }
    }
    
    //retrieval function. Is called from within the constructor.
    protected void retrieve() {
        _timestamps = new ArrayList<>();
        
        String x = null;
        try {
            x = _kb.getProperty(LIST_PROPERTY);
        } catch (SharkKBException ex) {
            // TODO
        }
        
        if(x != null){ 
            deserializeTimestamps(x);
        }
    }
    
    //internal serialization function.
    private String serializeTimestamps() {
        StringBuilder buf = new StringBuilder();
        
        for(PeerTimestamp t : _timestamps)
            buf.append(t.serialize());
        
        return buf.toString();
    }
    
    //internal deserialization function.
    private void deserializeTimestamps(String x) {
        for(String s : x.split(PeerTimestamp.PEER_TIMESTAMP_CLOSING_TAG)){
            try {
                _timestamps.add(new PeerTimestamp(s));
            } catch (SharkKBException ex) {
                L.d("Could not load timestamp for a peer: " + s + " message: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Internal class that represents one timestamp(when the peer was last encountered)
     */
    class PeerTimestamp {
        private Date _date;
        private final PeerSemanticTag _peer;
        static private final String PEER_TIMESTAMP_CLOSING_TAG = "</peer_timestamp>";
        static private final String PEER_TIMESTAMP_TAG = "<peer_timestamp>";
        static private final String SI_TAG = "<subject_identifiers>";
        static private final String SI_CLOSING_TAG = "</subject_identifiers>";
        static private final String DATE_TAG = "<date>";
        static private final String DATE_CLOSING_TAG = "</date>";
        
        /**
         * Default constructor.
         *
         * Needs a PeerSemanticTag representing the peer.
         */
        public PeerTimestamp(PeerSemanticTag peer) {
            _peer = InMemoSharkKB.createInMemoCopy(peer);
            _date = new Date(0);
        }
        
        /**
         * Creates a new peer timestamp from a serialized entry.
         */
        public PeerTimestamp(String serialized) throws SharkKBException {
            serialized = serialized.replaceFirst(PEER_TIMESTAMP_TAG, "");
            //<si>.*</si>
            int index;
            //Get number of occurences of tag
            int si_count = (serialized.length() - serialized.replace(SI_TAG, "").length()) / SI_TAG.length();
            String[] sis = new String[si_count];
            int n = 0;
            while((index = serialized.indexOf(SI_TAG)) != -1) {
                int endIndex = serialized.indexOf(SI_CLOSING_TAG);
                String substring = serialized.substring(index, endIndex);
                
                sis[n++] = (substring.substring(SI_TAG.length(), substring.length()));
                
                serialized = serialized.replaceFirst(substring, "");
            }
            _peer = TimestampList.this._kb.getPeerSemanticTag(sis);
            _date = new Date(Long.parseLong(
                    serialized.substring(
                            serialized.indexOf(DATE_TAG) + DATE_TAG.length(),
                            serialized.indexOf(DATE_CLOSING_TAG))));
        }
        
        /**
         * Returns the date when this peer was last met.
         * @return date of last meeting (actually, of last reset)
         */
        public Date getDate() {
            return _date;
        }
        
        /**
         * Resets the time of last meeting with this peer to now.
         */
        public void resetDate() {
            _date = new Date();
        }
       
        /**
         * Resets the time of last meeting with this peer to 01,01,1970.
         */
        public void setTimestampNull() {
            _date = new Date(0);
        }
        
        /**
         * Get the timestamps' peer.
         */
        public PeerSemanticTag getPeer() {
            return _peer;
        }
        
        /**
         * Serialize the timestamp.
         */
        public String serialize(){
            StringBuilder serializedSelf = new StringBuilder();
            // <peer_semantic_tag>
            serializedSelf.append(PEER_TIMESTAMP_TAG);
            // <si>
            for (String s : _peer.getSI()) {
                serializedSelf.append(SI_TAG);
                serializedSelf.append(s);
                serializedSelf.append(SI_CLOSING_TAG);
            }
            // </si>
            // <date>
            serializedSelf.append(DATE_TAG);
            serializedSelf.append(_date.getTime());
            serializedSelf.append(DATE_CLOSING_TAG);
            // </date>
            // </peer_semantic_tag>
            serializedSelf.append(PEER_TIMESTAMP_CLOSING_TAG);
            
            return serializedSelf.toString();
        }
    }
}


