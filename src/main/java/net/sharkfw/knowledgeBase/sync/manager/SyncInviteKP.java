package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

import java.util.Iterator;

/**
 * Created by j4rvis on 26.08.16.
 */
public class SyncInviteKP extends KnowledgePort {

    private final SyncManager syncManager;

    public SyncInviteKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

        // Check if it is an Invitation else return
        SemanticTag inviteTag = interest.getTypes().getSemanticTag(SyncManager.SHARK_SYNC_INVITE_TYPE_SI);
        if(inviteTag==null) return;

        L.d(this.se.getOwner().getName() + " received an Invite from " + interest.getSender().getName(), this);

        // TODO is sender in whitelist?
        // is uniqueName accepted? check if I already know it!
        boolean isNewInvite = true;
        Iterator<SemanticTag> iterator = interest.getTopics().stTags();
        while (iterator.hasNext()){
            SemanticTag next = iterator.next();
            SyncComponent syncComponent = syncManager.getComponentByName(next);
            if(syncComponent!=null){
                syncComponent.addApprovedMember(interest.getApprovers());
                isNewInvite = false;
            }
        }
        // TODO create an empty SyncComponent based on the interest?

        if(isNewInvite){
            Iterator<SemanticTag> topics = interest.getTopics().stTags();
            // Create an empty kb based on the first topic
            SemanticTag next = topics.next();
            SyncComponent component = syncManager.createSyncComponent(new InMemoSharkKB(), next, interest.getApprovers(), interest.getSender(), true);
            component.addApprovedMember(this.se.getOwner());
            // Trigger the listeners
            syncManager.triggerListener(component);
        }

        // set myself in approver aswell and reply with an OfferTypeTag

        // Set new type as OfferType
        STSet typeSet = InMemoSharkKB.createInMemoSTSet();
        typeSet.merge(SyncManager.SHARK_SYNC_OFFER_TAG);
        interest.setTypes(typeSet);

        interest.getApprovers().merge(this.se.getOwner());

        // Set myself as sender
        interest.setSender(this.se.getOwner());

        // and reply to originator
        try {
            asipConnection.expose(interest);
        } catch (SharkException e) {
            e.printStackTrace();
        }

    }
}
