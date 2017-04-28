package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncManager {

    // Interfaces
    public interface SyncInviteListener {
        void onInvitation(SyncComponent component);
    }

    // Public CONSTANTS
    public static final String SHARK_SYNC_INVITE_TYPE_SI = "http://www.sharksystem.net/sync/invite";
    public static final String SHARK_SYNC_ACCEPT_TYPE_SI = "http://www.sharksystem.net/sync/accept";
    public static final String SHARK_SYNC_MERGE_TYPE_SI = "http://www.sharksystem.net/sync/merge";
    public static final SemanticTag SHARK_SYNC_INVITE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_INVITE", SHARK_SYNC_INVITE_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_ACCEPT_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_ACCEPT", SHARK_SYNC_ACCEPT_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_MERGE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_MERGE", SHARK_SYNC_MERGE_TYPE_SI);

    // Ports
    private final SyncAcceptKP offerKP;
    private final SyncMergeKP syncMergeKP;
    private SyncInviteKP syncInviteKP;

    // Lists
    private final SyncMergeInfoSerializer mergeInfoSerializer;
    // TODO you will just be notified but can't decide if you ant to accept
    private List<SyncInviteListener> listeners = new ArrayList<>();
    private List<SyncComponent> components = new ArrayList<>();
    // Engine
    private SharkEngine engine;

    public SyncManager(SharkEngine engine) {
        this.engine = engine;
        this.offerKP = new SyncAcceptKP(this.engine, this);
        this.syncMergeKP = new SyncMergeKP(this.engine, this);
        this.mergeInfoSerializer = new SyncMergeInfoSerializer(this.engine.getStorage());
    }

    /**
     * Activate Invitation, so that other peers can invite us to their components.
     * @param allow
     */
    public void allowInvitation(boolean allow){
        this.allowInvitation(allow, null);
    }

    /**
     * Activate Invitation, so that other peers can invite us to their components.
     * With a rootKb to share the same Peers
     * @param allow
     * @param sharkKB
     */
    public void allowInvitation(boolean allow, SharkKB sharkKB) {
        if (allow) {
            this.syncInviteKP = new SyncInviteKP(this.engine, this, sharkKB);
        } else {
            this.syncInviteKP = null;
        }
    }

    /**
     * Get the changes of the Component regarding the given Peer. It will  be checked when we have seen the peer the last
     * time and therefor the changes inside if the knowledgeBase will be retrieved
     * @param component
     * @param peerSemanticTag
     * @return A SharkKB consisting of the changes or even null if there are no changes!
     * @throws SharkKBException
     */
    public SharkKB getChanges(SyncComponent component, PeerSemanticTag peerSemanticTag) throws SharkKBException {

        SharkKB changes = null;

        SyncMergeInfo mergeInfo = this.mergeInfoSerializer.get(peerSemanticTag, component.getUniqueName());

//        L.d("Is " + peerSemanticTag.getName() + " already listed? " + (mergeInfo!=null), this);

        if(mergeInfo!=null) {
            // It seems that we have merged with the peer at least once.
            // Now get the changes since the last merge
            long lastMerged = mergeInfo.getDate();
//            L.d("lastMerged: " + lastMerged, this);
            long lastChanges = component.getKb().getTimeOfLastChanges();
//            L.d("lastChanges: " + lastChanges, this);

            if (lastChanges > lastMerged) {
                // the last changes are newer than the last time of the merge.
                changes = component.getKb().getChanges(lastMerged);

                // TODO  here we are just presuming, that we will do the merge. But what if not? Should be triggered after sendMerge
                mergeInfo.updateDate();
            }
        } else {
            mergeInfo = new SyncMergeInfo(peerSemanticTag, component.getUniqueName(), System.currentTimeMillis());
            // Okay we haven't merged with our peer yet.
            // So our changes represent the whole kb
            changes = component.getKb();
        }
        this.mergeInfoSerializer.add(mergeInfo);
        // TODO Just checked for information inside the kb not for its changes regarding the STSets
        return changes;
    }

    /**
     * Create a syncComponent
     * @param kb
     * @param uniqueName
     * @param member
     * @param owner
     * @param writable
     * @return
     */
    public SyncComponent createSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet member,
            PeerSemanticTag owner,
            boolean writable) {

        if (getComponentByName(uniqueName) != null) return null;

        SyncComponent component = null;
        try {
            component = new SyncComponent(kb, uniqueName, member, owner, writable);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        components.add(component);
        return component;
    }

    public void removeSyncComponent(SyncComponent component) {
        components.remove(component);
    }

    public Iterator<SyncComponent> getSyncComponents() {
        return components.iterator();
    }

    /**
     * Get the component by its uniwue name
     * @param name
     * @return
     */
    public SyncComponent getComponentByName(SemanticTag name) {
        for (SyncComponent component : components) {
            if (SharkCSAlgebra.identical(component.getUniqueName(), name)) {
                return component;
            }
        }
        return null;
    }

    /**
     * Get all components where the peer is involved in
     * @param peerSemanticTag
     * @return
     * @throws SharkKBException
     */
    public Iterator<SyncComponent> getSyncComponentsWithPeer(PeerSemanticTag peerSemanticTag) throws SharkKBException {
        ArrayList<SyncComponent> componentArrayList = new ArrayList<>();

        Iterator<SyncComponent> syncComponents = getSyncComponents();
        while (syncComponents.hasNext()){
            SyncComponent next = syncComponents.next();

            PeerSemanticTag owner = next.getOwner();
            PeerSTSet members = next.getMembers();

            if(owner.getSI().equals(peerSemanticTag.getSI())){
                componentArrayList.add(next);
                continue;
            }

            PeerSemanticTag membersSemanticTag = members.getSemanticTag(peerSemanticTag.getSI());
            if(membersSemanticTag!=null){
                componentArrayList.add(next);
            }
        }
        return componentArrayList.iterator();
    }

    /**
     * Check if we have any approved Members and trigger Invitations if necessary.
     * @param component
     * @return
     */
    private boolean checkInvitation(SyncComponent component){
        /**
         * Okay so we have a component and want to send out a merge!
         * So what's first?
         * Do we have any approved member?
         * Is there still a member who is not approved?
         * TODO What if he does not want to approve or participate? Delete from member?
         */
        PeerSTSet members = component.getMembers();
        PeerSTSet approvedMembers = component.getApprovedMembers();

        try {
            if(members.isEmpty()){
                // Oh okay we don't have anyone who can participate
                // Should not be possible because the creator of the component will be added as well.
                return false;
            } else{
                if(approvedMembers.isEmpty()){
                    // for now we have no one who participates in our syncGroup
                    // We have to send invites to everyone!
                    this.sendInvite(component);
                    return false;
                } else {
                    // There is at least someone who participates, so we can send out our merge!
                    // Now check if we are the one
                    if (!SharkCSAlgebra.identical(members, approvedMembers)) {
                        // There are still some people missing so we are sending out our invites!
                        // TODO SendInvite?
                        Enumeration<PeerSemanticTag> enumeration = members.peerTags();
                        while (enumeration.hasMoreElements()){
                            PeerSemanticTag peerSemanticTag = enumeration.nextElement();
                        }
                        this.sendInvite(component);
                        return false;
                    }
                    return true;
                }
            }
        } catch (SharkKBException e) {
            L.e(e.getMessage(), this);
        }
        return false;

    }

    /**
     * Send a Merge to all approved Members
     * @param component
     */
    public void sendMerge(SyncComponent component){

//        L.d("Initiated sending a Merge to all approvedMembers.", this);

        if(checkInvitation(component) == false) return;

        PeerSTSet approvedMembers = component.getApprovedMembers();
        // Okay so now we are finished with our invites!
        // Let's send our merge!
        // Now we are iterating all approved members
        Enumeration<PeerSemanticTag> approvedMemberEnumeration = approvedMembers.peerTags();
        while (approvedMemberEnumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = approvedMemberEnumeration.nextElement();
            // Okay now that we have a peer we can send our changes to
            // we have to check if we have already merged with the peer and get the date of the last merge.
//            L.d("Name of approvedMember: " + peerSemanticTag.getName(), this);
            // Now check if we are the peer
            if(!SharkCSAlgebra.identical(peerSemanticTag, this.engine.getOwner())){
                sendMerge(component, peerSemanticTag);
            }
        }
    }

    /**
     * Send a Merge to a given peer
     * @param component
     * @param peer
     */
    public void sendMerge(SyncComponent component, PeerSemanticTag peer){

//        L.d("Initiated sending a merge to a special peer.", this);

        try {
            if (component.isInvited(peer)){
                SharkKB changes = getChanges(component, peer);
                if(changes!=null){
                    // We do have some changes we can send!
                    ASIPOutMessage outMessage = this.engine.createASIPOutMessage(
                            peer.getAddresses(),
                            this.engine.getOwner(),
                            peer,
                            null,
                            null,
                            component.getUniqueName(),
                            SyncManager.SHARK_SYNC_MERGE_TAG, 1);

                    outMessage.insert(changes);
                }
            } else {
                sendInvite(component, peer);
            }
        } catch (SharkKBException e) {
            L.e(e.getMessage(), this);
        }
    }

    /**
     * Reply to a Message from a given Peer!
     * @param component
     * @param peer
     * @param message
     * @throws SharkKBException
     */
    public void sendMerge(SyncComponent component, PeerSemanticTag peer, ASIPInMessage message) throws SharkKBException {

//        L.d("Initiated sending a Merge to reply to an already established connection.", this);

        SharkKB changes = getChanges(component, peer);
//        L.d(this.engine.getOwner().getName() + " wants to send a Merge to " + peer.getName() + ". Are there any changes? " + (changes!=null), this);
        if(changes!=null){
            ASIPOutMessage response = message.createResponse(null, SyncManager.SHARK_SYNC_MERGE_TAG);
            response.insert(changes);
        }
    }

    /**
     * Send an Invite to all members of the component who are not approved Members yet
     * @param component
     * @throws SharkKBException
     */
    public void sendInvite(SyncComponent component) throws SharkKBException {
        Enumeration<PeerSemanticTag> enumeration = component.getMembers().peerTags();
        ArrayList<String> addresses = new ArrayList<>();
        while (enumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = enumeration.nextElement();

            if (SharkCSAlgebra.identical(peerSemanticTag, this.engine.getOwner())) continue;

            if(!component.isInvited(peerSemanticTag)){
                String[] peerSemanticTagAddresses = peerSemanticTag.getAddresses();
                if(peerSemanticTagAddresses==null || peerSemanticTagAddresses.length<=0) continue;
                for(String address : peerSemanticTagAddresses){
                    if(address!=null){
                        addresses.add(address);
                    }
                }
            }
        }
        String[] addressesArray = new String[addresses.size()];
        addressesArray = addresses.toArray(addressesArray);

        if(addressesArray.length!=0){
            sendInvite(component, addressesArray);
        }
    }

    /**
     * Send an Invite of a component to a peer
     * @param component
     * @param peerSemanticTag
     */
    public void sendInvite(SyncComponent component, PeerSemanticTag peerSemanticTag){
        try {
            if(!component.isInvited(peerSemanticTag)){
                component.addMember(peerSemanticTag);
            }
            sendInvite(component, peerSemanticTag.getAddresses());
        } catch (SharkKBException e) {
            e.printStackTrace();
            L.d(e.getMessage(), this);
        }
    }

    private void sendInvite(SyncComponent component, String[] addresses) throws SharkKBException {

        if(addresses==null || addresses.length==0) return;

        PeerSemanticTag logicalSender = null;

        if(component.getOwner() == null){
            logicalSender = this.engine.getOwner();
        } else {
            logicalSender = component.getOwner();
        }

        ASIPOutMessage message = this.engine.createASIPOutMessage(addresses, logicalSender, null, null, null, component.getUniqueName(), null, 10);

        // Create ASIPInterest
        STSet topicSTSet = InMemoSharkKB.createInMemoSTSet();
        STSet typeSTSet = InMemoSharkKB.createInMemoSTSet();
        PeerSTSet approverSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        topicSTSet.merge(component.getUniqueName());
        typeSTSet.merge(SyncManager.SHARK_SYNC_INVITE_TAG);
        approverSTSet.merge(component.getOwner());

        int direction  = ASIPSpace.DIRECTION_IN;;
        if(component.isWritable()){
            direction = ASIPSpace.DIRECTION_INOUT;
        }
        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest(topicSTSet, typeSTSet, component.getOwner(), approverSTSet, component.getMembers(), null, null, direction);

        message.expose(interest);
    }

    public void addInviteListener(SyncInviteListener listener) {
        listeners.add(listener);
    }

    public void removeInviteListener(SyncInviteListener listener) {
        listeners.remove(listener);
    }

    public void triggerInviteListener(SyncComponent component) {
        for (SyncInviteListener listener : this.listeners) {
            listener.onInvitation(component);
        }
    }
}
