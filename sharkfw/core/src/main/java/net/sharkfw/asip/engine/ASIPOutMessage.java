package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.Base64;

import java.io.InputStream;

/**
 * Objects of this class are produced by the framework in order
 * to be serialized and transmitted to another peer.
 *
 * @author thsc
 */
public class ASIPOutMessage extends ASIPMessage {

    public ASIPOutMessage(SharkEngine engine,
                          StreamConnection connection,
                          boolean encrypted,
                          String encryptedSessionKey,
                          String version,
                          String format,
                          int command,
                          PeerSemanticTag sender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverSpatial,
                          TimeSemanticTag receiverTime,
                          String signature) throws SharkKBException {
        super(engine,
                connection,
                encrypted,
                encryptedSessionKey,
                version,
                format,
                command,
                sender,
                receiverPeer,
                receiverSpatial,
                receiverTime,
                signature);
    }

    public void expose(ASIPInterest interest) {
    }

    public void insert(ASIPKnowledge knowledge) {
    }

    public void raw(InputStream stream) {
    }

    public void setSecuritySettings(
            boolean encrypted, // mandatory
            String encryptedSessionKey
    ) {

    }
}
