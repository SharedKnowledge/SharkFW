package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncComponent {

    private SharkKB kb;
    private SemanticTag uniqueName;
    private PeerSTSet members;
    private PeerSemanticTag owner;
    private boolean writable;

    public SyncComponent(SharkKB kb, SemanticTag uniqueName, PeerSTSet members, PeerSemanticTag owner, boolean writable) {
        this.kb = kb;
        this.uniqueName = uniqueName;
        this.members = members;
        this.owner = owner;
        this.writable = writable;


    }

    public void addMember(PeerSemanticTag member) throws SharkKBException {
        members.merge(member);
    }

    public void removeMember(PeerSemanticTag member) throws SharkKBException {
        members.removeSemanticTag(member);
    }

    public SharkKB getKb() {
        return kb;
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
