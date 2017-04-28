package net.sharkfw.knowledgeBase.sync.manager.history;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.sync.manager.SyncComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by j4rvis on 4/28/17.
 */
public class SyncHistoryEntry {

    private PeerSemanticTag peer;
    private SyncComponent component;
    private List<Long> dates = new ArrayList();

    public SyncHistoryEntry(SyncComponent component, PeerSemanticTag peer) {
        this.component = component;
        this.peer = peer;
        this.dates.add(System.currentTimeMillis());
    }

    public void update(){
        this.dates.add(System.currentTimeMillis());
    }

    public PeerSemanticTag getPeer() {
        return peer;
    }

    public SyncComponent getComponent() {
        return component;
    }

    public long getLastSynced(){
        return Collections.max(this.dates);
    }

    public int getTimesSynced(){
        return this.dates.size();
    }

    // Can be useful if peers are next to each other for a while.
    public long getAverageSyncInterval(int numberOfSyncs){
        numberOfSyncs = (this.dates.size() < numberOfSyncs || numberOfSyncs == 0) ? this.dates.size() : numberOfSyncs;
        List<Long> longList = this.dates;
        Collections.sort(longList, Collections.reverseOrder());

        if(numberOfSyncs!=longList.size()){
            longList = longList.subList(0, numberOfSyncs);
        }

        int overallDifferences = 0;
        long previousLong = 0;
        for (Long aLong : longList) {
            if(previousLong == 0){
                previousLong = aLong;
            } else {
                overallDifferences+= previousLong - aLong;
            }
        }
        return overallDifferences/numberOfSyncs;
    }

    public long getAverageSyncInterval(){
        return getAverageSyncInterval(this.dates.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncHistoryEntry that = (SyncHistoryEntry) o;

        if (peer != null ? !peer.equals(that.peer) : that.peer != null) return false;
        return component != null ? component.equals(that.component) : that.component == null;
    }

    @Override
    public int hashCode() {
        int result = peer != null ? peer.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        return result;
    }
}