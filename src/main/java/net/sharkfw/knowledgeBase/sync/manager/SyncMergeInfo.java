package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.json.JSONObject;

/**
 * Created by j4rvis on 12/9/16.
 */
public class SyncMergeInfo {

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
