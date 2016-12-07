package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.asip.serialization.ASIPKnowledgeConverter;
import net.sharkfw.asip.serialization.ASIPMessageSerializer;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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
public class SyncMergeKP extends KnowledgePort {

    private SyncManager syncManager;

    public SyncMergeKP(SharkEngine se, SyncManager syncManager) {
        super(se);
        this.syncManager = syncManager;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        if(!SharkCSAlgebra.identical(message.getType(), SyncManager.SHARK_SYNC_MERGE_TAG))
            return;

        if(message.getCommand()!=ASIPMessage.ASIP_INSERT)
            return;

        // TODO Message not correct serialized! no physical sender!

        L.d(this.se.getOwner().getName() + " received a Merge from " + message.getPhysicalSender().getName(), this);

        SyncComponent component = syncManager.getComponentByName(message.getTopic());

        if(component == null) return;

        SyncKB syncKB = component.getKb();

        // check allowed sender .. better make that with black-/whitelist
        // deserialize kb from content

        try {
            syncKB.putChanges((SharkKB) asipKnowledge);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {

    }
}
