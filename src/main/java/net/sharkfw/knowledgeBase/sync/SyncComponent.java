package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncComponent {

    private final SharkEngine engine;
    private SyncKB syncKB;
    private SemanticTag uniqueName;
    private PeerSTSet members;
    private PeerSTSet approvedMembers = InMemoSharkKB.createInMemoPeerSTSet();
    private PeerSemanticTag owner;
    private boolean writable;

    public SyncComponent(SharkEngine engine, SharkKB kb, SemanticTag uniqueName, PeerSTSet members, PeerSemanticTag owner, boolean writable) throws SharkKBException {
        this.engine = engine;
        this.syncKB = new SyncKB(kb);
        this.uniqueName = uniqueName;
        this.members = members;
        this.owner = owner;
        this.writable = writable;

        // Send out invitations to the members

        // Create outMessage based on all peerAddresses
        Enumeration<PeerSemanticTag> enumeration = members.peerTags();
        ArrayList<String> addresses = new ArrayList();
        while (enumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = enumeration.nextElement();
            String[] peerSemanticTagAddresses = peerSemanticTag.getAddresses();
            for(String address : peerSemanticTagAddresses){
                addresses.add(address);
            }
        }
        String[] addressesArray = new String[addresses.size()];
        addressesArray = addresses.toArray(addressesArray);

        sendInvite(addressesArray);
        // Members of Merge not yet clear - wait for accepting the Invitation
//        this.syncMergeKP = new SyncMergeKP(this.engine, this.syncKB, this.uniqueName, this.members);
    }

    private void sendInvite(String[] addresses) throws SharkKBException {
        ASIPOutMessage message = this.engine.createASIPOutMessage(addresses, null);

        // Create ASIPInterest
        STSet topicSTSet = InMemoSharkKB.createInMemoSTSet();
        STSet typeSTSet = InMemoSharkKB.createInMemoSTSet();
        PeerSTSet approverSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        topicSTSet.merge(this.uniqueName);
        typeSTSet.merge(SyncManager.SHARK_SYNC_INVITE_TAG);
        approverSTSet.merge(this.owner);

        int direction  = ASIPSpace.DIRECTION_IN;;
        if(this.writable){
            direction = ASIPSpace.DIRECTION_INOUT;
        }
        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest(topicSTSet, typeSTSet, this.owner, approverSTSet, members, null, null, direction);

        // TODO expose Thread???
        // TODO send Invitation again if no accept?
        message.expose(interest);
    }

    public void addApprovedMember(PeerSemanticTag peerSemanticTag) throws SharkKBException {
        approvedMembers.merge(peerSemanticTag);

    }

    public void addApprovedMember(PeerSTSet members) throws SharkKBException {
        approvedMembers.merge(members);
    }

    public void addMember(PeerSemanticTag member) throws SharkKBException {
        members.merge(member);
        sendInvite(member.getAddresses());
    }

    public void removeMember(PeerSemanticTag member) throws SharkKBException {
        members.removeSemanticTag(member);
    }

    public SyncKB getKb() {
        return syncKB;
    }

    public SemanticTag getUniqueName() {
        return uniqueName;
    }

    public PeerSTSet getMembers() {
        return members;
    }

    public PeerSemanticTag getOwner() {
        return owner;
    }

    public boolean isWritable() {
        return writable;
    }
}
