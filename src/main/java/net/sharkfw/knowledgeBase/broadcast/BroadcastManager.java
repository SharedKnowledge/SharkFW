package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncMergeInfo;
import net.sharkfw.knowledgeBase.sync.manager.SyncMergeInfoSerializer;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncAcceptKP;
import net.sharkfw.knowledgeBase.sync.manager.port.SyncMergeKP;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.routing.SemanticRoutingKP;

import java.util.ArrayList;
import java.util.List;
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
    }

    public boolean checkWithEntryProfile(SharkKB newKnowledge, PeerSemanticTag physicalSender, ASIPInMessage message) {

        if (activeEntryProfile == null) return false;
        boolean isInteresting = false;
        try {
            if (newKnowledge.getTopicSTSet() == null || SharkCSAlgebra.isIn(newKnowledge.getTopicSTSet(), broadcastComponent.getKb().getTopicSTSet())) {
                isInteresting = true;
            }
            if (newKnowledge.getSpatialSTSet() == null || SharkCSAlgebra.isIn(newKnowledge.getSpatialSTSet(), broadcastComponent.getKb().getSpatialSTSet())) {
                isInteresting = true;
            }
            if (newKnowledge.getTimeSTSet() == null || SharkCSAlgebra.isIn(newKnowledge.getTimeSTSet(), broadcastComponent.getKb().getTimeSTSet())) {
                isInteresting = true;
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
        return isInteresting;
    }

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
}
