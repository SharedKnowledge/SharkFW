package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 * Objects of this class are produced by the framework in order
 * to be serialized and transmitted to another peer.
 * 
 * @author thsc
 */
public class ASIPOutMessage extends ASIPMessage {
    
    public ASIPOutMessage(
            boolean encrypted, // mandatory
            String encyptedSessionKey, // optional
            String version,  // mandatory
            String format,  // mandatory
            String messageID,  // optional
            int command,  // mandatory
            PeerSemanticTag sender,  // optional
            SpatialSemanticTag senderLocation,  // optional
            TimeSemanticTag senderTime,  // optional
            PeerSemanticTag receiver,  // optional
            SpatialSemanticTag receiverLocation,  // optional
            TimeSemanticTag receiverTime,  // optional
            String signature // optional
    ) {
        
        this.setEncrypted(encrypted);
        this.setEncyptedSessionKey(encyptedSessionKey);
        this.setVersion(version);
        this.setFormat(format);
        this.setCommand(command);
        
        // TODO: save rest of parameters
        
        this.setSignature(signature);
    }

    
    
}
