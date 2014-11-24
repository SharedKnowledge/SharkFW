package net.sharkfw.kep;

import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.pki.SharkPublicKeyStorage;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.SharkEngine.SecurityLevel;
import net.sharkfw.peer.SharkEngine.SecurityReplyPolicy;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.system.InterestStore;
import net.sharkfw.system.KnowledgeStore;

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
public abstract class KEPStub implements RequestHandler, KEPConnectionPool, KEPMessageAccounting {
    
    private static KEPStub env = null;
    /**
     * An integer value that contains the default silence period in which no message is allowed to be sent twice
     */
    protected int silentPeriod = SharkEngine.DEFAULT_SILTENT_PERIOD;

    // security stuff
    protected SharkPublicKeyStorage publicKeyStorage;
    protected SecurityReplyPolicy replyPolicy;
    protected boolean refuseUnverifiably;
    protected SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    protected SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    protected PrivateKey privateKey;

    public boolean handleMessage(KEPInMessage msg) {
        return this.callListener(msg);
    }
    
    /**
     * Add a <code>KEPHandlery</code> to this KEPStub.
     * <code>KEPHandler</code> are called when KEP-messages arrive.
     *
     * @see net.sharkfw.kep.KEPHandler
     * @see net.sharkfw.kep.KEPMessage
     * @see net.sharkfw.kep.KEPResponse
     * @see net.sharkfw.peer.KEPRequest
     *
     * @param listener A listener to handle KEP-messages.
     */
    abstract public void addListener(KnowledgePort listener);
    
    /**
     * Remove a listener from this <code>KEPHandler</code>
     *
     * @see net.sharkfw.kep.KEPHandler
     * @see net.sharkfw.kep.KEPMessage
     * @see net.sharkfw.kep.KEPResponse
     * @see net.sharkfw.peer.KEPRequest

     *
     * @param listener The listener object to remove.
     */
    abstract public void withdrawListener(KnowledgePort listener);

    /**
     * Call all listeners to handle the message.
     *
     * @see net.sharkfw.peer.KEPRequest
     * 
     * @param inMsg The messsage to handle
     * 
     * @return True if at least one handler was able to process the message. False otherwise.
     */
    abstract protected boolean callListener(KEPInMessage inMsg);

    public void initSecurity(PrivateKey privateKey, SharkPublicKeyStorage publicKeyStorage, 
            SecurityLevel encryptionLevel, SecurityLevel signatureLevel, 
            SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably) {
        
        this.privateKey = privateKey;
        this.publicKeyStorage = publicKeyStorage;
        this.signatureLevel = signatureLevel;
        this.encryptionLevel = encryptionLevel;
        this.replyPolicy = replyPolicy;
        this.refuseUnverifiably = refuseUnverifiably;
    }
    
    public abstract void setNotHandledRequestKP(KnowledgePort kp);

    public abstract void resetNotHandledRequestKP();
    
    private InterestStore sentInterests = new InterestStore();
    private KnowledgeStore sentKnowledge = new KnowledgeStore();
    
    private InterestStore unhandledInterests = new InterestStore();
    private KnowledgeStore unhandledKnowledge = new KnowledgeStore();
    
    /**
     * Remember that this interest was send now
     * @param interest 
     */
    protected void rememberInterest(SharkCS interest) {
        this.sentInterests.addInterest(interest);
    }
    
    /**
     * Remember that this interest was send now
     * @param interest 
     */
    protected void rememberKnowledge(Knowledge k) {
        this.sentKnowledge.addKnowledge(k);
    }

    public Iterator<SharkCS> getSentInterests(long since) {
        return this.sentInterests.getInterests(since);
    }

    public Iterator<Knowledge> getSentKnowledge(long since) {
        return this.sentKnowledge.getKnowledge(since);
    }
    
    protected void rememberUnhandledInterest(SharkCS interest) {
        this.unhandledInterests.addInterest(interest);
    }

    protected void rememberUnhandledKnowledge(Knowledge knowledge) {
        // cut information to make it smaller
        if(knowledge != null) {
            Enumeration<ContextPoint> contextPoints = knowledge.contextPoints();
            if(contextPoints != null) {
                while(contextPoints.hasMoreElements()) {
                    ContextPoint cp = contextPoints.nextElement();
                    
                    Enumeration<Information> infoEnum = cp.enumInformation();
                    if(infoEnum != null) {
                        while(infoEnum.hasMoreElements()) {
                            Information info = infoEnum.nextElement();
                            cp.removeInformation(info);
                        }
                    }
                }
            }
            
            // store cp without information - much smaller
            this.unhandledKnowledge.addKnowledge(knowledge);
        }
    }
    
    public Iterator<SharkCS> getUnhandledInterests(long since) {
        return this.unhandledInterests.getInterests(since);
    }

    public Iterator<Knowledge> getUnhandledKnowledge(long since) {
        return this.unhandledKnowledge.getKnowledge(since);
    }
    
    public void removeSentHistory() {
        this.sentInterests = new InterestStore();
        this.sentKnowledge = new KnowledgeStore();

        this.unhandledInterests = new InterestStore();
        this.unhandledKnowledge = new KnowledgeStore();
    }
    
    public void setSilentPeriod(int milliseconds) {
        if(milliseconds > 0) {
            this.silentPeriod = milliseconds;
        }
    }
}
