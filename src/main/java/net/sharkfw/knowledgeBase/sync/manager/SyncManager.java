package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncAcceptKP;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncInviteKP;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncManager {

    private final ExecutorService executor;

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
    private final SyncAcceptKP syncAcceptKP;
    private final SyncMergeKP syncMergeKP;
    private SyncInviteKP syncInviteKP;

    // Lists
    private final SyncMergeInfoSerializer mergeInfoSerializer;
    // TODO you will just be notified but can't decide if you ant to accept
//    private List<SyncInviteListener> listeners = new ArrayList<>();
    private List<SyncComponent> components = new ArrayList<>();
    // Engine
    private SharkEngine engine;

    public SyncManager(SharkEngine engine) {
        this.engine = engine;
        this.syncAcceptKP = new SyncAcceptKP(this.engine, this);
        this.syncMergeKP = new SyncMergeKP(this.engine, this);
        this.mergeInfoSerializer = new SyncMergeInfoSerializer(this.engine.getStorage());
        executor = Executors.newSingleThreadExecutor();
    }

    public void addSyncMergeListener(SyncMergeKP.SyncMergeListener listener){
        this.syncMergeKP.addSyncMergeListener(listener);
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

        if(mergeInfo!=null) {
            long lastMerged = mergeInfo.getDate();
            long lastChanges = component.getKb().getTimeOfLastChanges();

            if (lastChanges > lastMerged) {
                changes = component.getKb().getChanges(lastMerged);
            }
        } else {
            changes = component.getKb();
        }
        return changes;
    }

    /**
     * Add Component
     * @param component
     * @return
     */
    public boolean addSyncComponent(SyncComponent component){
        if (getComponentByName(component.getUniqueName()) != null) return false;
        components.add(component);
        return true;
    }

    /**
     * Create a syncComponent
     * @param kb
     * @param uniqueName
     * @param members
     * @param owner
     * @param writable
     * @return
     */
    public SyncComponent createSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet members,
            PeerSemanticTag owner,
            boolean writable) {

        if (getComponentByName(uniqueName) != null) return null;

        SyncComponent component = null;
        try {
            component = new SyncComponent(kb, uniqueName, members, owner, writable);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        components.add(component);
        return component;
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
            PeerSemanticTag member,
            PeerSemanticTag owner,
            boolean writable) {
        PeerSTSet peerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        try {
            peerSTSet.merge(member);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return createSyncComponent(kb, uniqueName, peerSTSet, owner, writable);
    }

    public SyncComponent createInvitedSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet members,
            PeerSTSet approvers,
            PeerSemanticTag owner,
            boolean writable) {

        try {
            SyncComponent component = new SyncComponent(kb, uniqueName, members, owner, writable);
            component.addApprovedMember(approvers);
            components.add(component);
            return component;
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeSyncComponent(SyncComponent component) {
        components.remove(component);
    }

    public List<SyncComponent> getSyncComponents() {
        return components;
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
    public List<SyncComponent> getSyncComponentsWithPeer(PeerSemanticTag peerSemanticTag) throws SharkKBException {
        ArrayList<SyncComponent> componentArrayList = new ArrayList<>();

        Iterator<SyncComponent> syncComponents = getSyncComponents().iterator();
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
        return componentArrayList;
    }

    /**
     * Send a Merge to all approved Members
     * @param component
     */
    public void doSync(SyncComponent component){
//        if(checkInvitation(component) == false) return;
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
                doSync(component, peerSemanticTag);
            }
        }
    }

    /**
     * Send a Merge to the peer from all components
     * @param peer
     */
    public void doSync(PeerSemanticTag peer){
        for (SyncComponent component : components) {
            try {
                if(SharkCSAlgebra.isIn(component.getApprovedMembers(), peer)){
                    doSync(component, peer);
                }
            } catch (SharkKBException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Send a Merge to a given peer
     * @param component
     * @param peer
     */
    public void doSync(final SyncComponent component, final PeerSemanticTag peer){
        if (component.hasAccepted(peer)){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        SharkKB changes = getChanges(component, peer);
                        if (hasChanged(changes)) {
                            L.d("We have some Changes so send insert to " + peer.getName(), this);
                            // We do have some changes we can send!
                            ASIPOutMessage outMessage = engine.createASIPOutMessage(
                                    peer.getAddresses(),
                                    engine.getOwner(),
                                    peer,
                                    null,
                                    null,
                                    component.getUniqueName(),
                                    SyncManager.SHARK_SYNC_MERGE_TAG, 1);

                            outMessage.insert(changes);
                            mergeInfoSerializer.add(component.getUniqueName(), peer);
                        }
                    } catch (SharkKBException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.submit(runnable);
        } else {
            doInvite(component, peer);
        }
    }

    /**
     * Reply to a Message from a given Peer!
     * @param component
     * @param peer
     * @param message
     * @throws SharkKBException
     */
    public void doSync(final SyncComponent component, final PeerSemanticTag peer, final ASIPInMessage message) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SharkKB changes = getChanges(component, peer);
                    if (hasChanged(changes)) {
                        ASIPOutMessage response = message.createResponse(null, SyncManager.SHARK_SYNC_MERGE_TAG);
                        response.insert(changes);
                        L.w(engine.getOwner().getName() + " sent changes!", this);
                        mergeInfoSerializer.add(component.getUniqueName(), peer);
                    }
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
            }
        };
        executor.submit(runnable);
    }

    public void doInviteOrSync(PeerSemanticTag peer){
        for (SyncComponent component : components) {
            try {
                if(SharkCSAlgebra.isIn(component.getApprovedMembers(), peer) || SharkCSAlgebra.identical(component.getOwner(), peer)){
                    doSync(component, peer);
                    L.d(peer.getName() + " already is an approved Member or the Owner so try to SYNC!", this);
//                    L.d(peer.getName() + " already is the Owner so try to SYNC!", this);
                } else if(SharkCSAlgebra.isIn(component.getMembers(), peer)){
                    doInvite(component, peer);
                    L.d(peer.getName() + " is a Member so INVITE!", this);
                }
//                else {
//                    L.d(peer.getName() + " is not part of component " + component.getUniqueName().getName(), this);
//                }
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send an Invite to all members of the component who are not approved Members yet
     * @param component
     * @throws SharkKBException
     */
    public void doInvite(SyncComponent component) throws SharkKBException {
        ArrayList<PeerSemanticTag> members = Collections.list(component.getMembers().peerTags());
        ArrayList<PeerSemanticTag> approvedMembers = Collections.list(component.getApprovedMembers().peerTags());
        members.removeAll(approvedMembers);
        Iterator<PeerSemanticTag> iterator = members.iterator();
        while (iterator.hasNext()){
            doInvite(component, iterator.next());
        }
    }

    /**
     * Send an Invite of a component to a peer
     * @param component
     * @param peerSemanticTag
     */
    public void doInvite(final SyncComponent component, final PeerSemanticTag peerSemanticTag){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                L.d("Start sending an Expose to " + peerSemanticTag.getName(), this);

                String[] addresses = peerSemanticTag.getAddresses();
                if(addresses==null || addresses.length==0) return;

                PeerSemanticTag logicalSender = null;

                if(component.getOwner() == null){
                    logicalSender = engine.getOwner();
                } else {
                    logicalSender = component.getOwner();
                }

                ASIPOutMessage message = engine.createASIPOutMessage(addresses, logicalSender, null,
                        null, null, component.getUniqueName(), null, 10);

                try {
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
                    ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest(topicSTSet, typeSTSet, component.getOwner(),
                            approverSTSet, component.getMembers(), null, null, direction);
                    message.expose(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
            }
        };

        executor.submit(runnable);

    }

    public boolean hasChanged(SharkKB sharkKB){
        boolean changed = false;
        try {
            if(!sharkKB.getTopicSTSet().isEmpty()) changed = true;
            if(!sharkKB.getTypeSTSet().isEmpty()) changed = true;
            if(!sharkKB.getPeerSTSet().isEmpty()) changed = true;
            if(!sharkKB.getTimeSTSet().isEmpty()) changed = true;
            if(!sharkKB.getSpatialSTSet().isEmpty()) changed = true;
            if(sharkKB.getNumberInformation() > 0) changed = true;
            return changed;
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
    }
}
