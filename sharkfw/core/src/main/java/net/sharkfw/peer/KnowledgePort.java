package net.sharkfw.peer;

import net.sharkfw.asip.ASIPSpace;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.SharkStub;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;

/**
 * This is the abstract superclass of all implementations of Knowledge Ports.
 * It implements the most important algorithms for contextualization, and
 * extraction and assimilation.
 *
 * It also offers some methods to provide some information on the {@link net.sharkfw.knowledgeBase.Interest}.
 *
 * Every KP covers exactly one Interest.
 * It will process incoming requests and call the appropriate mehtods
 * for handling them. Right now there are two possible methods which can be called:
 * <ul>
 * <li><code>doExpose()</code> if an expose command has been received</li>
 * <li><code>doInsert()</code> if an insert command has been received</li>
 * </ul>
 *
 * These methods are <code>abstract</code> and need to be implemented in subclasses.
 * That enables application programmes to implement their own logic for handling those requests.
 *
 * A standard implemenation can be found in {@link KnowledgePort}.
 * 
 * 
 * @author thsc
 * @author mfi
 */
abstract public class KnowledgePort {

    protected SharkCS interest;
    protected SharkCS receivedInterest; // TODO: Use!
    protected SharkKB kb;
    protected SharkStub sharkStub;
    private boolean isStarted = false;
    protected ArrayList<KPListener> listeners = new ArrayList();
    private String id = null;
    protected SharkEngine se;
    private PrivateKey privateKey;
    private AccessListManager accessList;

    /**
     * Section 5.1 requires this constructor
     * 
     * @param se
     * @param kb
     */
    protected KnowledgePort(SharkEngine se, SharkKB kb) {
        this.se = se;
        this.kb = kb;
        if (se != null) {
            this.sharkStub = se.getKepStub();
            se.addKP(this);
        }
    }
    
    public KnowledgePort(SharkEngine se) {
        this(se, null);
    }

    // All constructors below should probably go to KnowledgePort
    /**
     * Set the <code>SharkKB</code> this peer uses.
     *
     * @param kb An instance of <code>SharkKB</code> that is used by the local <code>SharkEngine</code>
     */
    public void setKB(SharkKB kb) {
        this.kb = kb;
    }
    
    public SharkKB getKB() {
        return this.kb;
    }
    
    /**
     * Create individual access list manager for this knowledge port.
     * @param uniqueName to make black annd white list persistent
     * @param kb storage for list entries - data are stored as properties
     */
    public void attachAccessListManager(String uniqueName, SharkKB kb) {
        this.accessList = new AccessListManager(uniqueName, kb);
    }
    
    public AccessListManager getAccessListManager() {
        return this.accessList;
    }

    /**
     * Set the <code>KEPStub</code> that is used as a protocol engine.
     *
     * @param kepStub An instance of <code>KEPStub</code> that is used as the protocol engine by the local <code>SharkEngine</code>
     */
    public void setSharkStub(SharkStub kepStub) {
        this.sharkStub = kepStub;
        this.sharkStub.addListener(this);
    }

    /**
     * Return whether the {@link net.sharkfw.knowledgeBase.Interest} inside the KP is a sending interest.
     *
     * @return <code>true</code> if the interest of this AbstractKP is a sending interest
     * (has <code>ContextSpace.OUT</code> set on its DIRECTION dimension. <code>false</code> otherwise.
     */
    public boolean isOKP() {
        SharkCS i = this.getInterest();
        if(i != null) {
            int dimension = this.getInterest().getDirection();

            return (dimension == SharkCS.DIRECTION_OUT || 
                    dimension == SharkCS.DIRECTION_INOUT);
        } else {
            return false;
        }
    }

