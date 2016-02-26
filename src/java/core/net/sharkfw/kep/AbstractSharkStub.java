package net.sharkfw.kep;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

/**
 *
 * @author thsc
 */
public abstract class AbstractSharkStub implements SharkStub {
    private final List<KnowledgePort> kps = new ArrayList<>();
    protected KnowledgePort notHandledRequestsHandler;
    protected SharkEngine se;
    private final HashMap<String, StreamConnection> table = new HashMap<>();

    public AbstractSharkStub(SharkEngine se) {
            this.se = se;
    }
    
    @Override
    public final void addListener(KnowledgePort newListener) {
        // already in there?
        Iterator<KnowledgePort> kpIter = kps.iterator();
        while(kpIter.hasNext()) {
            if(newListener == kpIter.next()) {
                return; // already in - do nothing
            }
        }
        
        // not found - add
        this.kps.add(newListener);
    };

    @Override
    public final void withdrawListener(KnowledgePort listener) {
        this.kps.remove(listener);
    };
    
    @Override
    public Iterator<KnowledgePort> getListener() {
        return this.kps.iterator();
    }
    
    // security stuff
    //protected SharkPublicKeyStorage publicKeyStorage;
    protected SharkPkiStorage sharkPkiStorage;
    protected SharkEngine.SecurityReplyPolicy replyPolicy;
    protected boolean refuseUnverifiably;
    protected SharkEngine.SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    protected SharkEngine.SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    protected PrivateKey privateKey;

    @Override
    public void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ SharkPkiStorage sharkPkiStorage,
            SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel, 
            SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably) {
        
        this.privateKey = privateKey;
        //this.publicKeyStorage = publicKeyStorage;
        this.sharkPkiStorage = sharkPkiStorage;
        this.signatureLevel = signatureLevel;
        this.encryptionLevel = encryptionLevel;
        this.replyPolicy = replyPolicy;
        this.refuseUnverifiably = refuseUnverifiably;
    }
    
    abstract void callAllInterest(KnowledgePort kp, StreamConnection con);
    
    /**
     * Stream was established and can be used for conversion.
     * Something must happen. Best would be to call each knowledge
     * port with an all interest.
     * @param con 
     */
    @Override
    public final void startConversion(StreamConnection con) {
        Iterator<KnowledgePort> kpIter = this.kps.iterator();
        while(kpIter.hasNext()) {
            KnowledgePort kp = kpIter.next();
            this.callAllInterest(kp, con);
        }
    }
}
