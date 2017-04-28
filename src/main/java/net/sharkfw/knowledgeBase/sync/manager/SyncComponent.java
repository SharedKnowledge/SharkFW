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
        this.approvedMembers.merge(this.owner);
    }

    public boolean hasAccepted(PeerSemanticTag tag) {
        try {
            return approvedMembers.getSemanticTag(tag.getSI()) != null;
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addApprovedMember(PeerSemanticTag peerSemanticTag){
        try {
            approvedMembers.merge(peerSemanticTag);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    public void addApprovedMember(PeerSTSet members){
        try {
            approvedMembers.merge(members);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    public void addMember(PeerSemanticTag member){
        try {
            members.merge(member);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    public void removeMember(PeerSemanticTag member){
        try {
            members.removeSemanticTag(member);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
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
