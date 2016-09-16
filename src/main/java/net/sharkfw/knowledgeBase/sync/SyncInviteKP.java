package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by j4rvis on 26.08.16.
 */
public class SyncInviteKP extends KnowledgePort {

    private final PropertyHolder propertyHolder;
    private final SyncManager syncManager;

    public SyncInviteKP(SharkEngine se, PropertyHolder propertyHolder) {
        super(se);
        this.propertyHolder = propertyHolder;
        this.syncManager = SyncManager.getInstance(null);
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

        // Check if it is an Invitation else return
        SemanticTag inviteTag = interest.getTypes().getSemanticTag(SyncManager.SHARK_SYNC_INVITE_TYPE_SI);
        if(inviteTag==null) return;

        // TODO is sender in whitelist?
        // is uniqueName accepted? check if I already know it!
        Iterator<SemanticTag> iterator = interest.getTopics().stTags();
        while (iterator.hasNext()){
            SemanticTag next = iterator.next();
            SyncComponent syncComponent = syncManager.getComponentByName(next);
            if(syncComponent!=null){
                // TODO already invited.
                // TODO Check if everything is still the same or new approver?
            }
        }

        // for now => accept invitation!

        // TODO create an empty SyncComponent based on the interest?

        // set myself in approver aswell and reply with an OfferTypeTag

        // Set new type as OfferType
        STSet typeSet = InMemoSharkKB.createInMemoSTSet();
        typeSet.merge(SyncManager.SHARK_SYNC_OFFER_TAG);
        interest.setTypes(typeSet);

        PeerSTSet approvers = interest.getApprovers();
        // Add to approvers
        approvers.merge(this.se.getOwner());
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
