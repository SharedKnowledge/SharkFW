package net.sharkfw.knowledgeBase.sync.manager.history;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by j4rvis on 4/28/17.
 */
public class SyncHistoryManager {

    private SharkKB storage;
    private SyncManager syncManager;
    private ArrayList<SyncHistoryEntry> entries = new ArrayList<>();

    public SyncHistoryManager(SyncManager syncManager, SharkKB storage){
        this.syncManager = syncManager;
        this.storage = storage;
    }

    public void addOrUpdateEntry(SyncComponent component, PeerSemanticTag peer){
        SyncHistoryEntry syncHistoryEntry = new SyncHistoryEntry(component, peer);
        if(entries.contains(syncHistoryEntry)) {
            entries.get(entries.indexOf(syncHistoryEntry)).update();
        } else {
            entries.add(syncHistoryEntry);
        }
    }

    public SyncHistoryEntry getEntries(SyncComponent component, PeerSemanticTag tag){
        int index = entries.indexOf(new SyncHistoryEntry(component, tag));
        if(index==-1){
            return null;
        } else {
            return entries.get(index);
        }
    }

    public List<SyncHistoryEntry> getEntries(SyncComponent component){
        List<SyncHistoryEntry> list = new ArrayList<>();
        for (SyncHistoryEntry entry : entries) {
            if(SharkCSAlgebra.identical(entry.getComponent().getUniqueName(), component.getUniqueName())){
                list.add(entry);
            }
        }
        return list;
    }

    public List<SyncHistoryEntry> getEntries(PeerSemanticTag peerSemanticTag){
        List<SyncHistoryEntry> list = new ArrayList<>();
        for (SyncHistoryEntry entry : entries) {
            if(SharkCSAlgebra.identical(entry.getPeer(), peerSemanticTag)){
                list.add(entry);
            }
        }
        return list;
    }

}
