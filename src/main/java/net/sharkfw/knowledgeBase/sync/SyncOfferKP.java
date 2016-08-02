package net.sharkfw.knowledgeBase.sync;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Util;
import org.json.JSONObject;

/**
 *
 * @author thsc
 */
public class SyncOfferKP extends KnowledgePort {
    public static final String SHARK_SYNC_TYPE_SI = "http://www.sharksystem.net/sync";
    
    private final HashMap<SemanticTag, SyncKB> kbs = new HashMap<>();
    private final HashMap<PeerSemanticTag, Long> lastSeen = new HashMap<>();
    
    private static final String LAST_SEEN_PROPERTY_NAME = "SHARK_SYNC_LAST_SEEN";
    private final PropertyHolder propertyHolder;

    public SyncOfferKP(SharkEngine se, PropertyHolder propertyHolder) {
        super(se);
        this.propertyHolder = propertyHolder;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        // nothing todo here
    }
    
    public void addKnowledgeBase(SyncKB kb, SemanticTag kbTitel) {
        this.kbs.put(kbTitel, kb);
    }
    
    public void removeKnowledgeBase(SemanticTag kbTitel) {
        this.kbs.remove(kbTitel);
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest.getTypes() != null && interest.getSender() != null && interest.getTypes() != null) {
            PeerSemanticTag peer = interest.getSender();
            SemanticTag st = interest.getTypes().getSemanticTag(SyncOfferKP.SHARK_SYNC_TYPE_SI);
            
            if(st != null && peer != null) {
                Long peerLastSeen = this.lastSeen.get(peer);
                // remember that 
                this.lastSeen.put(peer, System.currentTimeMillis());
                
                if(interest.getTopics() != null) {
                    STSet kbSIs = interest.getTopics();
                    if(kbSIs != null) {
                        // iterate kb sis and produce differences
                        Iterator<SemanticTag> kbSIIter = kbSIs.stTags();
                        while(kbSIIter.hasNext()) {
                            SemanticTag kbSI = kbSIIter.next();
                            SyncKB kb = this.kbs.get(kbSI);
                            if(kb != null) {
                                SharkKB changes = kb.getChanges(peerLastSeen);
                                
                                // produce message: TODO: send whole kb not only knowledge!!
                                JSONObject serializeKnowledge = ASIPSerializer.serializeKnowledge(changes);
                                // TODO: send those data as content to recipient
//                                try {
//                                    asipConnection.raw(null, message.getSender().getAddresses());
//                                } catch (SharkException ex) {
//                                    L.e(ex.getLocalizedMessage(), this);
//                                }
                            }
                        }
                    }
                }
            }
        }
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
            this.propertyHolder.setProperty(LAST_SEEN_PROPERTY_NAME, buf.toString());
        } catch (Exception ex) {
            L.e("couldn't write last seen entries for sync - critical", this);
        }
    }
    
    /**
     * TODO
     */
    private void restoreLastSeen() {
        try {
            String prop = this.propertyHolder.getProperty(LAST_SEEN_PROPERTY_NAME);
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
