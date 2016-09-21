package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncManager implements Runnable{

    private final SyncOfferKP offerKP;
    private final SyncMergeKP syncMergeKP;
    private SyncInviteKP syncInviteKP;
    private long intervall;
    private boolean suspended;

    public interface SyncInviteListener {

        void onInvitation(SyncComponent component);
    }
    public static final String SHARK_SYNC_INVITE_TYPE_SI = "http://www.sharksystem.net/sync/invite";

    //    public static final String SHARK_SYNC_ACCEPT_TYPE_SI = "http://www.sharksystem.net/sync/accept";
    public static final String SHARK_SYNC_OFFER_TYPE_SI = "http://www.sharksystem.net/sync/offer";
    public static final String SHARK_SYNC_MERGE_TYPE_SI = "http://www.sharksystem.net/sync/merge";
    public static final SemanticTag SHARK_SYNC_INVITE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_INVITE", SHARK_SYNC_INVITE_TYPE_SI);

    //    public static final SemanticTag SHARK_SYNC_ACCEPT_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_ACCEPT", SHARK_SYNC_ACCEPT_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_OFFER_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_OFFER", SHARK_SYNC_OFFER_TYPE_SI);
    public static final SemanticTag SHARK_SYNC_MERGE_TAG = InMemoSharkKB.createInMemoSemanticTag("SYNC_MERGE", SHARK_SYNC_MERGE_TYPE_SI);
    private SharkEngine engine;

    private List<SyncComponent> components = new ArrayList<>();

    private List<SyncInviteListener> listeners = new ArrayList<>();
    private Thread thread;

    public SyncManager(SharkEngine engine) {
        this.engine = engine;
        this.offerKP = new SyncOfferKP(this.engine, this, this.engine.getStorage());
        this.syncMergeKP = new SyncMergeKP(this.engine, this);
    }

    public void allowInvitation(boolean allow){
        if(allow){
            this.syncInviteKP = new SyncInviteKP(this.engine, this);
        } else {
            this.syncInviteKP = null;
        }
    }

    @Override
    public void run() {

        while (true){

            Iterator<SyncComponent> components = this.getSyncComponents();
            while (components.hasNext()){
                SyncComponent next = components.next();

                try {
                    Enumeration<PeerSemanticTag> enumeration = next.getApprovedMembers().peerTags();
                    while (enumeration.hasMoreElements()){
                        PeerSemanticTag peerSemanticTag = enumeration.nextElement();

                        // get the time when lastseen OR better when the lastMerge was sent

                        // get the changes since that date

                        long lastMerged = 0;

                        SharkKB changes = next.getKb().getChanges(lastMerged);
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

            try {
                Thread.sleep(this.intervall);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void startUpdatingWithInterval(long seconds){
        this.intervall = seconds;

        if(thread==null){
            this.thread = new Thread(this);
            this.thread.start();
        }

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

}
