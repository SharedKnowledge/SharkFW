package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.asip.serialization.ASIPMessageSerializer;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 * Created by j4rvis on 19.07.16.
 * 
 * @author thsc
 */
public class SyncMergeKP extends ContentPort {

    private SyncManager syncManager;

    public SyncMergeKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream) {
        if(!SharkCSAlgebra.identical(message.getType(), SyncManager.SHARK_SYNC_MERGE_TAG))
            return false;

        if(message.getCommand()!=ASIPMessage.ASIP_RAW)
            return false;

        // TODO Message not correct serialized! no physical sender!

        L.d(this.se.getOwner().getName() + " received a Merge from " + message.getPhysicalSender().getName(), this);

        SyncComponent component = syncManager.getComponentByName(message.getTopic());

        if(component == null) return false;

        SyncKB syncKB = component.getKb();

        // check allowed sender .. better make that with black-/whitelist
        // deserialize kb from content

        String text = "";
        Scanner s = new Scanner(message.getRaw(), StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        text = s.hasNext() ? s.next() : "";

        L.d("Length of received Merge: " + text.length(), this);

        SharkKB changes;

        try {
            changes = (SharkKB) ASIPMessageSerializerHelper.deserializeASIPKnowledge(text);
            syncKB.putChanges(changes);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

        return true;
    }
}
