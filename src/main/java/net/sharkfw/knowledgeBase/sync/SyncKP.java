package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * Created by j4rvis on 19.07.16.
 * 
 * @author thsc
 */
public class SyncKP extends ContentPort {
    private PeerSTSet allowedUsers = null;
    private SyncKB syncKB;
    private HashMap<PeerSemanticTag, Long> lastSeen = new HashMap<>();
    private static final String LAST_SEEN_PROPERTY_NAME = "SHARK_SYNC_LAST_SEEN";
    
    public SyncKP(SharkEngine se, SyncKB kb, PeerSTSet allowedUsers) {
        super(se);
        this.syncKB = kb;
        try {
            if(allowedUsers != null) {
                this.allowedUsers = InMemoSharkKB.createInMemoCopy(allowedUsers);
            }
        }
        catch(SharkKBException e) {
            // cannot happen..
        }
    }

    public SyncKP(SharkEngine se, SyncKB kb) {
        this(se, kb, null);
    }

    @Override
    protected boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream) {
        return false;
    }
  
    // test
    private void saveLastSeen() {
        //test
//        PeerSemanticTag p = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "http://alice.de", "mail://alice@alice.de");
//        Long t = System.currentTimeMillis();
//        this.lastSeen.put(p, t);
//        
//        p = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "http://bob.de", "mail://bob@bob.de");
//        t = System.currentTimeMillis();
//        this.lastSeen.put(p, t);
        // end test
                
        StringBuilder buf = new StringBuilder();
        Iterator<PeerSemanticTag> peerIter = this.lastSeen.keySet().iterator();
        try {
            while(peerIter.hasNext()) {
                PeerSemanticTag peer = peerIter.next();
                Long lastseen = this.lastSeen.get(peer);
                buf.append(ASIPSerializer.serializeTag(peer));
                buf.append("{" + Long.toString(lastseen) + "}");
            }
            L.d("write buf: " + buf.toString());
            this.syncKB.setProperty(LAST_SEEN_PROPERTY_NAME, buf.toString());
        } catch (Exception ex) {
            L.e("couldn't write last seen entries for sync - critical", this);
        }
    }
    
    /**
     * TODO
     */
    private void restoreLastSeen() {
        try {
            String prop = this.syncKB.getProperty(LAST_SEEN_PROPERTY_NAME);
            if(prop == null) return;
            
            Iterator<String> stringIter = Util.stringsBetween("{", "}", prop, 0);
            while(stringIter.hasNext()) {
                String peerString = "{" + stringIter.next() + "}";
                String timeString = stringIter.next();
                
                PeerSemanticTag peer = ASIPSerializer.deserializePeerTag(peerString);
                Long time = Long.parseLong(timeString);
                
                this.lastSeen.put(peer, time);
            }
            
        } catch (SharkKBException ex) {
            L.d("cannot read last seen entries for sync - critical", this);
        }
        
        
        
    }
    
    
}
