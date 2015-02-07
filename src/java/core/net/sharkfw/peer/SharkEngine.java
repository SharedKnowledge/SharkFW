package net.sharkfw.peer;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import net.sharkfw.kep.*;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoContextPoint;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.pki.SharkPublicKeyStorage;
import net.sharkfw.protocols.*;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Util;

/**
 * This class is the facade for the Shark-System. It provides a single interface to the user/developer where
 * he or she will be able to configure and run his or her instance of Shark. It allows the creation of
 * <code>KnowledgePort</code>s , interests (mostly <code>LocalInterest</code>s), as well as the starting
 * and stopping of network services.
 *
 * It offers access to different KB implementations, depending on the platform.
 *
 * TODO: Implement auto-publish on KB-changes!
 * TODO: Implement saving of KPs to disk.
 *
 * @see net.sharkfw.knowledgeBase.SharkKB
 * @see net.sharkfw.kep.SimpleKEPStub
 * @see net.sharkfw.kp.KnowledgePort
 * 
 * @author thsc
 * @author mfi
 */
abstract public class SharkEngine implements WhiteAndBlackListManager {

    // security settings
    private PrivateKey privateKey = null;
    private PeerSemanticTag engineOwnerPeer;
    private SharkPublicKeyStorage publicKeyStorage;
    private SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;
    private SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;


    /**
     * Creates standardKP with simple constructor.
     * 
     * It is deprecated - use KP constructor instead. It isn't useful to
     * handle StandardKP as a special one.
     * 
     * @param interest
     * @param kb
     * @deprecated 
     * @return 
     */
    public StandardKP createKP(SharkCS interest, SharkKB kb) {
        return new StandardKP(this, interest, kb);
    }
    /**
     * The <code>KEPStub</code> implementation that handles all KEP messages,
     * along with message accountin, connection pooling and observing
     * the <code>Environment</code>
     */
    protected KEPStub kepStub;
    /**
     * A collection containing all active <code>LocalInterest</code>'s wrapped up
     * in <code>KnowledgePort</code>s.
     */
    protected Vector<KnowledgePort> kps;
    /**
     * Storage for opened stubs to certain underlying protocols.
     */
    private Stub[] protocolStubs = new Stub[Protocols.NUMBERPROTOCOLS];
    /**
     * The address (in gcf notation) of the relay to use. If set to <code>null</code>
     * no relay will be used.
     */
    protected String relaisaddress;
    /**
     * Reference to the PeerSensor working on this SharkEngine
     */
    @SuppressWarnings("unused")
    private PeerSensor psensor = null;
    /**
     * Reference to the GeoSensor working on this SharkEngine
     */
    @SuppressWarnings("unused")
    private GeoSensor gsensor = null;
    /**
     * Reference to the TimeSensor working on this SharkEngine
     */
    @SuppressWarnings("unused")
    private TimeSensor tsensor = null;
    private static int DEFAULT_TCP_PORT = 7070;
    @SuppressWarnings("unused")
    private static int DEFAULT_HTTP_PORT = 8080;

    /**
     * Empty constructor for new API
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SharkEngine() {
        this.kps = new Vector();
    }

    protected void setKEPStub(KEPStub kepStub) {
        this.kepStub = kepStub;
        //this.environment = this.kepStub.getEnvironment();
    }

    /**
     * TODO: Pr�fen, ob wir finalize() noch brauchen
     */
    @Override
    protected void finalize() {
        this.deleteAllKP();
    }
    
    protected final void setProtocolStub(Stub protocolStub, int type) throws SharkProtocolNotSupportedException {
        if(protocolStub == null) { return; }
        
        this.removeProtocolStub(type);
        
        if(type < this.protocolStubs.length) {
            this.protocolStubs[type] = protocolStub;
        } else {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }
    }
    
    protected final void removeProtocolStub(int type) throws SharkProtocolNotSupportedException {
        if(type < this.protocolStubs.length) {
            Stub stub = this.protocolStubs[type];
            if(stub != null) {
                stub.stop();
            }
            
            this.protocolStubs[type] = null;
        } else {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }
    }
    
    public Stub getProtocolStub(int type) throws SharkProtocolNotSupportedException {
        if(type >= this.protocolStubs.length) {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }
        
        // already created ?
        if(this.protocolStubs[type] != null) {
            return this.protocolStubs[type];
        }
        
        Stub protocolStub = null;
        // create
        switch (type) {
            case net.sharkfw.protocols.Protocols.TCP:
                protocolStub = this.createTCPStreamStub(this.getKepStub(), DEFAULT_TCP_PORT, false);
                break;
//            case net.sharkfw.protocols.Protocols.UDP:
//                return this.startUDPMessageStub(handler);
//            case net.sharkfw.protocols.Protocols.BT_L2CAP:
//                return this.startBTL2CAPMessageStub(handler);
//            case net.sharkfw.protocols.Protocols.BT_RFCOMM:
//                return this.startBTRFCOMMMessageStub(handler);
//            case net.sharkfw.protocols.Protocols.HTTP:
//                return this.createTCPStreamStub(this.getKepStub(), DEFAULT_HTTP_PORT, true);
            case net.sharkfw.protocols.Protocols.MAIL:
                protocolStub = this.createMailStreamStub(this.getKepStub());
                break;
                
            case net.sharkfw.protocols.Protocols.WIFI_DIRECT:
                protocolStub = this.createWifiDirectStreamStub(this.getKepStub());
                break;

        }

        if(protocolStub != null) {
            this.protocolStubs[type] = protocolStub;
            return protocolStub;
        } else {
            throw new SharkProtocolNotSupportedException(Integer.toString(type));
        }
    }
    
    /**
     * Start networking using a certain protocol.
     *
     * @see net.sharkfw.protocols.Protocols
     *
     * @param type Int value to represent the protocol.
     * @return true on success, false on failure
     * @throws SharkProtocolNotSupportedException
     */
    protected final boolean startProtocol(int type) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = this.getProtocolStub(type);
        
        if(protocolStub != null) {
            protocolStub.start();
            return true;
        }
        
