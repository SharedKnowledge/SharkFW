package net.sharkfw.knowledgeBase.sync.manager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
public class SyncOfferKP extends KnowledgePort {

    private final HashMap<PeerSemanticTag, Long> lastSeen = new HashMap<>();

    private static final String LAST_SEEN_PROPERTY_NAME = "SHARK_SYNC_LAST_SEEN";
    private final PropertyHolder propertyHolder;
    private final SyncManager syncManager;

    public SyncOfferKP(SharkEngine se, SyncManager syncManager, PropertyHolder propertyHolder) {
        super(se);
        this.propertyHolder = propertyHolder;
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest.getTypes() != null && interest.getSender() != null) {

            PeerSemanticTag peer = interest.getSender();
            SemanticTag st = interest.getTypes().getSemanticTag(SyncManager.SHARK_SYNC_OFFER_TYPE_SI);
            
            if(st != null && peer != null) {


                SyncMergePropertyList mergePropertyList = this.syncManager.getMergePropertyList();
                L.d(this.se.getOwner().getName() + " received an Offer from " + interest.getSender().getName(), this);

//                // remember that
//                this.lastSeen.put(peer, System.currentTimeMillis());

                Iterator<SemanticTag> iterator = interest.getTopics().stTags();
                while (iterator.hasNext()){
                    SemanticTag next = iterator.next();
                    SyncComponent component = syncManager.getComponentByName(next);
                    if (component!=null){
                        // Get Component and update approved group members
                        component.addApprovedMember(interest.getApprovers());

                        // Now send the latest changes to the sender
                        SyncKB kb = component.getKb();
                        if(kb != null) {
                            SharkKB changes = kb;
                            Long peerLastSeen = null;

                            SyncMergeProperty property = mergePropertyList.get(peer, next);

                            if(property!=null){
                                peerLastSeen = property.getDate();
                                property.updateDate();
                            } else {
                                property = new SyncMergeProperty(peer, next, System.currentTimeMillis());
                            }

                            if(peerLastSeen!=null){
                                changes = kb.getChanges(peerLastSeen);
                            }

                            mergePropertyList.add(property);

                            String serializeKnowledge = ASIPSerializer.serializeKB(changes).toString();

                            L.d(serializeKnowledge, this);
                            L.d("Length of sending Merge: " + serializeKnowledge.length(), this);

                            ASIPOutMessage response = message.createResponse(null, SyncManager.SHARK_SYNC_MERGE_TAG);

                            response.raw(serializeKnowledge.getBytes(StandardCharsets.UTF_8));
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
