package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.TestUtils;

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
    }

    public boolean isInvited(PeerSemanticTag tag) throws SharkKBException {
        PeerSemanticTag semanticTag = approvedMembers.getSemanticTag(tag.getSI());
        if (semanticTag!=null){
            return true;
        } else {
            return false;
        }
    }

    public void sendInvite() throws SharkKBException {
        Enumeration<PeerSemanticTag> enumeration = members.peerTags();
        ArrayList<String> addresses = new ArrayList();
        while (enumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = enumeration.nextElement();

            if(!isInvited(peerSemanticTag)){
                String[] peerSemanticTagAddresses = peerSemanticTag.getAddresses();
                if(peerSemanticTagAddresses==null || peerSemanticTagAddresses.length<=0) continue;
                for(String address : peerSemanticTagAddresses){
                    if(address!=null){
                        addresses.add(address);
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : addresses)
        {
            sb.append(s);
            sb.append("\t");
        }
        L.d("addresses: " + sb.toString(), this);
        String[] addressesArray = new String[addresses.size()];
        addressesArray = addresses.toArray(addressesArray);

        if(addressesArray.length!=0){
            sendInvite(addressesArray);
        }
    }

    private void sendInvite(String[] addresses) throws SharkKBException {

        if(addresses==null || addresses.length==0) return;

        PeerSemanticTag logicalSender = null;

        if(this.owner == null){
            logicalSender = this.engine.getOwner();
        } else {
            logicalSender = this.owner;
        }

        ASIPOutMessage message = this.engine.createASIPOutMessage(addresses, logicalSender, null, null, null, null, null, 10);

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
        // TODO send Invitation again if not accepted?
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

    public PeerSTSet getApprovedMembers(){
        return approvedMembers;
    }

    public PeerSemanticTag getOwner() {
        return owner;
    }

    public boolean isWritable() {
        return writable;
    }
}
