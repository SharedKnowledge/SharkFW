package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.knowledgeBase.sync.manager.SyncMergeInfo;
import net.sharkfw.knowledgeBase.sync.manager.SyncMergeInfoSerializer;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncAcceptKP;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.routing.SemanticRoutingKP;
import net.sharkfw.system.L;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dustin Feurich
 */
public class BroadcastManager {

    private final ExecutorService executor;
    public static final String SHARK_BROADCAST_TYPE_SI = "http://www.sharksystem.net/broadcast";
    public static final SemanticTag SHARK_BROADCAST_TAG = InMemoSharkKB.createInMemoSemanticTag("BROADCAST", SHARK_BROADCAST_TYPE_SI);

    // Semantic Routing
    private SemanticRoutingKP semanticRoutingKP;
    private ASIPInterest activeEntryProfile;
    private List<ASIPInterest> entryProfiles;
    private ASIPInterest activeOutProfile;

    private HashMap<String, String> sentMessages;

    private final SyncMergeInfoSerializer mergeInfoSerializer;
    private SyncComponent broadcastComponent;
    private SharkEngine engine;

    public BroadcastManager(SharkEngine engine) {
        this.engine = engine;
        this.semanticRoutingKP = new SemanticRoutingKP(this.engine, this);
        this.activeEntryProfile = null;
        this.entryProfiles = new ArrayList<>();
        this.activeOutProfile = null;
        this.mergeInfoSerializer = new SyncMergeInfoSerializer(this.engine.getStorage());
        this.broadcastComponent = null;
        executor = Executors.newSingleThreadExecutor();
        sentMessages = new HashMap<>();
    }

    public boolean checkWithEntryProfile(SharkKB newKnowledge, PeerSemanticTag physicalSender, ASIPInMessage message) {

        if (activeEntryProfile == null || activeEntryProfile.getTopics() == null) return true;
        boolean isInteresting = false;
        String profileSI = null;
        if (activeEntryProfile.getTopics() instanceof SemanticNet) {
            isInteresting = checkSemanticNet((SemanticNet) activeEntryProfile.getTopics(), "Topic");
        }
        if (activeEntryProfile.getReceivers() instanceof SemanticNet && isInteresting) {
            isInteresting = checkSemanticNet((SemanticNet) activeEntryProfile.getReceivers(), "Receivers");
        }
        if (activeEntryProfile.getApprovers() instanceof SemanticNet && isInteresting) {
            isInteresting = checkSemanticNet((SemanticNet) activeEntryProfile.getApprovers(), "Approvers");
        }
        else {
            try {
                profileSI = activeEntryProfile.getTopics().tags().nextElement().getSI()[0];
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
            Enumeration<SemanticTag> topicTags = null;
            try {
                topicTags = newKnowledge.getTopicSTSet().tags();
            } catch (SharkKBException e) {
                e.printStackTrace();
                return false;
            }
            SemanticTag currentElement;
            while (topicTags.hasMoreElements()) {
                currentElement = topicTags.nextElement();
                System.out.println("_________ TAG SI: " + currentElement.getSI()[0]);
                if (profileSI.equals(currentElement.getSI()[0])) {
                    isInteresting = true;
                }
            }
        }
        return isInteresting;

    }

    private boolean checkSemanticNet(SemanticNet net, String netKind) {
        boolean isInteresting = false;
        Enumeration<SemanticTag> tags = null;


        return isInteresting;
    }

    public boolean isUnknown(SharkKB newKnowledge){
        Enumeration<SemanticTag> topicTags = null;
        try {
            topicTags = newKnowledge.getTopicSTSet().tags();
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
        SemanticTag topic = topicTags.nextElement();
        System.out.println("_________ IS UNKNOWN SI_____: " + topic.getSI()[0]);
        if (sentMessages.containsKey(topic.getSI()[0])) {
            //return false;
            return true; //temporarily true for debugging purposes
        }
        else {
            sentMessages.put(topic.getSI()[0], "message");
            return true;
        }
    }

    public void sendBroadcastMessage(final SyncComponent component, final List<PeerSemanticTag> peers) {
        for (final PeerSemanticTag peer : peers) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                        SharkKB changes = component.getKb();
                        L.d("Broadcast insert sent to: " + peer.getName(), this);
                        System.out.println("Broadcast insert sent to: " + peer.getName());
                        ASIPOutMessage outMessage = engine.createASIPOutMessage(
                                peer.getAddresses(),
                                engine.getOwner(),
                                peer,
                                null,
                                null,
                                component.getUniqueName(),
                                SHARK_BROADCAST_TAG, 1);

                        outMessage.insert(changes);
                        mergeInfoSerializer.add(component.getUniqueName(), peer);

                }
            };
            executor.submit(runnable);
        }
    }

    public void sendBroadcastMessage(final SyncComponent component, final PeerSemanticTag peer) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SharkKB changes = component.getKb();
                    L.d("Broadcast insert sent to: " + peer.getName(), this);
                    System.out.println("Broadcast insert sent to: " + peer.getName());
                    ASIPOutMessage outMessage = engine.createASIPOutMessage(
                            peer.getAddresses(),
                            engine.getOwner(),
                            peer,
                            null,
                            null,
                            component.getUniqueName(),
                            SHARK_BROADCAST_TAG, 1);

                    outMessage.insert(changes);
                    mergeInfoSerializer.add(component.getUniqueName(), peer);

                }
            };
            executor.submit(runnable);

    }

    public void addSemanticRoutingListener(SemanticRoutingKP.SemanticRoutingListener listener){
        this.semanticRoutingKP.addSemanticRoutingListener(listener);
    }

    public ASIPInterest getActiveEntryProfile() {
        return activeEntryProfile;
    }

    public void setActiveEntryProfile(ASIPInterest activeEntryProfile) {
        this.activeEntryProfile = activeEntryProfile;
    }

    public ASIPInterest getActiveOutProfile() {
        return activeOutProfile;
    }

    public void setActiveOutProfile(ASIPInterest activeOutProfile) {
        this.activeOutProfile = activeOutProfile;
    }

    public void addEntryProfile(ASIPInterest profile) {
        this.entryProfiles.add(profile);
    }

    public List<ASIPInterest> getEntryProfiles() {
        return entryProfiles;
    }

    public SyncComponent getBroadcastComponent() {
        return broadcastComponent;
    }

    public void setBroadcastComponent(SyncComponent broadcastComponent) {
        this.broadcastComponent = broadcastComponent;
    }

    public HashMap<String, String> getSentMessages() {
        return sentMessages;
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

        if (broadcastComponent != null) return broadcastComponent;
        SyncComponent component = null;
        try {
            component = new SyncComponent(kb, uniqueName, members, owner, writable);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        broadcastComponent = component;
        return component;
    }

    public boolean removeEntryProfile(ASIPInterest profile) {
        for (ASIPInterest interest: entryProfiles)
        {
            try {
                if (SharkCSAlgebra.identical(interest, profile)) {
                    entryProfiles.remove(interest);
                    return true;
                }
            } catch (SharkKBException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean checkWithOutProfile() { //TODO
        return true;
    }
}
