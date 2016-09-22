package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.knowledgeBase.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by j4rvis on 22.09.16.
 */
public class SyncMergePropertyList {

    private final static String SYNC_MERGE_PROPERTY_LIST = "SYNC_MERGE_PROPERTY_LIST";
    private final SharkKB storage;

    public SyncMergePropertyList(SharkKB storage) {
        this.storage = storage;
    }

    public void add(PeerSemanticTag peer, SemanticTag kbName, long date){
        SyncMergeProperty syncMergeProperty = new SyncMergeProperty(peer, kbName, date);
        add(syncMergeProperty);
    }

    public void add(SyncMergeProperty syncMergeProperty){
        ArrayList<SyncMergeProperty> list = pullList();

        ArrayList<SyncMergeProperty> temp = getByPeer(list, syncMergeProperty.getPeer());

        if(!temp.isEmpty()){
            temp = getByKbName(temp, syncMergeProperty.getKbName());

            if(!temp.isEmpty()){
                for(SyncMergeProperty property : temp){
                    if(property.getDate() < syncMergeProperty.getDate()){
                        list.remove(property);
                        list.add(syncMergeProperty);
                    }
                }
            }
        }

        list.add(syncMergeProperty);
        pushList(list);
    }

    public ArrayList<SyncMergeProperty> getByPeer(ArrayList<SyncMergeProperty> properties, PeerSemanticTag peer){
        ArrayList<SyncMergeProperty> list = new ArrayList<>();

        for (SyncMergeProperty mergeDate : properties){
            if(SharkCSAlgebra.identical(mergeDate.getPeer(), peer)){
                list.add(mergeDate);
            }
        }
        return list;
    }

    public SyncMergeProperty get(PeerSemanticTag peer, SemanticTag kbName){
        ArrayList<SyncMergeProperty> syncMergeProperties = pullList();

        ArrayList<SyncMergeProperty> byPeer = getByPeer(syncMergeProperties, peer);
        ArrayList<SyncMergeProperty> byKbName = getByKbName(byPeer, kbName);

        if(byKbName.isEmpty()) return null;

        // Can not be more than one entity - SHOULD not be! --> Test!
        return byKbName.iterator().next();
    }

    public ArrayList<SyncMergeProperty> getByKbName(ArrayList<SyncMergeProperty> properties, SemanticTag kbName){
        ArrayList<SyncMergeProperty> list = new ArrayList<>();

        for (SyncMergeProperty mergeDate : properties){
            if(SharkCSAlgebra.identical(mergeDate.getKbName(), kbName)){
                list.add(mergeDate);
            }
        }
        return list;
    }

    public ArrayList<SyncMergeProperty> getBeforeDate(ArrayList<SyncMergeProperty> properties, long date){
        ArrayList<SyncMergeProperty> list = new ArrayList<>();

        for (SyncMergeProperty mergeDate : properties){
            if(mergeDate.getDate() < date){
                list.add(mergeDate);
            }
        }
        return list;
    }

    public ArrayList<SyncMergeProperty> getAfterDate(ArrayList<SyncMergeProperty> properties, long date){
        ArrayList<SyncMergeProperty> list = new ArrayList<>();

        for (SyncMergeProperty mergeDate : properties){
            if(mergeDate.getDate() > date){
                list.add(mergeDate);
            }
        }
        return list;
    }


    private ArrayList<SyncMergeProperty> pullList(){
        ArrayList<SyncMergeProperty> temp = new ArrayList<>();

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

    private void pushList(ArrayList<SyncMergeProperty> list ){
        if(list.isEmpty()) return;

        String s = asString(list);

        try {
            this.storage.setProperty(SYNC_MERGE_PROPERTY_LIST, s);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    private String asString(ArrayList<SyncMergeProperty> list){
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        for ( SyncMergeProperty date : list){
            array.put(date.asString());
        }

        object.put(SYNC_MERGE_PROPERTY_LIST, array);

        return object.toString();
    }

    private ArrayList<SyncMergeProperty> asList(String serialized){
        if(serialized.isEmpty()) return null;

        ArrayList<SyncMergeProperty> tempList = new ArrayList<>();

        JSONObject object = new JSONObject(serialized);

        if(object.has(SYNC_MERGE_PROPERTY_LIST)){
            JSONArray jsonArray = object.getJSONArray(SYNC_MERGE_PROPERTY_LIST);

            for( Object entry : jsonArray){
                SyncMergeProperty date = new SyncMergeProperty((String) entry);
                tempList.add(date);
            }
        }
        return tempList;
    }

}
