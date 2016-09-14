package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;
import org.apache.maven.wagon.providers.ssh.jsch.ScpCommandExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncManager {

    public interface SyncInviteListener {
        void onInvitation(SyncComponent component);
    }

    private static SyncManager sInstance = new SyncManager();
    private List<SyncComponent> components = new ArrayList<>();
    private List<SyncInviteListener> listeners = new ArrayList<>();

    public static SyncManager getInstance() {
        return sInstance;
    }
    private SyncManager() { }

    public void allowInvitation(boolean allow){
        // TODO activate or deactivate InvitationKP
    }

    public SyncComponent createSyncComponent(
            SharkKB kb,
            SemanticTag uniqueName,
            PeerSTSet member,
            PeerSemanticTag owner,
            boolean writable) {

        if(getComponentByName(uniqueName)!= null) return null;

        SyncComponent component = new SyncComponent(kb, uniqueName, member, owner, writable);
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
