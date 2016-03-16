package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
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

    public ASIPOutMessage(
            SharkEngine engine,
            StreamConnection connection,
            boolean encrypted, // mandatory
            String encyptedSessionKey, // optional
            String version,  // mandatory
            String format,  // mandatory
            String messageID,  // optional
            PeerSemanticTag sender,  // optional
            SpatialSemanticTag senderLocation,  // optional
            TimeSemanticTag senderTime,  // optional
            PeerSemanticTag receiver,  // optional
            SpatialSemanticTag receiverLocation,  // optional
            TimeSemanticTag receiverTime,  // optional
            String signature // optional
    ) {
        super();
        this.setEncrypted(encrypted);
        this.setEncyptedSessionKey(encyptedSessionKey);
        this.setVersion(version);
        this.setFormat(format);

        // TODO: save rest of parameters

        this.setSignature(signature);
    }

    public void expose(ASIPInterest interest) {
    }

    public void insert(ASIPKnowledge knowledge) {
    }

    public void raw(InputStream stream) {
    }

    public void setSecuritySettings(
            boolean encrypted, // mandatory
            String encyptedSessionKey, // optional
            String version,  // mandatory
            String format){


    }
}
