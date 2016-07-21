package net.sharkfw.asip;

import java.security.PrivateKey;
import java.util.Iterator;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.Stub;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

/**
 * @author thsc
 * @author mfi
 */
public interface SharkStub {
    Iterator<ASIPPort> getListener();
    
    void addListener(ASIPPort newListener);

    void withdrawListener(ASIPPort listener);
    
    void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ SharkPkiStorage sharkPkiStorage,
            SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel, 
            SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably);

    public void handleStream(StreamConnection con);
    
    public void handleNewConnectionStream(StreamConnection con);
    
    /**
     * Stream was established and can be used for conversion.
     * Something must happen. Best would be to call each knowledge
     * port with an all kepInterest.
     * @param con 
     */
    public void startConversion(StreamConnection con);
    
    /**
     * Handle an kepInterest which does not necessarily comes over a
     * underlaying protocol.
     * 
     * @param interest
     */
    public void handleInterest(Interest interest);
    
    public void handleASIPInterest(ASIPInterest interest, SharkStub stub);
}
