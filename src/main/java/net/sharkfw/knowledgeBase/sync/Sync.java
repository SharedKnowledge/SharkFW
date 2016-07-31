package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author thsc42
 * @param <SyncEntity>
 */
public abstract class Sync<SyncEntity extends Sync> {
    
    // must be overwritten by each inheriting class
    SyncEntity wrapSyncObject(Object target) {
        throw new RuntimeException("you really MUST overwritte wrapTag in your class :)");
    }
    
    public ArrayList<SyncEntity> wrapSTIter(Sync caller, 
            Iterator<SyncEntity> targets) {
        
        ArrayList tagList = new ArrayList();
        while(targets.hasNext()) {
            tagList.add(caller.wrapSyncObject(targets.next()));
        }
        return tagList;
    }
    
    public ArrayList<SyncEntity> wrapSTEnum(Sync caller, 
            Enumeration<SyncEntity> targets) {
        
        ArrayList tagList = new ArrayList();
        while(targets.hasMoreElements()) {
            tagList.add(caller.wrapSyncObject(targets.nextElement()));
        }
        return tagList;
    }
}
