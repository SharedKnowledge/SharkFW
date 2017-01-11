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

    public SyncMergeInfo get(PeerSemanticTag peer, SemanticTag kbName){
        ArrayList<SyncMergeInfo> syncMergeInfos = pullList();
        ArrayList<SyncMergeInfo> byPeer = getByPeer(syncMergeInfos, peer);
        ArrayList<SyncMergeInfo> byKbName = getByKbName(byPeer, kbName);

        if(byKbName.isEmpty()) return null;

        // Can not be more than one entity - SHOULD not be! --> Test!
        return byKbName.iterator().next();
    }

    private ArrayList<SyncMergeInfo> getByPeer(ArrayList<SyncMergeInfo> infos, PeerSemanticTag peer){
        ArrayList<SyncMergeInfo> list = new ArrayList<>();

        for (SyncMergeInfo mergeDate : infos){
            if(SharkCSAlgebra.identical(mergeDate.getPeer(), peer)){
                list.add(mergeDate);
            }
        }
        return list;
    }

    private ArrayList<SyncMergeInfo> getByKbName(ArrayList<SyncMergeInfo> infos, SemanticTag kbName){
        ArrayList<SyncMergeInfo> list = new ArrayList<>();

        for (SyncMergeInfo mergeDate : infos){
            if(SharkCSAlgebra.identical(mergeDate.getKbName(), kbName)){
                list.add(mergeDate);
            }
        }
        return list;
    }

    private ArrayList<SyncMergeInfo> getBeforeDate(ArrayList<SyncMergeInfo> infos, long date){
        ArrayList<SyncMergeInfo> list = new ArrayList<>();

        for (SyncMergeInfo mergeDate : infos){
            if(mergeDate.getDate() < date){
                list.add(mergeDate);
            }
        }
        return list;
    }

    private ArrayList<SyncMergeInfo> getAfterDate(ArrayList<SyncMergeInfo> infos, long date){
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
