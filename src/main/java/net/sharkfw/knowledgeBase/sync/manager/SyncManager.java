package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkTask;
import net.sharkfw.system.SharkTaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private final SyncMergeInfoSerializer mergePropertyList;
    // TODO you will just be notified but can't decide if you ant to accept
    private List<SyncInviteListener> listeners = new ArrayList<>();
    private List<SyncComponent> components = new ArrayList<>();
    // Engine
    private SharkEngine engine;

    public SyncManager(SharkEngine engine) {
        this.engine = engine;
        this.offerKP = new SyncOfferKP(this.engine, this, this.engine.getStorage());
        this.syncMergeKP = new SyncMergeKP(this.engine, this);
        this.mergePropertyList = new SyncMergeInfoSerializer(this.engine.getStorage());
    }

    public SyncMergeInfoSerializer getMergePropertyList() {
        return this.mergePropertyList;
    }

    public void allowInvitation(boolean allow) {
        if (allow) {
            this.syncInviteKP = new SyncInviteKP(this.engine, this);
        } else {
            this.syncInviteKP = null;
        }
    }

    public void triggerSyncReply(ASIPInMessage message) {
        ReplySyncTask replySyncTask = new ReplySyncTask(this, message);
        SharkTaskExecutor.getInstance().submit(replySyncTask);
    }

    public void triggerSync() {
        L.d("Trigger sync started!", this);
        SharkTaskExecutor.getInstance().submit(this);
    }

    public void startUpdateProcess(long minutes) {
        SharkTaskExecutor.getInstance().scheduleAtFixedRate(this, minutes, TimeUnit.MINUTES);
    }

    @Override
    protected Object process() {
        L.d("Sync Process started.", this);
        Iterator<SyncComponent> components = this.getSyncComponents();
        while (components.hasNext()) {
            SyncComponent next = components.next();

            L.d("We have a SyncComponent", this);

            try {
                Enumeration<PeerSemanticTag> enumeration = next.getApprovedMembers().peerTags();
                L.d("Do we have approved Members?", this);
                while (enumeration.hasMoreElements()) {

                    PeerSemanticTag peerSemanticTag = enumeration.nextElement();

                    L.d("We have found " + peerSemanticTag.getName(), this);

                    // get the time when lastseen OR better when the lastMerge was sent

                    // get the changes since that date

                    L.d("peerSemanticTag: " + ASIPMessageSerializerHelper.serializeTag(peerSemanticTag).toString(), this);
                    L.d("next.getUniqueName(): " + ASIPMessageSerializerHelper.serializeTag(next.getUniqueName()).toString(), this);

                    L.d("mergePropertyList: " + this.mergePropertyList.toString(), this);

                    SyncMergeInfo property = this.mergePropertyList.get(peerSemanticTag, next.getUniqueName());

                    L.d("Property is null? " + (property==null), this);

                    L.d(property.asString());

                    long lastMerged = property.getDate();
                    long lastChanges = next.getKb().getTimeOfLastChanges();

                    L.d("lastMerged: " + lastMerged, this);
                    L.d("lastChanges: " + lastChanges, this);

                    if (lastChanges > lastMerged) {

                        L.d("Yeah so start Syncing!!! Send out the Merge!", this);

                        SharkKB changes = next.getKb().getChanges(lastMerged);

                        // TODO if changes are empty?

                        property.updateDate();

                        this.mergePropertyList.add(property);

                        ASIPOutMessage outMessage = this.engine.createASIPOutMessage(
                                peerSemanticTag.getAddresses(),
                                this.engine.getOwner(),
                                peerSemanticTag,
                                null,
                                null,
                                next.getUniqueName(),
                                SyncManager.SHARK_SYNC_MERGE_TAG, 1);

                        outMessage.insert(changes);
                    }

                }

            } catch (SharkKBException e) {
                e.printStackTrace();
                L.d(e.getMessage(), this);
            }
        }
        return null;
    }

    public SharkKB getChanges(SyncComponent component, PeerSemanticTag peerSemanticTag) throws SharkKBException {

        SharkKB changes = null;

        SyncMergeInfo property = this.mergePropertyList.get(peerSemanticTag, component.getUniqueName());

        if(property!=null) {
            // It seems that we have merged with the peer at least once.
            // Now get the changes since the last merge
            long lastMerged = property.getDate();
            long lastChanges = component.getKb().getTimeOfLastChanges();

            L.d("lastMerged: " + lastMerged, this);
            L.d("lastChanges: " + lastChanges, this);

            if (lastChanges > lastMerged) {
                // the last changes are newer than the last time of the merge.
                changes = component.getKb().getChanges(lastMerged);

                // TODO  here we are just presuming, that we will do the merge. But what if not? Should be triggered after sendMerge
//                property.updateDate();
//                this.mergePropertyList.add(property);
            }
        } else {
            // Okay we haven't merged with our peer yet.
            // So our changes represent the whole kb
            changes = component.getKb();
        }
        return changes;
    }

    public SyncComponent createSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet member,
            PeerSemanticTag owner,
            boolean writable) {

        if (getComponentByName(uniqueName) != null) return null;

        SyncComponent component = null;
        try {
            component = new SyncComponent(this, kb, uniqueName, member, owner, writable);
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

    public SyncComponent getComponentByName(SemanticTag name) {
        for (SyncComponent component : components) {
            if (SharkCSAlgebra.identical(component.getUniqueName(), name)) {
                return component;
            }
        }
        return null;
    }

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
                // Oh okay we don't have anyone who should participate
                // Should not be possible because the creator of the component will be added as well.
                return false;
            }
            if(approvedMembers.isEmpty()){
                // for now we have no one who participates in our syncGroup
                // We have to send invites to everyone!
                this.sendInvite(component);
                return false;
            } else {
                // There is at least someone who participates, so we can send out our merge!
                if (!SharkCSAlgebra.identical(members, approvedMembers)) {
                    // There are still some people missing so we are sending out our invites!
                    this.sendInvite(component);
                }
                return true;
            }
        } catch (SharkKBException e) {
            L.e(e.getMessage(), this);
        } finally {
            return false;
        }

    }

    public void sendMerge(SyncComponent component){

        if(!checkInvitation(component)) return;

        PeerSTSet approvedMembers = component.getApprovedMembers();
        // Okay so now we are finished with our invites!
        // Let's send our merge!
        // Now we are iterating all approved members
        Enumeration<PeerSemanticTag> approvedMemberEnumeration = approvedMembers.peerTags();
        while (approvedMemberEnumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = approvedMemberEnumeration.nextElement();
            // Okay now that we have a peer we can send our changes to
            // we have to check if we have already merged with the peer and get the date of the last merge.
            sendMerge(component, peerSemanticTag);
        }
    }

    public void sendMerge(SyncComponent component, PeerSemanticTag peer){

        SharkKB changes = null;
        try {
            if (!component.isInvited(peer)) return;
            changes = getChanges(component, peer);
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
                // TODO !!!THREAD!!!
                // TODO Now trigger the changes in the mergeList
            }
        } catch (SharkKBException e) {
            L.e(e.getMessage(), this);
        }
    }

    public void sendMerge(SyncComponent component, ASIPConnection connection){


//        ASIPOutMessage response = message.createResponse(null, SyncManager.SHARK_SYNC_MERGE_TAG);
//        response.insert(changes);
    }

    public void sendInvite(SyncComponent component) throws SharkKBException {
        Enumeration<PeerSemanticTag> enumeration = component.getMembers().peerTags();
        ArrayList<String> addresses = new ArrayList<>();
        while (enumeration.hasMoreElements()){
            PeerSemanticTag peerSemanticTag = enumeration.nextElement();

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

    public void sendInvite(SyncComponent component, PeerSemanticTag peerSemanticTag){
        try {
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

        ASIPOutMessage message = this.engine.createASIPOutMessage(addresses, logicalSender, null, null, null, null, null, 10);

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

        // TODO expose Thread!!!
        message.expose(interest);
    }

    public void addInviteListener(SyncInviteListener listener) {
        listeners.add(listener);
    }

    public void removeInviteListener(SyncInviteListener listener) {
        listeners.remove(listener);
    }

    public void triggerListener(SyncComponent component) {
        for (SyncInviteListener listener : this.listeners) {
            listener.onInvitation(component);
        }
    }

    private class SyncMergeInfoSerializer {

        private final static String SYNC_MERGE_PROPERTY_LIST = "SYNC_MERGE_PROPERTY_LIST";
        private final SharkKB storage;

        public SyncMergeInfoSerializer(SharkKB storage) {
            this.storage = storage;
        }

        public void add(PeerSemanticTag peer, SemanticTag kbName, long date){
            SyncMergeInfo syncMergeProperty = new SyncMergeInfo(peer, kbName, date);
            add(syncMergeProperty);
        }

        public void add(SyncMergeInfo syncMergeInfo){
            ArrayList<SyncMergeInfo> list = pullList();

            ArrayList<SyncMergeInfo> temp = getByPeer(list, syncMergeInfo.getPeer());

            if(!temp.isEmpty()){
                temp = getByKbName(temp, syncMergeInfo.getKbName());

                if(!temp.isEmpty()){
                    for(SyncMergeInfo property : temp){
                        if(property.getDate() < syncMergeInfo.getDate()){
                            list.remove(property);
                            list.add(syncMergeInfo);
                        }
                    }
                }
            }

            list.add(syncMergeInfo);
            pushList(list);
        }

        public ArrayList<SyncMergeInfo> getByPeer(ArrayList<SyncMergeInfo> infos, PeerSemanticTag peer){
            ArrayList<SyncMergeInfo> list = new ArrayList<>();

            for (SyncMergeInfo mergeDate : infos){
                if(SharkCSAlgebra.identical(mergeDate.getPeer(), peer)){
                    list.add(mergeDate);
                }
            }
            return list;
        }

        public SyncMergeInfo get(PeerSemanticTag peer, SemanticTag kbName){
            ArrayList<SyncMergeInfo> syncMergeProperties = pullList();
            L.d("syncMergeProperties.size: " + syncMergeProperties.size(), this);
            ArrayList<SyncMergeInfo> byPeer = getByPeer(syncMergeProperties, peer);
            L.d("byPeer.size: " + byPeer.size(), this);
            ArrayList<SyncMergeInfo> byKbName = getByKbName(byPeer, kbName);
            L.d("byKbName.size: " + byKbName.size(), this);

            if(byKbName.isEmpty()) return null;

            // Can not be more than one entity - SHOULD not be! --> Test!
            return byKbName.iterator().next();
        }

        public ArrayList<SyncMergeInfo> getByKbName(ArrayList<SyncMergeInfo> infos, SemanticTag kbName){
            ArrayList<SyncMergeInfo> list = new ArrayList<>();

            for (SyncMergeInfo mergeDate : infos){
                if(SharkCSAlgebra.identical(mergeDate.getKbName(), kbName)){
                    list.add(mergeDate);
                }
            }
            return list;
        }

        public ArrayList<SyncMergeInfo> getBeforeDate(ArrayList<SyncMergeInfo> infos, long date){
            ArrayList<SyncMergeInfo> list = new ArrayList<>();

            for (SyncMergeInfo mergeDate : infos){
                if(mergeDate.getDate() < date){
                    list.add(mergeDate);
                }
            }
            return list;
        }

        public ArrayList<SyncMergeInfo> getAfterDate(ArrayList<SyncMergeInfo> infos, long date){
            ArrayList<SyncMergeInfo> list = new ArrayList<>();

            for (SyncMergeInfo mergeDate : infos){
                if(mergeDate.getDate() > date){
                    list.add(mergeDate);
                }
            }
            return list;
        }

        private ArrayList<SyncMergeInfo> pullList(){
            ArrayList<SyncMergeInfo> temp = new ArrayList<>();

            String property = "";
            try {
                property = this.storage.getProperty(SYNC_MERGE_PROPERTY_LIST);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
            if(property != null){
                temp = asList(property);
            }
            return temp;
        }

        private void pushList(ArrayList<SyncMergeInfo> list ){
            if(list.isEmpty()) return;

            String s = asString(list);

            try {
                this.storage.setProperty(SYNC_MERGE_PROPERTY_LIST, s);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }

        private String asString(ArrayList<SyncMergeInfo> list){
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();

            for ( SyncMergeInfo date : list){
                array.put(date.asJSON());
            }

            object.put(SYNC_MERGE_PROPERTY_LIST, array);

            return object.toString();
        }

        private ArrayList<SyncMergeInfo> asList(String serialized){
            if(serialized.isEmpty()) return null;

            ArrayList<SyncMergeInfo> tempList = new ArrayList<>();

            JSONObject object = new JSONObject(serialized);

            if(object.has(SYNC_MERGE_PROPERTY_LIST)){
                JSONArray jsonArray = object.getJSONArray(SYNC_MERGE_PROPERTY_LIST);
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SyncMergeInfo date = new SyncMergeInfo(jsonObject);
                    tempList.add(date);
                }
            }
            return tempList;
        }

        @Override
        public String toString() {
            return asString(pullList());
        }
    }

    private class SyncMergeInfo {

        public final static String PEER_ENTRY = "PEER";
        public final static String KB_NAME_ENTRY = "KB_NAME";
        public final static String DATE_ENTRY = "DATE";


        private PeerSemanticTag peer;
        private SemanticTag kbName;
        private long date;

        public SyncMergeInfo(PeerSemanticTag peer, SemanticTag kbName, long date) {
            this.peer = peer;
            this.kbName = kbName;
            this.date = date;
        }

        public SyncMergeInfo(JSONObject serialized){
            JSONObject object = serialized;

            if(object.has(PEER_ENTRY) && object.has(KB_NAME_ENTRY) && object.has(DATE_ENTRY)){
                try {
                    this.peer = ASIPMessageSerializerHelper.deserializePeerTag(object.getString(PEER_ENTRY));
                    this.kbName = ASIPMessageSerializerHelper.deserializeTag(object.getString(KB_NAME_ENTRY));
                    this.date = object.getLong(DATE_ENTRY);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
            }
        }

        public String asString(){
            return this.asJSON().toString();
        }

        public JSONObject asJSON(){
            JSONObject object = new JSONObject();
            try {
                object.put(PEER_ENTRY, ASIPMessageSerializerHelper.serializeTag(peer).toString());
                object.put(KB_NAME_ENTRY, ASIPMessageSerializerHelper.serializeTag(kbName).toString());
                object.put(DATE_ENTRY, date);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
            return object;
        }

        public PeerSemanticTag getPeer() {
            return peer;
        }

        public SemanticTag getKbName() {
            return kbName;
        }

        public long getDate() {
            return date;
        }

        public void updateDate(){
            this.date = System.currentTimeMillis();
        }
    }
}
