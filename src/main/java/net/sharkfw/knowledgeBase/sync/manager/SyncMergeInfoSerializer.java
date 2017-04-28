package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by j4rvis on 12/9/16.
 */
public class SyncMergeInfoSerializer {

    private final static String SYNC_MERGE_PROPERTY_LIST = "SYNC_MERGE_PROPERTY_LIST";
    private final SharkKB storage;

    public SyncMergeInfoSerializer(SharkKB storage) {
        this.storage = storage;
    }

    public void add(SemanticTag kbName, PeerSemanticTag peer){
        SyncMergeInfo syncMergeProperty = new SyncMergeInfo(peer, kbName, System.currentTimeMillis());
        add(syncMergeProperty);
    }

    public void add(SyncMergeInfo syncMergeInfo){
        ArrayList<SyncMergeInfo> list = pullList();
        boolean added = false;
        for (SyncMergeInfo mergeInfo : list) {
            if (SharkCSAlgebra.identical(mergeInfo.getPeer(), syncMergeInfo.getPeer())){
                if (SharkCSAlgebra.identical(mergeInfo.getKbName(), syncMergeInfo.getKbName())){
                    int index = list.indexOf(mergeInfo);
                    list.remove(index);
                    list.add(index, syncMergeInfo);
                    added = true;
                }
            }
        }
        if(!added) list.add(syncMergeInfo);
        pushList(list);
    }

    public SyncMergeInfo get(PeerSemanticTag peer, SemanticTag kbName){
        ArrayList<SyncMergeInfo> info = pullList();
        for (SyncMergeInfo syncMergeInfo : info) {
            if(SharkCSAlgebra.identical(syncMergeInfo.getKbName(), kbName)){
                if (SharkCSAlgebra.identical(syncMergeInfo.getPeer(), peer)){
                    return syncMergeInfo;
                }
            }
        }
        return null;
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
            JSONObject object = new JSONObject(property);
            if(object.has(SYNC_MERGE_PROPERTY_LIST)){
                JSONArray jsonArray = object.getJSONArray(SYNC_MERGE_PROPERTY_LIST);
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SyncMergeInfo date = new SyncMergeInfo(jsonObject);
                    temp.add(date);
                }
            }
        }
        return temp;
    }

    private void pushList(ArrayList<SyncMergeInfo> list ){
        if(list.isEmpty()) return;

        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        for ( SyncMergeInfo date : list){
            array.put(date.asJSON());
        }

        object.put(SYNC_MERGE_PROPERTY_LIST, array);
        String s = object.toString();


        try {
            this.storage.setProperty(SYNC_MERGE_PROPERTY_LIST, s);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }
}