        return false;
    }

    /**
     * Start networking an a given Port (if applicable on networking technology, presumably TCP or UDP).
     *
     * @see net.sharkfw.protocols.Protocols
     *
     * @param type Int value to represent the protocol
     * @param port Int vlaue to represent the port number
     * @return true on success, false on failure TODO: failure is announced by exception!
     * @throws SharkProtocolNotSupportedException
     */
    @SuppressWarnings("unused")
    protected final boolean start(int type, int port) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = this.startServer(type, kepStub, port);
        
        return true;
    }

    /**
     * Start networking an a given Port (if applicable on networking technology, presumably TCP or UDP).
     *
     * @see net.sharkfw.protocols.Protocols
     *
     * @param type Int value to represent the protocol
     * @param port Int vlaue to represent the port number
     * @return true on success, false on failure TODO: failure is announced by exception!
     * @throws SharkProtocolNotSupportedException
     */
    protected final boolean start(int type) throws SharkProtocolNotSupportedException, IOException {
        return this.startProtocol(type);
    }

    /**
     * Starting all available protocols:
     * <ul>
     * <li>TCP</li>
     * <li>Mail</li>
     * <li>HTTP</li>
     * </ul>
     */
    public void start() throws IOException {
        for (int i = 0; i < Protocols.NUMBERPROTOCOLS; i++) {
            try {
                this.startProtocol(i);
            } catch (SharkProtocolNotSupportedException ex) {
                L.d("protocol not supported: ", i);
            }
        }
    }
    
    public void startWifiDirect() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support wifi direct");
    }
    
    public void startTCP(int port) throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support tcp");
    }
    
    public void startMail() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support e-mail");
    }
    
    /**
     * 
     * @return true if tcp stub is running - false otherwise
     */
    public boolean tcpProtocolRunning() {
        return this.isProtocolStarted(Protocols.TCP);
    }

    /**
     * Return a Stub which holds a connection to the targetadress.     
     * If no connection exists, it will be created.
     *
     * @see net.sharkfw.protocols.Stub
     * 
     * @param recipientAddress
     * @return An instance of <code>Stub</code>, which holds the connection to <code>recipientAddress</code>.
     * @throws SharkProtocolNotSupportedException
     */
    Stub getStub(String recipientAddress) throws SharkProtocolNotSupportedException {
        int protocolType = Protocols.getValueByAddress(recipientAddress);

        return this.getProtocolStub(protocolType);
    }

    /**
     * Stops networking for a given protocol.
     *
     * @throws net.sharkfw.kep.SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Protocols
     *
     * @param type Int value representing the protocol
     */
    public void stopProtocol(int type) throws SharkProtocolNotSupportedException {
        Stub protocolStub = this.protocolStubs[type];
        
        if(protocolStub != null) {
            protocolStub.stop();
            
            this.removeProtocolStub(type);
        }
    }
    
    public void stopTCP() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.TCP);
    }

    public void stopWifiDirect() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.WIFI_DIRECT);
    }

    public void stopMail() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.MAIL);
    }

    /**
     * Adds a <code>KnowledgePort</code> to this <code>SharkEngine</code>.
     * Most KnowledgePorts call this method fromn their constructors.
     * Needs to be invisible to the user in the future.
     *
     * @see net.sharkfw.kp.KnowledgePort
     * 
     * @param kp The instance of <code>KnowledgePort</code> to add.
     */
    void addKP(KnowledgePort kp) {
        kp.setKEPStub(this.kepStub);
        kps.add(kp);
    }
    
    /**
     * Usually, interests come from outside over a network connection and are received
     * and handled by a KEPStub. This method works the opposite way. The interest
     * is taken and offered any open knowledge port which can react on it.
     * 
     * This method can be used in at least to cases:
     * 
     * <ol>
     * <li> Establishing an ad hoc connection start usually by getting an
     * address. One partner must now establish a KEP connection. It could be done
     * in this way: A anonymous peer semantic tag is created. The network address
     * is added. An interest is created with no entries but the peer. This interest
     * is send to this method and KP can decide to establish a KEP connection based
     * on the already establised ad hoc connection.
     * 
     * <li> Some application may want to store interest, e.g. to store interest
     * which hadn't been interesting when receiving them. This method allows 
     * <i> replying </i> that interest.
     * 
     * </ol>
     * 
     * Not yet implemented...
     * 
     * @param interest 
     */
    public void handleInterest(SharkCS interest) {
        KEPInMessage internalMessage = new KEPInMessage(this, 
                KEPMessage.KEP_EXPOSE, interest, this.getKepStub());
        
        this.getKepStub().handleMessage(internalMessage);
    }
    
    /**
     * Takes a connection and handles it. 
     * Can be used to set up a connection in an ad hoc network.
     * 
     * Active interest will be sent over this connection to trigger
     * a reply from connected peer.
     * 
     * @param con 
     */
    public void handleConnection(StreamConnection con) {
        // creates an empty interest - which is interpreted as any interest.
        Interest anyInterest = InMemoSharkKB.createInMemoInterest();
        anyInterest.setDirection(SharkCS.DIRECTION_INOUT);
        
        KEPInMessage internalMessage = new KEPInMessage(this, 
                KEPMessage.KEP_EXPOSE, anyInterest, con, this.getKepStub());
        this.getKepStub().handleMessage(internalMessage);
    }

    /**
     * Define a default KP that handles messages that no other KP likes to
     * reply to.
     * 
     * KEP messages are handled with knowledge ports. It happens that no 
     * KP is willing to handle a request which won't be seldom.
     * 
     * Applications can define a <i>default knowledge port</i>. It will only 
     * be called if no other KP has handled the request. 
     * 
     * It's behaves like the default branch in a switch statement. If any
     * comparision fails - default will be called.
     * 
     * @param kp 
     */
//    public void setDefaultKP(KnowledgePort kp) {
//        // remove this kp from usual KP list
//        this.kps.remove(kp);
//        
//        // add as default
//        this.kepStub.setNotHandledRequestKP(kp);
//    }
//
//    public void resetDefaultKP() {
//        this.kepStub.resetNotHandledRequestKP();
//    }

    /**
     * Return all KP which are currently registered in this SharkEngine.
     * @return enumeration of objects of class KP
     * @see net.sharkfw.peer.AbstractKP
     */
    public Enumeration<KnowledgePort> getKPs() {
        return this.kps.elements();
    }
    
    /**
     * @return
     */
    public Iterator<KnowledgePort> getAllKP() {
        EnumerationChain<KnowledgePort> kpIter = new EnumerationChain<KnowledgePort>();
        kpIter.addEnumeration(this.getKPs());
        return kpIter;
    }    

    /**
     * Stops and removes <code>KnowledgePort</code>
     * @param kp The <code>KnowledgePort</code> to remove.
     */
    public void deleteKP(KnowledgePort kp) {
        kp.stop();
        this.kps.remove(kp);
    }

    /**
     * Runs <code>deleteKP()</code> for every <code>KnowledgePort</code> in
     * this <code>SharkEngine</code>
     *
     * @see net.sharkfw.peer.SharkEngine#deleteKP(net.sharkfw.kp.KnowledgePort) 
     */
    public void deleteAllKP() {

        while (this.kps.size() != 0) {
            KnowledgePort kp = (KnowledgePort) this.kps.elementAt(0);
            this.deleteKP(kp);
        }
    }
    
    public PeerSemanticTag getOwner() {
        return this.engineOwnerPeer;
    }    
    
    /*********************************************************************
     *               black- and white list management                    *
     *********************************************************************/
    
    /**
     * can be overwritten and will be replace soon when black-/white list
     * management is integrated into the framework
     * @param sender
     * @return 
     */
