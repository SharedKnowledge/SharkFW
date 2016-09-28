package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.SharkTask;
import net.sharkfw.system.SharkTaskExecutor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncManager extends SharkTask {

    // Interfaces
    public interface SyncInviteListener {
        void onInvitation(SyncComponent component);
    }
    // Public CONSTANTS
    public static final String SHARK_SYNC_INVITE_TYPE_SI = "http://www.sharksystem.net/sync/invite";
    public static final String SHARK_SYNC_OFFER_TYPE_SI = "http://www.sharksystem.net/sync/offer";
    public static final String SHARK_SYNC_MERGE_TYPE_SI = "http://www.sharksystem.net/sync/merge";
    public static final SemanticTag SHARK_SYNC_INVITE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_INVITE", SHARK_SYNC_INVITE_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_OFFER_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_OFFER", SHARK_SYNC_OFFER_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_MERGE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_MERGE", SHARK_SYNC_MERGE_TYPE_SI);

    // Ports
    private final SyncOfferKP offerKP;
    private final SyncMergeKP syncMergeKP;
    private SyncInviteKP syncInviteKP;

    // Lists
    private final SyncMergePropertyList mergePropertyList;
    // TODO you will just be notified but can't decide if you ant to accept
    private List<SyncInviteListener> listeners = new ArrayList<>();
    private List<SyncComponent> components = new ArrayList<>();
    // Engine
    private SharkEngine engine;

    public SyncManager(SharkEngine engine) {
        this.engine = engine;
        this.offerKP = new SyncOfferKP(this.engine, this, this.engine.getStorage());
        this.syncMergeKP = new SyncMergeKP(this.engine, this);
        this.mergePropertyList = new SyncMergePropertyList(this.engine.getStorage());
    }

    public SyncMergePropertyList getMergePropertyList(){
        return this.mergePropertyList;
    }

    public void allowInvitation(boolean allow){
        if(allow){
            this.syncInviteKP = new SyncInviteKP(this.engine, this);
        } else {
            this.syncInviteKP = null;
        }
    }

    public void startUpdateProcess(long minutes){
        SharkTaskExecutor.getInstance().scheduleAtFixedRate(this, minutes, TimeUnit.MINUTES);
    }

    @Override
    protected Object process() {
        Iterator<SyncComponent> components = this.getSyncComponents();
        while (components.hasNext()){
            SyncComponent next = components.next();

            try {
                Enumeration<PeerSemanticTag> enumeration = next.getApprovedMembers().peerTags();
                while (enumeration.hasMoreElements()){
                    PeerSemanticTag peerSemanticTag = enumeration.nextElement();

                    // get the time when lastseen OR better when the lastMerge was sent

                    // get the changes since that date

                    SyncMergeProperty property = this.mergePropertyList.get(peerSemanticTag, next.getUniqueName());

                    long lastMerged = property.getDate();

                    SharkKB changes = next.getKb().getChanges(lastMerged);

                    // TODO if changes are empty?

                    property.updateDate();

                    this.mergePropertyList.add(property);

                    String serializedChanges = ASIPSerializer.serializeKB(changes).toString();

                    ASIPOutMessage outMessage = this.engine.createASIPOutMessage(
                            peerSemanticTag.getAddresses(),
                            this.engine.getOwner(),
                            peerSemanticTag,
                            null,
                            null,
                            next.getUniqueName(),
                            SyncManager.SHARK_SYNC_MERGE_TAG, 1);

                    outMessage.raw(serializedChanges.getBytes(StandardCharsets.UTF_8));
                }

            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public SyncComponent createSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet member,
            PeerSemanticTag owner,
            boolean writable){

        if(getComponentByName(uniqueName)!= null) return null;

        SyncComponent component = null;
        try {
            component = new SyncComponent(engine, kb, uniqueName, member, owner, writable);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        components.add(component);
        return component;
    }

    public void removeSyncComponent(SyncComponent component){
        components.remove(component);
    }

    public Iterator<SyncComponent> getSyncComponents(){
        return components.iterator();
    }

    public SyncComponent getComponentByName(SemanticTag name){
        for (SyncComponent component : components ){
            if(SharkCSAlgebra.identical(component.getUniqueName(), name) ){
                return component;
            }
        }
        return null;
    }

    public void addInviteListener(SyncInviteListener listener){
        listeners.add(listener);
    }

    public void removeInviteListener(SyncInviteListener listener){
        listeners.remove(listener);
    }

    public void triggerListener(SyncComponent component){
        for (SyncInviteListener listener : this.listeners){
            listener.onInvitation(component);
        }
    }
}