    /**
     * Return whether the {@ link Interest} inside this AbstractKP is a receiving interest.
     *
     * @return <code>true</code> if the interest of this AbstractKP is a receiving interest. <code>false</code> otherwise.
     */
    public boolean isIKP() {
        SharkCS i = this.getInterest();
        if(i != null) {
            int dimension = this.getInterest().getDirection();

            return (dimension == SharkCS.DIRECTION_IN || 
                    dimension == SharkCS.DIRECTION_INOUT);
        } else {
            return false;
        }
        
    }

    /**
     * Return the {@link Interest} that this AbstractKP handles.
     * 
     * @return The interest which is kept inside this AbstractKP.
     * @return null if no interest was set
     */
    public SharkCS getInterest() {
        return this.interest;
    }
    
    /**
     * Set interest. Knowledge port makes a copy of this interest.
     * @param interest 
     */
    protected void setInterest(SharkCS interest) {
        try {
            this.interest = InMemoSharkKB.createInMemoCopy(interest);
        } catch (SharkKBException ex) {
            this.interest = interest;
        }
    }

    /** 
     * Make this AbstractKP stop listening to incoming requests.
     */
    public void stop() {
        this.sharkStub.withdrawListener(this);
        this.isStarted = false;
    }

    /**
     * Make this AbstractKP start listening to incoming requests, by registering it on the KEPStub.
     */
    public void start() {
        // listen again
        this.sharkStub.addListener(this);
        this.isStarted = true;
        this.se.addKP(this);
    }
    
    public synchronized final boolean handleMessage(ASIPInMessage msg, 
            ASIPConnection con) {

        this.doProcess(msg, con);
        return con.responseSent();
    }
    
    /********************************************************
     *   parse KEP message - call KEPEngine for handling    *
     ********************************************************/
    /**
     * This is the central method for handling incoming messages.
     * It determines the way of communication that is being used to transmit
     * the message and checks the received command to call
     * <ul>
     * <li><code>doExpose()</code> or</li>
     * <li><code>doInsert()</code></li>
     * </ul>
     * accordingly.
     *
     * It also gives some debug information.
     * 
     * @see #doExpose(net.sharkfw.peer.KEPRequest)
     * @see #doInsert(net.sharkfw.peer.KEPRequest)
     *
     * @param msg Request retrieved by a KEP Stub
     */
    public synchronized final boolean handleMessage(KEPInMessage msg) {
        L.d("KP.handleMessage()", this);
        
        // check black-/white list
        PeerSemanticTag sender = null;
        try {
            sender = msg.getSender();
        } catch (SharkKBException ex) {
            //
        }
        
        // check access list management
        
        // has got this k its own access manager
        WhiteAndBlackListManager accessManager = this.getAccessListManager();
        if(accessManager == null) {
            // no - take engine
            accessManager = this.se;
        }
        
        if(!accessManager.isAccepted(sender)) {
            // not allowed to access this kp or engine in general
            
            // create log message
            String senderSI = "sender not transmitted";
            if(sender != null) {
                senderSI = sender.getSI()[0];
            }
            
            L.l("stop handling request because sender is not welcome due to black/white list: " + senderSI, this);
            return false;
        }
        // end access list management

        // Let the request know which handler is holding it
        msg.setKEPHandler(this);

        // get kep command
        int cmd = msg.getCmd();

        // debugging:
        SharkCS msgInterest = msg.getInterest();

        if (msgInterest != null) {
            this.receivedInterest = msgInterest;
        }

        // set security setting
        this.se.initSecurity(msg);
        
        switch (cmd) {
            case KEPInMessage.KEP_INSERT:
                try {
                    this.doInsert(msg.getKnowledge(), msg);
                } catch (Exception ex) {
                    L.e("Error while handling insert request:\n" + ex.getMessage(), this);
                }
                break;
            case KEPInMessage.KEP_EXPOSE:
                try {
                    this.doExpose(msg.getInterest(), msg);
                } catch (Exception ex) {
                    L.e("Error while handling expose request:\n" + ex.getMessage(), this);
                }
                break;
        }

        boolean responded = msg.responseSent();
        msg.resetResponse();

        return responded;
    }

