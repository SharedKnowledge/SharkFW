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

    private SyncKB syncKB;
    private SemanticTag uniqueName;
    private PeerSTSet members;
    private PeerSTSet approvedMembers = InMemoSharkKB.createInMemoPeerSTSet();
    private PeerSemanticTag owner;
    private boolean writable;

    public SyncComponent(SharkKB kb, SemanticTag uniqueName, PeerSTSet members, PeerSemanticTag owner, boolean writable) throws SharkKBException {
        this.syncKB = new SyncKB(kb);
        this.uniqueName = uniqueName;
        this.members = members;
        this.owner = owner;
        this.writable = writable;
    }

    public boolean isInvited(PeerSemanticTag tag) throws SharkKBException {
        return approvedMembers.getSemanticTag(tag.getSI()) != null;
    }

    public void addApprovedMember(PeerSemanticTag peerSemanticTag) throws SharkKBException {
        approvedMembers.merge(peerSemanticTag);
    }

    public void addApprovedMember(PeerSTSet members) throws SharkKBException {
        approvedMembers.merge(members);
    }

    public void addMember(PeerSemanticTag member) throws SharkKBException {
        members.merge(member);
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