//    public boolean isAccepted(PeerSemanticTag sender) {
//        return true;
//    }
    
    /*********************************************************************
     *               moved from system factory to here                   *
     *********************************************************************/
//    boolean udpAvailable = true, tcpAvailable = true, b2l2capAvailable = true, btrfcommAvailable = true, httpAvailable = true;

//    private MessageStub startBTRFCOMMMessageStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        if (this.btrfcomm == null && this.btrfcommAvailable) {
//            try {
//                this.btrfcomm = this.createBTRFCOMMStub(handler);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.btrfcommAvailable = false;
//                throw e;
//            }
//        }
//        
//        return this.btrfcomm;
//    }

//    private StreamStub createTCPStreamStub(RequestHandler handler, int port, boolean isHTTP) throws SharkProtocolNotSupportedException {
//        if (isHTTP) {
//            try {
//                this.http = this.createTCPStreamStub(handler, port, true);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.httpAvailable = false;
//                throw e;
//            }
//            return this.http;
//        } else {
//            try {
//                this.tcp = this.createTCPStreamStub(handler, port, false);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.tcpAvailable = false;
//                throw e;
//            }
//            return this.tcp;
//        }
//    }

//    private StreamStub startTCPStreamStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        return this.createTCPStreamStub(handler, Protocols.ARBITRARY_PORT, false);
//    }

//    private MessageStub startUDPMessageStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        if (this.udp == null && this.udpAvailable) {
//            try {
//                this.udp = this.createUDPMessageStub(handler);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.udpAvailable = false;
//                throw e;
//            }
//        }
//
//        return this.udp;
//    }
//
//    private MessageStub startBTL2CAPMessageStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        if (this.btl2cap == null && this.b2l2capAvailable) {
//            try {
//                this.btl2cap = this.createBTL2CAPStub(handler);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.b2l2capAvailable = false;
//                throw e;
//            }
//        }
//
//        return this.btl2cap;
//    }
    @SuppressWarnings("unused")
    private boolean mailAvailable = true; // a guess

//    private MessageStub startMailMessageStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        if (this.mailMessageStub == null && this.mailAvailable) {
//            try {
//                this.mailMessageStub = this.createMailStub(handler);
//            } catch (SharkProtocolNotSupportedException e) {
//                this.mailAvailable = false; // obviously not available
//                L.w("Mail Stub not available", e);
//                throw e;
//            }
//        }
//
//        return this.mailMessageStub;
//    }

