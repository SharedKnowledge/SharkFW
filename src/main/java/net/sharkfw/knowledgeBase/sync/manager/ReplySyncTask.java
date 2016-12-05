package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkTask;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by j4rvis on 12/2/16.
 */
public class ReplySyncTask extends SharkTask{

    private final ASIPInMessage message;
    private final PeerSemanticTag physicalSender;
    private final SyncManager syncManager;

    /**
     * Reply to an already established connection to just send related changes.
     * @param message
     */
    public ReplySyncTask(SyncManager syncManager, ASIPInMessage message) {
        this.syncManager = syncManager;
        // Get the other peer
        this.message = message;
        this.physicalSender = message.getPhysicalSender();
    }

    @Override
    protected Object process() {

        Iterator<SyncComponent> components = null;
        try {
            components = syncManager.getSyncComponentsWithPeer(this.physicalSender);
            while (components.hasNext()) {
                SyncComponent next = components.next();

                Enumeration<PeerSemanticTag> enumeration = next.getApprovedMembers().peerTags();
                while (enumeration.hasMoreElements()) {

                    PeerSemanticTag peerSemanticTag = enumeration.nextElement();

                    SharkKB changes = syncManager.getChanges(next, peerSemanticTag);

                    if(changes!=null){
                        String serializedChanges = ASIPSerializer.serializeKB(changes).toString();

                        ASIPOutMessage response = this.message.createResponse(next.getUniqueName(), SyncManager.SHARK_SYNC_MERGE_TAG);

                        response.raw(serializedChanges.getBytes(StandardCharsets.UTF_8));
                    }

                }
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
