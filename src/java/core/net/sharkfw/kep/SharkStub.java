package net.sharkfw.kep;

import java.security.PrivateKey;
import java.util.Iterator;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

/**
 * @author thsc
 * @author mfi
 */
public interface SharkStub {
    Iterator<KnowledgePort> getListener();
    
    void addListener(KnowledgePort newListener);

    void withdrawListener(KnowledgePort listener);
    
    void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ SharkPkiStorage sharkPkiStorage,
            SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel, 
            SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably);

    public void handleStream(StreamConnection con);
}
