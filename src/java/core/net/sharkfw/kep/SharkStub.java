package net.sharkfw.kep;

import java.security.PrivateKey;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

/**
 * A KEPStub encapsulates a number (0 .. n) of supported and active
 * protocols. A KEP stub could have e.g. two running server (one
 * listening for incomming UDP-Datagrams, one accepting request for
 * establishing a new Bluetooth RFCOMM connection.
 * 
 * KEPStubs hide upper classes like KP from the underlying protocols.
 * 
 * @author thsc
 * @author mfi
 */
public interface SharkStub extends RequestHandler, KEPConnectionPool, KEPMessageAccounting {
    
    public void addListener(KnowledgePort newListener);

    public  void withdrawListener(KnowledgePort listener);
    
    public  void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ SharkPkiStorage sharkPkiStorage,
            SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel, 
            SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably);
    
}
