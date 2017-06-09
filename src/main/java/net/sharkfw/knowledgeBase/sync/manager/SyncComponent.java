package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.Sync;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.KnowledgeUtils;
import net.sharkfw.system.L;
import net.sharkfw.system.TestUtils;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncComponent {

    private final static SemanticTag SYNC_TYPE = InMemoSharkKB.createInMemoSemanticTag("SYNC_INFO", "si:sync_info");
    private final static String SYNC_NAME = "SYNC_NAME";
    private final static String SYNC_MEMBERS = "SYNC_MEMBERS";
    private final static String SYNC_APPROVED_MEMBERS = "SYNC_APPROVED_MEMBERS";
    private final static String SYNC_WRITABLE = "SYNC_WRITABLE";
    private final static String SYNC_OWNER = "SYNC_OWNER";

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

//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_OWNER, ASIPMessageSerializerHelper.serializeTag(owner).toString());
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_NAME, ASIPMessageSerializerHelper.serializeTag(uniqueName).toString());
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_MEMBERS, ASIPMessageSerializerHelper.serializeSTSet(members).toString());
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_APPROVED_MEMBERS, ASIPMessageSerializerHelper.serializeSTSet(approvedMembers).toString());
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_WRITABLE, writable);

//        this(kb);
//
//        if(this.uniqueName==null){
//            this.uniqueName=uniqueName;
//            KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_NAME, ASIPMessageSerializerHelper.serializeTag(uniqueName).toString());
//        }
//        if(this.members==null){
//            this.members = members;
//            KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_MEMBERS, ASIPMessageSerializerHelper.serializeSTSet(members).toString());
//        }
//        if(this.owner==null){
//            this.owner = owner;
//            KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_OWNER, ASIPMessageSerializerHelper.serializeTag(owner).toString());
//        }
//        this.writable = writable;
//        this.approvedMembers.merge(this.owner);
//
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_APPROVED_MEMBERS, ASIPMessageSerializerHelper.serializeSTSet(approvedMembers).toString());
//        KnowledgeUtils.setInfoWithName(syncKB, getSpace(), SYNC_WRITABLE, writable);

    }

//    public SyncComponent(SharkKB kb) throws SharkKBException {
//        this.syncKB = new SyncKB(kb);
//
//        String ownerString = KnowledgeUtils.getInfoAsString(syncKB, getSpace(), SYNC_OWNER);
//        if(ownerString!=null) this.owner=ASIPMessageSerializerHelper.deserializePeerTag(ownerString);
//
//        String nameString = KnowledgeUtils.getInfoAsString(syncKB, getSpace(), SYNC_OWNER);
//        if(nameString!=null) this.uniqueName=ASIPMessageSerializerHelper.deserializeTag(nameString);
//
//        String memberString = KnowledgeUtils.getInfoAsString(syncKB, getSpace(), SYNC_MEMBERS);
//        if(memberString!=null) this.members=ASIPMessageSerializerHelper.deserializePeerSTSet(null, memberString);
//
//        String approvedMembersString = KnowledgeUtils.getInfoAsString(syncKB, getSpace(), SYNC_APPROVED_MEMBERS);
//        if(approvedMembersString!=null) this.approvedMembers=ASIPMessageSerializerHelper.deserializePeerSTSet(null, approvedMembersString);
//
//        this.writable = KnowledgeUtils.getInfoAsBoolean(syncKB, getSpace(), SYNC_WRITABLE);
//    }

    private ASIPSpace getSpace() throws SharkKBException {
        return this.syncKB.createASIPSpace(null, SYNC_TYPE, null, null, null, null, null, ASIPSpace.DIRECTION_INOUT);
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

    public PeerSTSet getMembersWithoutPeer(PeerSemanticTag peer) throws SharkKBException {
        PeerSTSet peerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        Enumeration<PeerSemanticTag> peerSemanticTagEnumeration = members.peerTags();
        while (peerSemanticTagEnumeration.hasMoreElements()){
            PeerSemanticTag nextElement = peerSemanticTagEnumeration.nextElement();
            if(!SharkCSAlgebra.identical(nextElement, peer)){
                peerSTSet.merge(nextElement);
            }
        }
        return peerSTSet;
    }

    public PeerSTSet getApprovedMembers(){
        return approvedMembers;
    }

    public PeerSTSet getApprovedMembersWithoutPeer(PeerSemanticTag peer) throws SharkKBException {
        PeerSTSet peerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        Enumeration<PeerSemanticTag> peerSemanticTagEnumeration = approvedMembers.peerTags();
        while (peerSemanticTagEnumeration.hasMoreElements()){
            PeerSemanticTag nextElement = peerSemanticTagEnumeration.nextElement();
            if(!SharkCSAlgebra.identical(nextElement, peer)){
                peerSTSet.merge(nextElement);
            }
        }
        return peerSTSet;
    }

    public PeerSemanticTag getOwner() {
        return owner;
    }

    public boolean isWritable() {
        return writable;
    }
}
