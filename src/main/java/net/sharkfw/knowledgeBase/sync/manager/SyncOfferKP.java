package net.sharkfw.knowledgeBase.sync.manager;

import java.util.HashMap;
import java.util.Iterator;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
public class SyncOfferKP extends KnowledgePort {

    private final SyncManager syncManager;

    public SyncOfferKP(SharkEngine se, SyncManager syncManager) {
        super(se);
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

                L.d(this.se.getOwner().getName() + " received an Offer from " + message.getPhysicalSender().getName(), this);

                Iterator<SemanticTag> iterator = interest.getTopics().stTags();
                while (iterator.hasNext()){
                    SemanticTag next = iterator.next();
                    SyncComponent component = syncManager.getComponentByName(next);
                    if (component!=null){

                        component.addApprovedMember(interest.getApprovers());
                        this.syncManager.sendMerge(component, peer, message);

//                        // Get Component and update approved group members
//
//                        // Now send the latest changes to the sender
//                        SyncKB kb = component.getKb();
//                        if(kb != null) {
//                            SharkKB changes = kb;
//                            Long peerLastSeen = null;
//
//                            SyncMergeInfo property = mergePropertyList.get(peer, next);
//
//                            if(property!=null){
//                                peerLastSeen = property.getDate();
//                                property.updateDate();
//                            } else {
//                                property = new SyncMergeInfo(peer, next, System.currentTimeMillis());
//                            }
//
//                            if(peerLastSeen!=null){
//                                changes = kb.getChanges(peerLastSeen);
//                            }
//
//                            mergePropertyList.add(property);
//
//                            L.d("mergePropertyList after add: " + mergePropertyList.toString(), this);
//
//                            ASIPOutMessage response = message.createResponse(null, SyncManager.SHARK_SYNC_MERGE_TAG);
//
//                            response.insert(changes);
//                        }
//
                    }
                }
            }
        }
    }
}
