package net.sharkfw.knowledgeBase.sync.manager.port;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

import java.util.Iterator;

/**
 * Created by j4rvis on 26.08.16.
 */
public class SyncInviteKP extends KnowledgePort {

    private final SyncManager syncManager;
    private final SharkKB rootKB;

    public SyncInviteKP(SharkEngine se, SyncManager syncManager, SharkKB rootKB) {
        super(se);
        this.syncManager = syncManager;
        if(rootKB!=null){
            this.rootKB = rootKB;
        } else {
            this.rootKB = new InMemoSharkKB();
        }
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

        L.d("Do we already know the component? " + !isNewInvite, this);

        // TODO create an empty SyncComponent based on the interest?

        if(isNewInvite){
            Iterator<SemanticTag> topics = interest.getTopics().stTags();
            // Create an empty kb based on the first topic
            SemanticTag next = topics.next();

            // Necessary to share same peers!
            InMemoSharkKB inMemoSharkKB = new InMemoSharkKB(
                    InMemoSharkKB.createInMemoSemanticNet(),
                    InMemoSharkKB.createInMemoSemanticNet(),
                    this.rootKB.getPeersAsTaxonomy(),
                    InMemoSharkKB.createInMemoSpatialSTSet(),
                    InMemoSharkKB.createInMemoTimeSTSet()
            );

            interest.getApprovers().merge(this.se.getOwner());

            SyncComponent component = syncManager.createSyncComponent(inMemoSharkKB, next, interest.getApprovers(), interest.getSender(), true);
            // Trigger the listeners
//            syncManager.triggerInviteListener(component);
        }

        // set myself in approver aswell and reply with an OfferTypeTag

        // Set new type as OfferType
        STSet typeSet = InMemoSharkKB.createInMemoSTSet();
        typeSet.merge(SyncManager.SHARK_SYNC_ACCEPT_TAG);
        interest.setTypes(typeSet);

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