//    private StreamStub createMailStreamStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
//        if (this.mailStreamStub == null && this.mailAvailable) {
//            try {
//                this.mailStreamStub = this.createMailStreamStub(handler);
//            } catch (SharkProtocolNotSupportedException ex) {
//                this.mailAvailable = false;
//                L.w("Mail Streamstub not available", this);
//                throw ex;
//            }
//        }
//        return this.mailStreamStub;
//    }
//
    /**
     * Start a new protocol server for the given protocol.
     *
     * @param protocol The protocol as an int value.
     * @param handler The request handler, for handling incoming requests
     * @param port The port to open.
     * @return A new Stub instance for the given protocol type.
     * @throws SharkProtocolNotSupportedException
     */
    protected final Stub startServer(int protocol, RequestHandler handler, int port) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = null;
        
        switch (protocol) {
            case net.sharkfw.protocols.Protocols.TCP:
                protocolStub = this.createTCPStreamStub(handler, port, false);
                break;
                
//            case net.sharkfw.protocols.Protocols.HTTP:
//                protocolStub = this.createTCPStreamStub(handler, port, true);
//                break;
        }
        
        if(protocolStub != null) {
            this.setProtocolStub(protocolStub, protocol);
            protocolStub.start();
            
            return protocolStub;
        } else {
            System.err.println("port not supported");
            throw new SharkProtocolNotSupportedException(Integer.toString(protocol));
        }
    }

    protected Stub startServer(int protocol, RequestHandler handler) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = this.getProtocolStub(protocol);
        
        protocolStub.start();
        
        return protocolStub;
    }

    /***************************************************************
     * following methods should be overwritten in derived classes  *
     ***************************************************************/
    protected StreamStub createTCPStreamStub(RequestHandler handler, int port, boolean isHTTP) throws SharkProtocolNotSupportedException {
        if (isHTTP) {
            throw new SharkProtocolNotSupportedException("HTTP no supported");
        } else {
            throw new SharkProtocolNotSupportedException("TCP no supported");
        }
    }

    protected MessageStub createMailStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Mail not supported");
    }

    protected StreamStub createMailStreamStub(RequestHandler handler) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Mail per Stream not supported");
    }

    protected Stub createWifiDirectStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Wifi not supported in that version");
    }
    
    /********************************************************************
     *                  Serialization stuff                             *
     ********************************************************************/
    protected int kFormat = KEPMessage.XML;

    /**
     * Returns the serialization format
     *
     * @see net.sharkfw.kep.KEPMessage
     *
     * @return An integer value representing the serialization format for knowledge
     */
    public int getKnowledgeFormat() {
        return this.kFormat;
    }

    /**
     * Set the serialization format for knowledge
     *
     * @see net.sharkfw.kep.KEPMessage
     * 
     * @param format An integer value representing the format to use
     * @throws SharkNotSupportedException
     */
    void setKnowledgeFormat(int format) throws SharkNotSupportedException {
        if (format > KEPMessage.MAXNUMBER || format < 0) {
            throw new SharkNotSupportedException("unknwon format: " + format);
        }

        this.kFormat = format;
    }

    /**
     * Returns an instance of <code>KnowledgeSerializer</code> for a given serialization type.
     *
     * @see net.sharkfw.kep.KEPMessage
     * 
     * @param format An integer value representing the format to use
     * @return An instance of <code>KnowlegdeSerializer</code> for the given serialization type.
     * @throws SharkNotSupportedException
     */
    KnowledgeSerializer getKnowledgeSerializer(int format) throws SharkNotSupportedException {
        return KEPMessage.getKnowledgeSerializer(format);
    }

    /**
     * Return the currently used instance of <code>KnowledgeSerializer</code>
     *
     * @return The instance of <code>KnowledgeSerializer</code> that's being used by this engine.
     */
    KnowledgeSerializer getKnowledgeSerializer() {
        try {
            return KEPMessage.getKnowledgeSerializer(this.kFormat);
        } catch (Exception e) {
            // shouldn't happen
            return KEPMessage.getKnowledgeSerializer();
        }
    }

    /*************************************************************
     *                      some getter                          *
     *************************************************************/
    /**
     * Return the currently used KEPStub.
     *
     * @return The KEPStub, currently used by this SharkEngine.
     */
    public KEPStub getKepStub() {
        return kepStub;
    }
    private long kepSessionTimeOut = 3000;

    /**
     * Return the connectionout in milliseconds, which is used for KEPSessions.
     * @return The connectiontimeout for KEPSessions in milliseconds.
     */
    public long getConnectionTimeOut() {
        return this.kepSessionTimeOut;
    }

    /**
     * Set the timeout value for KEPSessions (in milliseconds)
     * @param millis The amount of milliseconds after which an idle KEPSession is terminated.
     */
    public void setConnectionTimeOut(long millis) {
        this.kepSessionTimeOut = millis;
    }

    /**
     * Return whether or not the SharkEngine has any open communication stubs.
     * @return <code>true</code> if this SharkEngine has at least one open communication stub, <code>false</code> otherwise.
     */
    public boolean isStarted() {
        for (int i = 0; i < Protocols.NUMBERPROTOCOLS; i++) {
            if (this.protocolStubs[i] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Return whether or not the protocol specified by the int value is
     * started on this SharkEngine or note.</p>
     * 
     * <p>Example: <br />
     * <code>boolean mailStarted = engine.isProtocolStarted(Protocols.MAIL)</code>
     * </p>
     * 
     * @param protocol
     * @return <code>true</code> if the given protocol is started, <code>false</code> if not.
     */
    public boolean isProtocolStarted(int protocol) {
        try {
            Stub protocolStub = this.getProtocolStub(protocol);
            
            return protocolStub.started();
            
        } catch (SharkProtocolNotSupportedException ex) {
            // cannot happen
            return false;
        }
    }

    /**
     * Publish the interest of that kp to the environment.
     * 
     * @param kp <code>KnowledgePort</code> to publish.
     * @param recipient
     * @throws net.sharkfw.system.SharkSecurityException
     * @throws java.io.IOException
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void publishKP(KnowledgePort kp, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        this.sendInterest(kp.getInterest(), recipient, kp);
    }
    
    public void sendInterest(SharkCS interest, PeerSemanticTag recipient, KnowledgePort kp) throws SharkSecurityException, SharkKBException, IOException {
        this.sendKEPCommand(interest, null, kp, recipient);
    }
    
    public void sendKnowledge(Knowledge k, PeerSemanticTag recipient, KnowledgePort kp) throws SharkSecurityException, SharkKBException, IOException {
        this.sendKEPCommand(null, k, kp, recipient);
    }

    @SuppressWarnings("unused")
    private void sendKEPCommand(SharkCS interest, Knowledge k, KnowledgePort kp, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        L.d("Send KEP command to recipient: >>>>>>>>>>>\n", this);
        
        // See if a response has been sent yet
        boolean sent = false;
        
        /*
         * Read receipient's addresses
         */
        String[] addresses = recipient.getAddresses();

        if (addresses == null) {
            L.e("KP cannot send KEP message: No address in remote peer dimension in interest and no address set in publish found. Aborting.", this);
            return;
        }

        KEPOutMessage response = this.createKEPOutMessage(addresses, recipient);

        if (response != null) {
            // Response could be created

            try {
                // send interest
                if(interest != null) {
                    response.expose(interest);
                }

                // send knowledge
                if(k != null) {
                    response.insert(k);
                }
            }
            catch(IOException e) {
                throw new SharkKBException(e.getMessage());
            }
        }

        if(response != null) {
            // If the response has been sent we are finished.
            sent = response.responseSent();
        }

        L.d("<<<<<<<<<<<<<<<<<< End sending knowledge or interest to recipient", this);
    }
    
    /**
     * Bring receiver addresses in an order. Put addresses up which should
     * be tried first. Message are just send once. Thus, the first valid address
     * is taken.
     * 
     * This method puts stream addresses up. More concret: TCP comes first, than
     * HTTP, than mail, anything else afterwords.
     * 
     * @param addresses
     * @return 
     */
    protected String[] prioritizeAddresses(String[] addresses) {
        if(addresses == null) {
            return null;
        }
        
        String[] orderedAddresses = new String[addresses.length];
        
        // make a copy first
        System.arraycopy(addresses, 0, orderedAddresses, 0, addresses.length);

        // bubble sort
        boolean changed;
        do {
            changed = false;
            String temp;
            for(int i = 0; i < orderedAddresses.length-1; i++) {
                int j = i+1;
                
                if(this.better(orderedAddresses[j], orderedAddresses[i])) {
                    // lower address has "better" address - bubble!
                    temp = orderedAddresses[i];
                    orderedAddresses[i] = orderedAddresses[j];
                    orderedAddresses[j] = temp;
                    changed = true;
                }
            }
        } while(changed);
        
        return orderedAddresses;
    }
    
    /**
     * Defines if address A or B is "better". This implementation
     * is pretty simple: stream protocols are better than message based
     * 
     * @param addrA
     * @param addrB
     * @return 
     */
    protected boolean better(String addrA, String addrB) {
        try {
            int aProtocol = Protocols.getValueByAddress(addrA);
            int bProtocol = Protocols.getValueByAddress(addrB);
            
            if(Protocols.isStreamProtocol(aProtocol) && 
                    !Protocols.isStreamProtocol(bProtocol)) {
                return true;
            }
        }
        catch(SharkProtocolNotSupportedException e) {
            return false;
        }
        
        return false;
    }
    /**
     * Creates a new KEPOutMessage without security initialization.
     * 
     * @param addresses
     * @return 
     */
    @SuppressWarnings("rawtypes")
    private KEPOutMessage createKEPOutMessage(String[] addresses) {
        KEPOutMessage response = null;
        MessageStub mStub;
        StreamStub sStub;
        StreamConnection sConn = null;
        
        if(addresses == null) {
            return null;
        }
        
        // sort addresses first
        addresses = this.prioritizeAddresses(addresses);

        Enumeration addrEnum = Util.array2Enum(addresses);
        while (addrEnum.hasMoreElements()) {            
            String address = (String) addrEnum.nextElement();
            L.d("sendInterest: try address:"+address, this);
            //boolean fromPool = false;
            try {
                /*
                 * Check if stub is available
                 */
                
                int type = Protocols.getValueByAddress(address);
                Stub protocolStub = this.getProtocolStub(type);

                /*
                 * Find out which protocol to use
                 */
                if (protocolStub instanceof StreamStub) {
                    sStub = (StreamStub) protocolStub;
                    //        sConn = this.kepStub.getConnectionByAddress(address);
                    //      if(sConn == null) {
                    try {
                        sConn = sStub.createStreamConnection(address);
                    }
                    catch(RuntimeException re) {
                        throw new SharkException(re.getMessage());
                    }
                    //    } else {
                    //  fromPool = true;
                    //  }
                    response = new KEPOutMessage(this, sConn, KEPMessage.getKnowledgeSerializer(this.kFormat));
                } else {
                    mStub = (MessageStub) protocolStub;
                    response = new KEPOutMessage(this, mStub, KEPMessage.getKnowledgeSerializer(this.kFormat), address);
                }
            } catch (SharkNotSupportedException ex) {
                L.e(ex.getMessage(), this);
//                ex.printStackTrace();
                continue;
            } catch (IOException ex) {
                L.e(ex.getMessage(), this);
//                ex.printStackTrace();
                continue;
            } catch (SharkProtocolNotSupportedException spn) {
                L.e(spn.getMessage(), this);
//                spn.printStackTrace();
                continue;
            } catch(SharkException sse) {
                L.w("cannot create KEP message: " + sse.getMessage(), this);
                continue;
            }

            if (sConn != null /*&& !fromPool*/) {
                this.kepStub.handleStream(sConn);
            }
            
            // one kep message is enough
            if(response != null) {
                return response;
            }
        }
        
        return null;
    }
    
    /**
     * Create a KEP message that shall be send to on (!) of those addresses
     * @return 
     */
    private KEPOutMessage createKEPOutMessage(String[] addresses, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException {
        KEPOutMessage response = this.createKEPOutMessage(addresses);

        if(response != null) {
            this.initSecurity(response, recipient);
        }
        return response;
    }
    
    /**
     * Create a message as reply on an already received message
     * @return 
     */
    KEPOutMessage createKEPOutResponse(StreamConnection con, 
            String[] addresses, PublicKey publicKeyRemotePeer, 
            String[] remotePeerSI, boolean encrypted, boolean signed)
                throws SharkKBException, SharkSecurityException, SharkException {
        
        L.d("Creating new KEP reply:", this);
                
        KEPOutMessage response = null;

        // is there already a stub?
        if(con != null) {
            // we take existing stream connection

            response = new KEPOutMessage(this, con, 
                    this.getKnowledgeSerializer());
        }
        else { // there is no open connection
            response = this.createKEPOutMessage(addresses);
        }
        
        if(response == null) {
            throw new SharkException("couldn't create KEP reponse message");
        }
            
        ///////////////////////////////////////////////////////////////////
        //                       setting up security                     //
        ///////////////////////////////////////////////////////////////////
    
        // check reply policy what todo
        
        // pre set all parameter with those are found in original request
        PrivateKey useThisPrivateKey = this.privateKey;
        
        String[] useThisSI = null;
        PeerSemanticTag seOwner = this.getOwner();
        if(seOwner != null) {
            useThisSI = seOwner.getSI();
        }

        PublicKey useThisPublicKey = publicKeyRemotePeer;
        boolean sign = true;

        ///////////////////////////////////////////////////////////
        //                 "as defined" policy                   //
        ///////////////////////////////////////////////////////////
        if(this.replyPolicy == SecurityReplyPolicy.AS_DEFINED) {
            
            // encryption - set or unset public key remote peer ///
            
            if(this.encryptionLevel == SecurityLevel.NO) {
                // no encryption at all
                useThisPublicKey = null;
            } 
            else if(this.encryptionLevel == SecurityLevel.MUST) {
                if(useThisPublicKey == null && remotePeerSI != null) {
                    // there is no public key - maybe we have it on oki store
                    useThisPublicKey = this.publicKeyStorage.getPublicKey(
                                                            remotePeerSI);
                }
                
                if(useThisPublicKey == null) {
                    throw new SharkSecurityException("security policy declares encryption a MUST but no public key of remote peer can be found - fatal - message not sent");
                }
            }
            // else SecurityLevel.IF_POSSIBLE; - nothing todo
            
            // signing - set or unset private key and si to identify this peer
            
            if(this.signatureLevel == SecurityLevel.NO) {
                // no signing - remove public key and si
                sign = false;
            }
            else if(this.signatureLevel == SecurityLevel.MUST) {
                if(useThisPrivateKey == null || useThisSI == null) {
                    throw new SharkSecurityException("security policy declares encryption a MUST but no private key set or no SI of this peer found - fatal - message not sent");
                }
            }
            // else SecurityLevel.IF_POSSIBLE; - nothing todo
        } 
        
        ///////////////////////////////////////////////////////////
        //                 "(try) same" policy                   //
        ///////////////////////////////////////////////////////////
        else {
            // encryption

            // if message wasn't encrypted - don't encrypt either
            if(!encrypted) {
                useThisPublicKey = null;
            } else {
                if(this.replyPolicy == SecurityReplyPolicy.SAME) {
                    // we must encrypt
                    if(useThisPublicKey == null && remotePeerSI != null) {
                        // there is no public key - maybe we have it on oki store
                        useThisPublicKey = this.publicKeyStorage.getPublicKey(
                                                                remotePeerSI);
                    }
                    if(useThisPublicKey == null) {
                        throw new SharkSecurityException("security policy is SAME AS MESSAGE and message was encrypted but cannot find public key - fatal - message not sent");
                    }
                } // TRY_SAME. nothing todo
            }

            // siging
            if(!signed) {
                sign = false;
            } else {
                // we like to sign
                sign = true;
                if(useThisPrivateKey == null || useThisSI == null) {
                    if(this.replyPolicy == SecurityReplyPolicy.SAME) {
                        // we must sign - test must not fail
                        throw new SharkSecurityException("security policy is SAME AS MESSAGE and message was signed but no private key set or no SI of this peer found - fatal - message not sent");
                    } else {
                        // we wanted but cannot
                        sign = false;
                    }
                }
            }
        }
        
        response.initSecurity(useThisPrivateKey, useThisPublicKey, useThisSI, sign);
  
        return response;
    }
    
    private void initSecurity(KEPOutMessage msg, PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException {
        if(recipient != null) {
            this.initSecurity(msg, recipient.getSI());
        } else {
            this.initSecurity(msg, (String[]) null);
        }
    }
    
    private void initSecurity(KEPOutMessage msg, String[] recipientSIs) throws SharkSecurityException, SharkKBException {
        // set public and private key fpr encryption and signing if needed.
        
        String recipientSI = "no recipient set - not so good";
        if(recipientSIs != null) {
            recipientSI = recipientSIs[0];
        }
        
        L.d("Init security. 1st recipient si is: " + recipientSI, this);
        
        PublicKey publicKey = null;
        String[] sendingPeerSIString = null;
        
        if(this.engineOwnerPeer != null) {
            sendingPeerSIString = this.engineOwnerPeer.getSI();
        }
        
        boolean sign = true;

        PrivateKey useThisPrivateKey = this.privateKey;
        
        if(this.encryptionLevel != SharkEngine.SecurityLevel.NO 
                && this.publicKeyStorage != null) {

            // try to find recipient public key
            if (recipientSIs == null) {
                if(this.encryptionLevel == SharkEngine.SecurityLevel.MUST) {
                    throw new SharkSecurityException("encryption level is MUST but no recipient defined which is required to find its public key");
                }
            } else {
                publicKey = this.publicKeyStorage.getPublicKey(recipientSIs);
                
                if(sendingPeerSIString == null) {
                    throw new SharkSecurityException("encryption level is MUST - engine owner is not set but it is required to allowed communication partner find its public key");
                }
            }
            
            if(useThisPrivateKey == null) {
                throw new SharkSecurityException("encryption level is MUST but no private key found to wrap session key");
            }
        }
        
        if(this.signatureLevel != SharkEngine.SecurityLevel.NO) {
            if(signatureLevel == SharkEngine.SecurityLevel.MUST 
                    && (useThisPrivateKey == null || this.engineOwnerPeer == null)) {
                throw new SharkSecurityException("signing level is MUST but private key or peer si or both are missing");
            } else if(this.engineOwnerPeer != null) {
                // we can sign and should do it
                sendingPeerSIString = this.engineOwnerPeer.getSI();
            } // else we have no peer - no signing
        } else { // no signing at all
            sign = false;
        }
        
        // init request with both key which can be null if level is IF_POSSIBLE
        msg.initSecurity(useThisPrivateKey, publicKey, sendingPeerSIString, sign);
    }

    /**
     * That message iterates all remote peers in kp interest and exposes that 
     * interest to them
     * @param kp
     * @throws net.sharkfw.system.SharkSecurityException
     * @throws java.io.IOException
     */ 
    public void publishKP(KnowledgePort kp) throws SharkSecurityException, IOException {
        L.d("publishKP() started", this);
        /**
         * If no relay address is set
         * send the interest to every peer
         * on the REMOTEPEER dimension.
         */
        SharkCS interest = kp.getInterest();
        // Return if no interest was set
        if (interest == null) {
            return;
        }
        try {
            PeerSTSet recipients = (PeerSTSet) interest.getSTSet(SharkCS.DIM_REMOTEPEER);
            Enumeration<SemanticTag> recipientTags = recipients.tags();
            while (recipientTags != null && recipientTags.hasMoreElements()) {
                PeerSemanticTag ropst = (PeerSemanticTag) recipientTags.nextElement();
                this.publishKP(kp, ropst);
            }
        } catch (SharkKBException ex) {
            L.e(ex.getMessage(), this);
            ex.printStackTrace();
        }

        L.d("publishKP() ended", this);
    }

    public void publishAllKP() throws SharkSecurityException, IOException {
        L.d("Publishing all KPs", this);
        // Find all KPs
        @SuppressWarnings("rawtypes")
        Enumeration<KnowledgePort> kpEnum = this.kps.elements();

        // publish one by one to the environment
        while (kpEnum.hasMoreElements()) {
            KnowledgePort kp = kpEnum.nextElement();

            this.publishKP(kp);
        }

    }

    public void publishAllKP(PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        L.d("Publishing all KPs", this);

        // Find all KPs
        @SuppressWarnings("rawtypes")
        Enumeration kpEnum = this.kps.elements();

        // Publish them one by one to the recipient
        while (kpEnum.hasMoreElements()) {
            KnowledgePort kp = (KnowledgePort) kpEnum.nextElement();
            this.publishKP(kp, recipient);
        }
    }

    public void stop() {
        for (int i = 0; i < Protocols.NUMBERPROTOCOLS; i++) {
            try {
                this.stopProtocol(i);
            } catch (SharkProtocolNotSupportedException ex) {
                L.d("protocol not supported: ", i);
            }
        }
    }

    /**
     * Get a reference to the PeerSensor this SharkEngine uses. If no PeerSensor
     * is supported, null is returned.
     *
     * @return An instance of PeerSensor, or null if none is supported
     * @deprecated 
     */
    public abstract PeerSensor startPeerSensor();

    /**
     * Stop the currently running PeerSensor.
     * @deprecated 
     */
    public abstract void stopPeerSensor();

    /**
     * Get a reference to the GeoSensor this SharkEngine uses. If no GeoSensor
     * is supported, null is returned.
     *
     * @return An instance of GeoSensor, or null if none is supported.
     * @deprecated 
     */
    public abstract GeoSensor getGeoSensor();

    /**
     * Stop the currently running GeoSensor.
     * @deprecated 
     */
    public abstract void stopGeoSensor();

    /**
     * Sutart the GeoSensor
     */
    public abstract void startGeoSensor();

    public abstract void persist() throws SharkKBException;

    public enum SecurityLevel { MUST, IF_POSSIBLE, NO}
    public enum SecurityReplyPolicy { SAME, TRY_SAME, AS_DEFINED }
    
    /**
     * 
     * @param engineOwnerPeer
     * @param privateKey private RSA key of this peer
     * @param peer a PST that signes messages - in most cases it will be 
     * the description of the user who actual runs that software
     * @param publicKeyStorage object providing a Shark public key storage
     * @param encryptionLevel set encryption level of first messages. 
     * MUST: message must be encrypted - if no public key available - no message will be sent
     * IF_POSSIBLE: message will be encrypted if a valid public key can be found
     * NO: message won't be encrypted at all
     * @param signatureLevel set level signature level of first KEP message
     * @param replyPolicy defines security level that should be used in reply of
     * a received call: SAME - the same methods have to be used or no message will 
     * be sent. TRY_SAME: same as SAME but message is also sent if key are missing and
     * signing and/or encryption isn't possible. AS_DEFINED uses same level as defined 
     * with security and encryptipon level. 
     * @param refuseUnverifiably a message cannot be verified if the peer has no 
     * public key. This paramter defines whether the message is to be refused in this 
     * case or not. Note: Messages with wrong signatures are refused in any case.
     */
    @SuppressWarnings("unused")
    public void initSecurity(PeerSemanticTag engineOwnerPeer, 
            SharkPublicKeyStorage publicKeyStorage,
            SecurityLevel encryptionLevel, SecurityLevel signatureLevel,
            SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably) 
            throws SharkSecurityException {
        
        PrivateKey useThisPrivateKey;
        
        try {
            if(publicKeyStorage != null) {
                this.privateKey = publicKeyStorage.getPrivateKey();
            }
        }
        catch(SharkKBException e) {
            this.privateKey = null;
        }
        
        // keep pki store at least for the next few lines of code
        this.publicKeyStorage = publicKeyStorage;
        
        if(encryptionLevel == SharkEngine.SecurityLevel.MUST) {
            if(publicKeyStorage == null) {
                throw new SharkSecurityException("encryption level is MUST but no public key storage available");
            }

            if(this.privateKey == null) {
                throw new SharkSecurityException("encryption level is MUST but no private key in storage found - need private key to unwrap session encryption key");
            }
        } 
        else if(encryptionLevel == SharkEngine.SecurityLevel.NO) {
            // encryption is not allowed - forget PKI storage, if set
            publicKeyStorage = null;
        }
        
        if(signatureLevel == SharkEngine.SecurityLevel.MUST 
                && (this.privateKey == null || engineOwnerPeer == null)) {
            throw new SharkSecurityException("signing level is MUST but private key or peer description missing");
        }
        
        // description of this peer
        this.engineOwnerPeer = engineOwnerPeer;
        
        this.encryptionLevel = encryptionLevel;
        
        this.signatureLevel = signatureLevel;
        
        // remember reply policy
        this.replyPolicy = replyPolicy;
        
        this.refuseUnverifiably = refuseUnverifiably;
        
        // propagate to KEPStub the handles Requests.
        this.kepStub.initSecurity(this.privateKey, this.publicKeyStorage,
                this.encryptionLevel, this.signatureLevel, this.replyPolicy, 
                this.refuseUnverifiably);
    }
    
    void initSecurity(KEPInMessage msg) {
        msg.initSecurity(privateKey, publicKeyStorage, encryptionLevel, 
                signatureLevel, replyPolicy, refuseUnverifiably);
    }
    
    public SharkPublicKeyStorage getPublicKeyStorage() {
        return this.publicKeyStorage;
    }
    
    ////////////////////////////////////////////////////////////////////////
    //                    don't sent information again                    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Knowledge can be sent. It is a number of context points.
     * There are two tests before sending knowledge:
     * 
     * <ul>
     * <li> Are information attaced to CPs already sent to recipient. Those
     * information are removed from knowledge
     * <li> Does a context point contain any information. Knowledge is sent if
     * at least a single non-empty context point exists.
     * </ul>
     * 
     * This second rule can be overwritten with this methode. It can
     * be allowed sending empty cps. This can be useful when semantic tags are 
     * the real content that shall be transmitted.
     * 
     * Default is now true.
     * 
     * @param allowed 
     */
    public void setAllowSendingEmptyContextPoints(boolean allowed) {
        this.allowEmptyContextPoints = allowed;
    }
    
    public boolean getAllowSendingEmptyContextPoints() {
        return this.allowEmptyContextPoints;
    }
    
    private final HashMap<Integer,String> deliveredInformation = 
            new HashMap<>();
    
    private boolean allowEmptyContextPoints = true;
    
    /**
     * This methods checks whether information are already sent to a peer
     * @param k knowledge ought to be sent
     * @param address recipient address
     * @return Knowledge with information that are not already sent or null if 
     * all information have already been transmitted
     * @deprecated ???
     */
    @SuppressWarnings("rawtypes")
    public Knowledge removeSentInformation(Knowledge k, String address) {
        
        // create knowledge to be returned
        Knowledge retK = new InMemoKnowledge(k.getVocabulary());
        
        // let's investigate each cp
        Enumeration cpEnum = k.contextPoints();
        while(cpEnum.hasMoreElements()) {
            ContextPoint cp = (ContextPoint)cpEnum.nextElement();
            
            // hung up that point
//            k.removeContextPoint(cp);
            
            InMemoContextPoint newCP = new InMemoContextPoint(cp.getContextCoordinates());
            
            Enumeration infoEnum = cp.enumInformation();
            while(infoEnum.hasMoreElements()) {
                Information info = (Information)infoEnum.nextElement();
                
                Integer hash = new Integer(info.hashCode());
                
                // already in list ?
                String oldAddress = this.deliveredInformation.get(hash);
                
                // already sent?
                boolean sent = false;
                if(oldAddress != null && address.equalsIgnoreCase(oldAddress)) {
                    sent = true;
                }
                
                if(!sent) {
                    // no yet sent - keep it.
                    newCP.addInformation(info);
                }
            }
            
            // something left?
            if(newCP.getNumberInformation() > 0 || this.allowEmptyContextPoints) {
                // add properties from original cp
                Util.copyPropertiesFromPropertyHolderToPropertyHolder(cp, newCP);
                
                // hang in the new cp
                retK.addContextPoint(newCP);
            }
        }
        
        if(retK.getNumberOfContextPoints() > 0) {
            return retK;
        } else {
            return null;
        }
    }
    
    /**
     * this method stores what information are sent to whom in order to suppress
     * duplicates. Note: It only suppresses duplicates to direct communication
     * partners. It does not inspect the remote dimension of context points though.
     * @param k knowledge to be sent
     * @param address communication partners address
     */
    @SuppressWarnings("rawtypes")
    public void setSentInformation(Knowledge k, String address) {
        // lets investigate any cp
        Enumeration cpEnum = k.contextPoints();
        while(cpEnum.hasMoreElements()) {
            ContextPoint cp = (ContextPoint)cpEnum.nextElement();
            
            Enumeration infoEnum = cp.enumInformation();
            
            while(infoEnum.hasMoreElements()) {
                Information info = (Information)infoEnum.nextElement();
                int hash = info.hashCode();
                this.deliveredInformation.put(new Integer(hash), address);
            }
        }
    }
    
    public Iterator<SharkCS> getSentInterests(long since) {
        return this.kepStub.getSentInterests(since);
    }

    public Iterator<Knowledge> getSentKnowledge(long since) {
        return this.kepStub.getSentKnowledge(since);
    }
  
    public Iterator<SharkCS> getUnhandledInterests(long since) {
        return this.kepStub.getUnhandledInterests(since);
    }

    public Iterator<SharkCS> getUnhandledKnowledge(long since) {
        return this.getUnhandledKnowledge(since);
    }

    public void removeSentHistory() {
        this.kepStub.removeSentHistory();
    }
    
    public final static int DEFAULT_SILTENT_PERIOD = 500;
    
    /**
     * There can be a weired situation in spontaneous networks when 
     * establishing a connection: Two peer start sending simultaneously.
     * It can happen that both peer exchange same message over to different
     * channels. The engine tries to prevent this situation be defining a
     * so called "silent period". Identical interests and knowledge want
     * be sent regardless to what recipient. This methods allows defining
     * that silent period. 
     * 
     * Unit test require a quite short siltent period. In real time, some
     * seconds are usefull. Default is defined in this class with 
     * DEFAULT_SILTENT_PERIOD.
     * 
     * @param milliseconds 
     */
    public void setSilentPeriod(int milliseconds) {
        this.kepStub.setSilentPeriod(milliseconds);
    }
    
    /////////////////////////////////////////////////////////////////
    //                 remember unsent messages                    //
    /////////////////////////////////////////////////////////////////
    
    private SharkKB unsentMessagesKB;
    private static final String UNSENTMESSAGE_SI = "http://www.sharksystem.net/vocabulary/unsentMesssages";
    private SemanticTag unsentMessagesST = InMemoSharkKB.createInMemoSemanticTag("UnsentMessage", UNSENTMESSAGE_SI);
    
    private static final String INTEREST_CONTENT_TYPE = "x-shark/interest";
    private static final String KNOWLEDGE_CONTENT_TYPE = "x-shark/knowledge";
    
    public void setUnsentMessagesKB(SharkKB kb) {
       this.unsentMessagesKB = kb; 
    }
    
    private ContextCoordinates getUnsentCC(PeerSemanticTag recipient) {
        return InMemoSharkKB.createInMemoContextCoordinates(
                this.unsentMessagesST, recipient, null, null, 
                null, null, SharkCS.DIRECTION_NOTHING);
    }
    
    private ContextPoint getUnsentMessageCP(PeerSemanticTag recipient) {
        if(this.unsentMessagesKB != null) {
            try {
                ContextPoint cp = this.unsentMessagesKB.createContextPoint(
                        this.getUnsentCC(recipient));

                return cp;
            }
            catch(SharkKBException e) {
            }
        }
        
        return null;
    }
    
    private XMLSerializer xs = null;
    
    private XMLSerializer getXMLSerializer() {
        if(this.xs == null) {
            this.xs = new XMLSerializer();
        }
        
        return this.xs;
    }

    /**
     * stores unsent message somewhere... TODO
     * @param interest
     * @param recipient 
     */
    public void rememberUnsentInterest(SharkCS interest, PeerSemanticTag recipient) {
        ContextPoint cp = this.getUnsentMessageCP(recipient);
        
        if(cp == null) {
            L.w("cannot save unsent interest: ", this);
            return;
        }
        
        try {
            String interestString = this.getXMLSerializer().serializeSharkCS(interest);
            Information i = cp.addInformation(interestString);
            
            i.setContentType(INTEREST_CONTENT_TYPE);
            
        } catch (SharkKBException ex) {
            L.d("cannot serialize interest", this);
        }
    }
    
    /**
     * stores unsent knowledge somewhere... TODO
     * @param k
     * @param recipient
     */
    public void rememberUnsentKnowledge(Knowledge k, PeerSemanticTag recipient) {
        ContextPoint cp = this.getUnsentMessageCP(recipient);
        
        if(cp == null) {
            L.w("cannot save unsent knowledge: ", this);
            return;
        }
        
        try {
            Information i = cp.addInformation();
            OutputStream os = i.getOutputStream();
            SharkOutputStream sos = new UTF8SharkOutputStream(os);
            this.getXMLSerializer().write(k, sos);
            i.setContentType(KNOWLEDGE_CONTENT_TYPE);
        } catch (Exception ex) {
            L.d("cannot serialize knowledge", this);
        }
    }
    
    /**
     * Re-send unsent messages.
     */
    public void sendUnsentMessages() {
        if(this.unsentMessagesKB != null) {
            try {
                Enumeration<ContextPoint> cpEnum = this.unsentMessagesKB.getAllContextPoints();
                if(cpEnum == null) {
                    return;
                }
                
                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();
                    
                    this.unsentMessagesKB.removeContextPoint(cp.getContextCoordinates());
                    
                    Enumeration<Information> infoEnum = cp.enumInformation();
                    if(infoEnum == null) {
                        continue;
                    }
                    
                    while(infoEnum.hasMoreElements()) {
                        Information i = infoEnum.nextElement();
                        
                        if(i.getContentType().equalsIgnoreCase(INTEREST_CONTENT_TYPE)) {
                            // Interest
                            String serialeInterest = i.getContentAsString();
                            SharkCS deserializeSharkCS = this.getXMLSerializer().deserializeSharkCS(serialeInterest);
                            cp.removeInformation(i);
                            
                            // TODO reset - prevent loop!
                        }
                        else if(i.getContentType().equalsIgnoreCase(KNOWLEDGE_CONTENT_TYPE)) {
                            // knowledge
                            // TODO
                        }
                        
                    }
                }
                
            }
            catch(SharkKBException e) {
                
            }
        }
    }
    
    public void removeUnsentMessages() {
        if(this.unsentMessagesKB != null) {
            try {
                Enumeration<ContextPoint> cpEnum = this.unsentMessagesKB.getAllContextPoints();
                if(cpEnum == null) {
                    return;
                }
                
                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();
                    this.unsentMessagesKB.removeContextPoint(cp.getContextCoordinates());
                }
            }
            catch(SharkKBException e) {
                L.d("problems while iterating stored unsent messages", this);
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////
    //                        list manager methods                         //
    /////////////////////////////////////////////////////////////////////////
    
    abstract protected SystemPropertyHolder getSystemPropertyHolder();
    
    // reimplemented with with delegate
    private AccessListManager accessList = new AccessListManager("Shark_SharkEngine", this.getSystemPropertyHolder());
    
    public void acceptPeer(PeerSemanticTag peer, boolean accept) {
        this.accessList.acceptPeer(peer, accept);
    }

    public void useWhiteList(boolean whiteList) {
        this.accessList.useWhiteList(whiteList);
    }
    
    public void useBlackWhiteList(boolean on) {
        this.accessList.useBlackWhiteList(on);
    }
    
    public boolean isAccepted(PeerSemanticTag peer) {
        return this.accessList.isAccepted(peer);
    }
}