    /**
     * Place logic for handling an insert command in this method.
     *
     * @param knowledge
     * @param kepConnection
     * @see net.sharkfw.kep.KEPResponse
     * @see net.sharkfw.peer.KEPRequest
     * @deprecated 
     */
    protected abstract void doInsert(Knowledge knowledge, KEPConnection kepConnection);

    /**
     * Place logic for handling an insert command in this method.
     *
     * @param asipKnowledge
     * @param asipConnection
     * @see net.sharkfw.kep.KEPResponse
     * @see net.sharkfw.peer.KEPRequest
     */
    protected void doInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        // legacy wrapper
        // create knowledge object
        try {
            Knowledge k = InMemoSharkKB.legacyCreateKEPKnowledge(asipKnowledge);

            this.doInsert(k, (KEPConnection) asipConnection);
        }
        catch(SharkKBException e) {
            L.w("problems when performing legacy wrapper doInsert: " + e.getMessage());
        }
    }

    protected void doProcess(ASIPInMessage msg, ASIPConnection con){

        // TODO
        L.d("about handling ASIP message");

        // Do a lot of other stuff here.. add what is required, see below

        // ...

        switch (msg.getCommand()) {
            case ASIPMessage.ASIP_INSERT:
                try {
                    this.doInsert(msg.getKnowledge(), con);
                } catch (Exception ex) {
                    L.e("Error while handling insert request:\n" + ex.getMessage(), this);
                }
                break;
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    this.doExpose(msg.getInterest(), con);
                } catch (Exception ex) {
                    L.e("Error while handling expose request:\n" + ex.getMessage(), this);
                }
                break;
            case ASIPMessage.ASIP_RAW:
                try {
                    this.doRaw(con.getInputStream(), con);
                } catch (Exception ex) {
                    L.e("Error while handling expose request:\n" + ex.getMessage(), this);
                }
                break;
        }

    }

    /**
     * Place logic for handling an expose command in this method.
     *
     * @param interest
     * @param kepConnection
     * @see net.sharkfw.kep.KEPResponse
     * @see net.sharkfw.peer.KEPRequest
     * @deprecated 
     */
    protected abstract void doExpose(SharkCS interest, KEPConnection kepConnection);
    
    /**
     * standard behaviour: map to doExpose variant of KEP to provide backward
     * compatibility 
     * @param interest
     * @param asipConnection 
     */
    protected void doExpose(ASIPSpace interest, ASIPConnection asipConnection) throws SharkKBException {
        // produce a KEP interest based on LASP-interest
        
        STSet topics = interest.getTopics();
        
        // we take the first approver - rest is lost..
        PeerSemanticTag originator = null;
        PeerSTSet approvers = interest.getApprovers();
        if(approvers != null) {
            originator = approvers.peerTags().nextElement();
        }
        
        PeerSTSet peers = null;
        
        PeerSemanticTag sender = interest.getSender();
        if(sender != null) {
            peers = InMemoSharkKB.createInMemoPeerSTSet();
            peers.merge(sender);
        }
        
        PeerSTSet remotePeers = interest.getReceivers();
        TimeSTSet times = interest.getTimes();
        SpatialSTSet locations = interest.getLocations();
        int direction = interest.getDirection();
        
        SharkCS kepInterest = InMemoSharkKB.createInMemoInterest(topics, originator, peers, 
                remotePeers, times, locations, direction);
        
        this.doExpose(kepInterest, asipConnection.asKepConnection());
    }
    
    /**
     * Standard behaviour: do nothing
     * @param is
     * @param kepConnection 
     */
    protected void doRaw(InputStream is, ASIPConnection asipConnection) {
    }
    
    /**
     * Has this AbstractKP been started to handle requests?
     * @return <code>true</code> if active , <code>false</code> if stopped.
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * Add a KPListener to this Knowledge Port.
     *
     * @param listener Listener impl to be added
     * 
     */

	@SuppressWarnings("unchecked")
    public final void addListener(KPListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Withdraw the given listener from this Knowledge Port.
     *
     * @param listener Listener impl to be removed
     */
    public void removeListener(KPListener listener) {
        this.listeners.remove(listener);
    }

    // ==========================================================================
    // Notification helper methods
    /**
     * Notifiy all Listeners for event 'expose sent'.
     * 
     * @param kp The kp instance that sent the expose.
     * @param mutualinterest The interest that has been sent.
     */
    protected void notifyExposeSent(KnowledgePort kp, SharkCS mutualinterest) {
        Iterator listenerIter = this.listeners.iterator();

        while (listenerIter.hasNext()) {
            KPListener kpl = (KPListener) listenerIter.next();
            kpl.exposeSent(kp, mutualinterest);
        }
    }

    /**
     * Notify all listeners for event 'insert sent'
     * 
     * @param kp The kp instance that sent the insert.
     * @param k The knowledge sent.
     */
    protected void notifyInsertSent(KnowledgePort kp, Knowledge k) {
        Iterator listenerIter = this.listeners.iterator();

        while (listenerIter.hasNext()) {
            KPListener kpl = (KPListener) listenerIter.next();
            kpl.insertSent(kp, k);
        }
    }

    /**
     * Notify all listener for event 'knowledge assimilated'
     * 
     * @param kp The kp instance that assimilated knowledge.
     * @param cp The {@link net.sharkfw.knowledgeBase.ContextPoint} that has been assimilated.
     */
    protected void notifyKnowledgeAssimilated(KnowledgePort kp, ContextPoint cp) {
        Iterator listenerIter = this.listeners.iterator();

        while (listenerIter.hasNext()) {
            KPListener kpl = (KPListener) listenerIter.next();
            kpl.knowledgeAssimilated(kp, cp);
        }
    }

    /**
     * Notify all listener for event 'received knowledge'
     * 
     * @param k The knowledge that was received.
     */
    protected void notifyKnowledgeReceived(Knowledge k) {
//        Enumeration listenerEnum = this.listeners.elements();
//
//        while (listenerEnum.hasMoreElements()) {
//            KPListener kpl = (KPListener) listenerEnum.nextElement();
//            kpl.receivedKnowledge(k);
//        }
    }
    
    /**
     * send knowledge to all recipients - this call is delegated to shark engine
     * @param k knowledge to be sent
     * @param recipientAddresses addresses in Shark format. mail://... tcp://... etc.
     */
    public void sendKnowledge(Knowledge k, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        /*
        String[] addresses = recipient.getAddresses();
        this.sendKnowledge(k, addresses);
        */
        
        this.se.sendKnowledge(k, recipient, this);
    }
    
    /**
     * Send current interest to recipient
     * @param recipient 
     */
    public void publish(PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        this.sendInterest(this.getInterest(), recipient);
    }
   
    /**
     * send knowledge to all recipients - this call is delegated to shark engine
     * @param k knowledge to be sent
     * @param recipientAddresses addresses in Shark format. mail://... tcp://... etc.
     */
    @SuppressWarnings("deprecation")
    public void sendInterest(SharkCS interest, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        this.se.sendInterest(interest, recipient, this);
    }
    
    /**
     * Return an Array of FragmentationParameters like:
     * <code>new FragmentationParameter(false, false, 0);</code>
     *
     * @return An Array of size <code>ContextSpace.MAXDIMENSIONS</code> with FPs
     */
    public static FragmentationParameter[] getZeroFP() {
        FragmentationParameter fp[] = new FragmentationParameter[SharkCS.MAXDIMENSIONS];

        for (int i = 0; i < SharkCS.MAXDIMENSIONS; i++) {
            fp[i] = new FragmentationParameter(false, false, 0);
        }
        return fp;
    }
}