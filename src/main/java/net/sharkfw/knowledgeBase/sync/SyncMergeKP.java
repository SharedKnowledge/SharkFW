package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 * Created by j4rvis on 19.07.16.
 * 
 * @author thsc
 */
public class SyncMergeKP extends ContentPort {
    private PeerSTSet allowedUsers = null;
    private SyncKB syncKB;
    SemanticTag kbTitel;
    
    public SyncMergeKP(SharkEngine se, SyncKB kb, SemanticTag kbTitel, PeerSTSet allowedUsers) {
        super(se);
        this.syncKB = kb;
        this.kbTitel = kbTitel;
        
        try {
            if(allowedUsers != null) {
                this.allowedUsers = InMemoSharkKB.createInMemoCopy(allowedUsers);
            }
        }
        catch(SharkKBException e) {
            // cannot happen..
        }
    }

    public SyncMergeKP(SharkEngine se, SyncKB kb, SemanticTag kbTitel) {
        this(se, kb, kbTitel, null);
    }

    @Override
    protected boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream) {
        message.getTopic();
        if(!SharkCSAlgebra.identical(this.kbTitel, message.getTopic())) return false;
        
        // check allowed sender .. better make that with black-/whitelist
        // deserialize kb from content
        InputStream rawContent = message.getRaw();
        
        SharkKB changes; // that shall be deserialized kb
        
        try {
            // add to kb
            this.syncKB.putChanges(syncKB);

            // we are done :)
        }
        catch(SharkKBException e) {
            // do something useful
        }
        
        return true;
        
    }
}
