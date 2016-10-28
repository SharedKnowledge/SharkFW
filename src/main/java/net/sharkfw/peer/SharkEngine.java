package net.sharkfw.peer;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPStub;
import net.sharkfw.asip.SharkStub;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.asip.engine.SimpleASIPStub;
import net.sharkfw.asip.engine.serializer.SharkProtocolNotSupportedException;
import net.sharkfw.asip.engine.serializer.XMLSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.manager.SyncManager;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.protocols.*;
import net.sharkfw.system.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.util.*;

/**
 * This class is the facade for the Shark-System. It provides a single interface to the user/developer where
 * he or she will be able to configure and run his or her instance of Shark. It allows the creation of
 * <code>KnowledgePort</code>s , interests (mostly <code>LocalInterest</code>s), as well as the starting
 * and stopping of network services.
 * <p>
 * It offers access to different KB implementations, depending on the platform.
 * <p>
 * TODO: Implement auto-publish on KB-changes!
 * TODO: Implement saving of KPs to disk.
 *
 * @author thsc
 * @author mfi
 * @see net.sharkfw.knowledgeBase.SharkKB
 * @see KnowledgePort
 */
abstract public class SharkEngine implements WhiteAndBlackListManager {

    // security settings
    private PrivateKey privateKey = null;
    private PeerSemanticTag engineOwnerPeer;
    //private SharkPublicKeyStorage publicKeyStorage;
    private SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;
    private SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;

    protected ASIPStub asipStub;

    /**
     * A collection containing all active <code>LocalInterest</code>'s wrapped up
     * in <code>KnowledgePort</code>s.
     */
    protected List<ASIPPort> ports;
    /**
     * Storage for opened stubs to certain underlying protocols.
     */
    private final Stub[] protocolStubs = new Stub[Protocols.NUMBERPROTOCOLS];
    /**
     * The address (in gcf notation) of the relay to use. If set to <code>null</code>
     * no relay will be used.
     */
    protected String relaisaddress;

    private static int DEFAULT_TCP_PORT = 7070;
    @SuppressWarnings("unused")
    private static int DEFAULT_HTTP_PORT = 8080;
    protected ConnectionStatusListener connectionListener = null;
    private SharkKB storage;
    private SyncManager syncManager;

    /**
     * Empty constructor for new API
     */
    public SharkEngine() {
        this.ports = new ArrayList<>();
        this.storage = new InMemoSharkKB();
    }

    public SharkEngine(SharkKB storage) {
        this();
        this.storage = storage;
        this.refreshPersistedASIPPort();
    }

    public SharkKB getStorage() {
        return this.storage;
    }

    public SyncManager getSyncManager() {
        if (this.syncManager == null) {
            this.syncManager = new SyncManager(this);
            this.syncManager.startUpdateProcess(15);
        }
        return this.syncManager;

    }

    public void setEngineOwnerPeer(PeerSemanticTag tag) {
        this.engineOwnerPeer = tag;
    }

    protected void setASIPStub(SimpleASIPStub asipStub) {
        this.asipStub = asipStub;
    }

    /**
     * TODO: Pruefen, ob wir finalize() noch brauchen
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            this.deleteAllKP();
        } finally {
            super.finalize();
        }
    }

    protected final void setProtocolStub(Stub protocolStub, int type) throws SharkProtocolNotSupportedException {
        if (protocolStub == null) {
            return;
        }

        this.removeProtocolStub(type);

        if (type < this.protocolStubs.length) {
            this.protocolStubs[type] = protocolStub;
        } else {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }
    }

    protected final void removeProtocolStub(int type) throws SharkProtocolNotSupportedException {
        if (type < this.protocolStubs.length) {
            Stub stub = this.protocolStubs[type];
            if (stub != null) {
                stub.stop();
            }

            this.protocolStubs[type] = null;
        } else {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }
    }

    public Stub getProtocolStub(int type) throws SharkProtocolNotSupportedException {
        if (type >= this.protocolStubs.length) {
            throw new SharkProtocolNotSupportedException("unknown protocol number: " + type);
        }

        // already created ?
        if (this.protocolStubs[type] != null) {
            return this.protocolStubs[type];
        }

        Stub protocolStub = null;
        // create
        switch (type) {
            case net.sharkfw.protocols.Protocols.TCP:
                if (this.getAsipStub() != null) {
                    protocolStub = this.createTCPStreamStub(this.getAsipStub(), DEFAULT_TCP_PORT, false, null);
                }
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
                if (this.getAsipStub() != null) {
                    protocolStub = this.createMailStreamStub(this.getAsipStub());
                }
                break;

            case net.sharkfw.protocols.Protocols.WIFI_DIRECT:
                if (this.getAsipStub() != null) {
                    protocolStub = this.createWifiDirectStreamStub(this.getAsipStub());
                }
                break;

        }

        if (protocolStub != null) {
            this.protocolStubs[type] = protocolStub;
            return protocolStub;
        } else {
            throw new SharkProtocolNotSupportedException(Integer.toString(type));
        }
    }

    /**
     * Start networking using a certain protocol.
     *
     * @param type Int value to represent the protocol.
     * @return true on success, false on failure
     * @throws SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Protocols
     */
    protected final boolean startProtocol(int type) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = this.getProtocolStub(type);

        if (protocolStub != null) {
            protocolStub.start();
            return true;
        }

        return false;
    }

    /**
     * Start networking an a given Port (if applicable on networking technology, presumably TCP or UDP).
     *
     * @param type Int value to represent the protocol
     * @param port Int vlaue to represent the port number
     * @return true on success, false on failure TODO: failure is announced by exception!
     * @throws SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Protocols
     */
    @SuppressWarnings("unused")
    protected final boolean start(int type, int port, ASIPKnowledge knowledge) throws SharkProtocolNotSupportedException, IOException {
        Stub stub = null;
        if (this.asipStub != null) {
            this.startServer(type, this.asipStub, port, knowledge);
        }

        return true;
    }

    /**
     * Start networking an a given Port (if applicable on networking technology, presumably TCP or UDP).
     *
     * @param type Int value to represent the protocol
     * @return true on success, false on failure TODO: failure is announced by exception!
     * @throws SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Protocols
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

    public void startNfc() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support nfc");
    }

    public void startBluetooth() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support bluetooth");
    }

    public void startTCP(int port) throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support tcp");
    }

    public void startTCP(int port, ASIPKnowledge knowledge) throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support tcp");
    }

    public void startMail() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("device does not support e-mail");
    }

    /**
     * @return true if tcp stub is running - false otherwise
     */
    public boolean tcpProtocolRunning() {
        return this.isProtocolStarted(Protocols.TCP);
    }

    /**
     * Return a Stub which holds a connection to the targetadress.
     * If no connection exists, it will be created.
     *
     * @param recipientAddress
     * @return An instance of <code>Stub</code>, which holds the connection to <code>recipientAddress</code>.
     * @throws SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Stub
     */
    Stub getStub(String recipientAddress) throws SharkProtocolNotSupportedException {
        int protocolType = Protocols.getValueByAddress(recipientAddress);

        return this.getProtocolStub(protocolType);
    }

    /**
     * Stops networking for a given protocol.
     *
     * @param type Int value representing the protocol
     * @throws SharkProtocolNotSupportedException
     * @see net.sharkfw.protocols.Protocols
     */
    public void stopProtocol(int type) throws SharkProtocolNotSupportedException {
        Stub protocolStub = this.protocolStubs[type];

        if (protocolStub != null) {
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

    public void stopNfc() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.NFC);
    }

    public void stopBluetooth() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.BLUETOOTH);
    }

    public void stopMail() throws SharkProtocolNotSupportedException {
        this.stopProtocol(Protocols.MAIL);
    }

    /**
     * Adds a <code>KnowledgePort</code> to this <code>SharkEngine</code>.
     * Most KnowledgePorts call this method fromn their constructors.
     * Needs to be invisible to the user in the future.
     *
     * @param kp The instance of <code>KnowledgePort</code> to add.
     * @see KnowledgePort
     */
    void addKP(ASIPPort kp) {
        if (this.asipStub != null)
            kp.setSharkStub(this.asipStub);
        ports.add(kp);
    }

    public void handleASIPInterest(ASIPInterest interest) {
        this.getAsipStub().handleASIPInterest(interest, this.asipStub);
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
//        this.ports.remove(kp);
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
     *
     * @return enumeration of objects of class KP
     * @deprecated
     */
    public Enumeration<ASIPPort> getPorts() {
        return new Iterator2Enumeration(this.ports.iterator());
    }

    /**
     * @return
     */
    public Iterator<ASIPPort> getAllPorts() {
        EnumerationChain<ASIPPort> kpIter = new EnumerationChain<>();
        kpIter.addEnumeration(this.getPorts());
        return kpIter;
    }

    /**
     * Stops and removes <code>KnowledgePort</code>
     *
     * @param kp The <code>KnowledgePort</code> to remove.
     */
    public void deleteKP(ASIPPort kp) {
        kp.stop();
        this.ports.remove(kp);
    }

    /**
     * Runs <code>deleteKP()</code> for every <code>KnowledgePort</code> in
     * this <code>SharkEngine</code>
     *
     * @see net.sharkfw.peer.SharkEngine#deleteKP(net.sharkfw.peer.ASIPPort)
     */
    public void deleteAllKP() {

        while (!this.ports.isEmpty()) {
            ASIPPort kp = this.ports.get(0);
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
     * moved from system factory to here                   *
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
     * @param handler  The request handler, for handling incoming requests
     * @param port     The port to open.
     * @return A new Stub instance for the given protocol type.
     * @throws SharkProtocolNotSupportedException
     */
    protected final Stub startServer(int protocol, RequestHandler handler, int port, ASIPKnowledge knowledge) throws SharkProtocolNotSupportedException, IOException {
        Stub protocolStub = null;

        switch (protocol) {
            case net.sharkfw.protocols.Protocols.TCP:
                protocolStub = this.createTCPStreamStub(handler, port, false, knowledge);
                break;

//            case net.sharkfw.protocols.Protocols.HTTP:
//                protocolStub = this.createTCPStreamStub(handler, port, true);
//                break;
        }

        if (protocolStub != null) {
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
    protected StreamStub createTCPStreamStub(RequestHandler handler, int port, boolean isHTTP, ASIPKnowledge knowledge) throws SharkProtocolNotSupportedException {
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

    protected Stub createWifiDirectStreamStub(SharkStub sharkStub) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Wifi not supported in that version");
    }

    protected Stub createNfcStreamStub(SharkStub sharkStub) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Nfc not supported in that version");
    }

    protected Stub createBluetoothStreamStub(SharkStub sharkStub) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("Bluetooth not supported in that version");
    }

    /********************************************************************
     * Serialization stuff                             *
     * <p>
     * /*************************************************************
     * some getter                          *
     *************************************************************/

    public ASIPStub getAsipStub() {
        return this.asipStub;
    }

    private long sessionTimeOut = 3000;

    /**
     * Return the connectionout in milliseconds, which is used for KEPSessions.
     *
     * @return The connectiontimeout for KEPSessions in milliseconds.
     */
    public long getConnectionTimeOut() {
        return this.sessionTimeOut;
    }

    /**
     * Set the timeout value for KEPSessions (in milliseconds)
     *
     * @param millis The amount of milliseconds after which an idle KEPSession is terminated.
     */
    public void setConnectionTimeOut(long millis) {
        this.sessionTimeOut = millis;
    }

    /**
     * Return whether or not the SharkEngine has any open communication stubs.
     *
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
     * <p>
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
     * Publish the Interest of that kp to the environment.
     *
     * @param kp        <code>KnowledgePort</code> to publish.
     * @param recipient
     * @throws net.sharkfw.system.SharkSecurityException
     * @throws java.io.IOException
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void publishKP(KnowledgePort kp, PeerSemanticTag recipient)
            throws SharkSecurityException, SharkKBException, IOException {

        if (kp.getAsipInterest() != null)
            this.sendASIPInterest(kp.getAsipInterest(), recipient, kp);
    }

    public void sendASIPInterest(ASIPInterest interest, PeerSemanticTag recipient, KnowledgePort kp)
            throws SharkSecurityException, SharkKBException, IOException {

        this.sendASIPCommand(interest, null, null, kp, recipient);
    }

    public void sendRaw(InputStream is, PeerSemanticTag recipient, KnowledgePort kp)
            throws SharkSecurityException, SharkKBException, IOException {

        this.sendASIPCommand(null, null, is, kp, recipient);
    }

    public void sendASIPKnowledge(ASIPKnowledge knowledge, PeerSemanticTag recipient, KnowledgePort kp)
            throws SharkSecurityException, SharkKBException, IOException {

        this.sendASIPCommand(null, knowledge, null, kp, recipient);
    }

    private void sendASIPCommand(ASIPInterest interest, ASIPKnowledge knowledge, InputStream is, KnowledgePort kp, PeerSemanticTag recipient)
            throws SharkSecurityException, SharkKBException, IOException {


        String[] addresses = recipient.getAddresses();

        if (addresses == null) {
            L.e("KP cannot send ASIP message: No address in remote peer dimension in kepInterest and no address set in publish found. Aborting.", this);
            return;
        }

        ASIPOutMessage response = this.createASIPOutMessage(addresses, recipient);

        if (response != null) {
            if (interest != null) {
                response.expose(interest);
            }
            if (knowledge != null) {
                response.insert(knowledge);
            }
            if (is != null) {
                response.raw(is);
            }
        }

        L.d("<<<<<<<<<<<<<<<<<< End sending knowledge, kepInterest or raw to recipient", this);
    }

    /**
     * Bring receiver addresses in an order. Put addresses up which should
     * be tried first. Message are just send once. Thus, the first valid address
     * is taken.
     * <p>
     * This method puts stream addresses up. More concret: TCP comes first, than
     * HTTP, than mail, anything else afterwords.
     *
     * @param addresses
     * @return
     */
    protected String[] prioritizeAddresses(String[] addresses) {
        if (addresses == null) {
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
            for (int i = 0; i < orderedAddresses.length - 1; i++) {
                int j = i + 1;

                if (this.better(orderedAddresses[j], orderedAddresses[i])) {
                    // lower address has "better" address - bubble!
                    temp = orderedAddresses[i];
                    orderedAddresses[i] = orderedAddresses[j];
                    orderedAddresses[j] = temp;
                    changed = true;
                }
            }
        } while (changed);

        return orderedAddresses;
    }

    /**
     * Defines if address A or B is "better". This implementation
     * is pretty simple: stream protocols are better than message based.
     * Can and should be overwritten.
     *
     * @param addrA
     * @param addrB
     * @return
     */
    protected boolean better(String addrA, String addrB) {
        try {
            int aProtocol = Protocols.getValueByAddress(addrA);
            int bProtocol = Protocols.getValueByAddress(addrB);

            if (Protocols.isStreamProtocol(aProtocol) &&
                    !Protocols.isStreamProtocol(bProtocol)) {
                return true;
            }
        } catch (SharkProtocolNotSupportedException e) {
            return false;
        }

        return false;
    }

    public void addConnectionStatusListener(ConnectionStatusListener listener) {
        this.connectionListener = listener;
    }

    public ASIPOutMessage createASIPOutResponse(StreamConnection connection, String[] receiverAddress, ASIPInMessage inMessage) throws SharkKBException {


        if (connection != null) {
//            L.d("We still have the connection", this);
            if (this.connectionListener != null) {
                connection.addConnectionListener(this.connectionListener);
            }
            if (inMessage.getSender() == null) {
                String receiver = connection.getReceiverAddressString();
                int colon = receiver.lastIndexOf(":");
                String newAddress = receiver.substring(0, colon + 1);
                newAddress += "7071";
                PeerSemanticTag tag = InMemoSharkKB.createInMemoPeerSemanticTag("receiver", "www.receiver.de", newAddress);
                inMessage.setSender(tag);
            }

            return new ASIPOutMessage(this, connection, inMessage);
        } else {
            return this.createASIPOutMessage(receiverAddress, inMessage.getSender());
        }
    }

    public ASIPOutMessage createASIPOutMessage(String[] addresses, PeerSemanticTag receiver) {
        return this.createASIPOutMessage(addresses, this.engineOwnerPeer, receiver, null, null, null, null, 10);
    }


    public ASIPOutMessage createASIPOutMessage(
            String[] addresses,
            PeerSemanticTag sender,
            PeerSemanticTag receiverPeer /* kann null sein*/,
            SpatialSemanticTag receiverSpatial /* kann null sein*/,
            TimeSemanticTag receiverTime /* kann null sein*/,
            SemanticTag topic,
            SemanticTag type,
            long ttl /* Max hops*/) {

        ASIPOutMessage message = null;
        MessageStub mStub;
        StreamStub sStub;
        StreamConnection sConn = null;

        if (addresses == null) {
            return null;
        }

        // sort addresses first
        addresses = this.prioritizeAddresses(addresses);

        Enumeration addrEnum = Util.array2Enum(addresses);
        while (addrEnum.hasMoreElements()) {
            String address = (String) addrEnum.nextElement();

            //boolean fromPool = false;
            try {
                /*
                 * Check if stub is available
                 */

                int protocol = Protocols.getValueByAddress(address);
                Stub protocolStub = this.getProtocolStub(protocol);

                /*
                 * Find out which protocol to use
                 */
                if (protocolStub instanceof StreamStub) {
                    sStub = (StreamStub) protocolStub;
                    //        sConn = this.kepStub.getConnectionByAddress(address);
                    //      if(sConn == null) {
                    try {
                        sConn = sStub.createStreamConnection(address);
                        if (this.connectionListener != null) {
                            sConn.addConnectionListener(this.connectionListener);
                        }
                    } catch (RuntimeException re) {
                        throw new SharkException(re.getMessage(), re.getCause());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    //    } else {
                    //  fromPool = true;
                    //  }
                    message = new ASIPOutMessage(this, sConn, ttl, sender, receiverPeer, receiverSpatial, receiverTime, topic, type);
                } else {
                    // TODO MessageStub necessary?
                    mStub = (MessageStub) protocolStub;
                    message = new ASIPOutMessage(this, mStub, ttl, sender, receiverPeer, receiverSpatial, receiverTime, topic, type, address);
                }
            } catch (SharkNotSupportedException ex) {
                L.e(ex.getMessage(), this);
//                ex.printStackTrace();
                continue;
//            } catch (IOException ex) {
//                L.e(ex.getMessage(), this);
////                ex.printStackTrace();
//                continue;
            } catch (SharkProtocolNotSupportedException spn) {
                L.e(spn.getMessage(), this);
//                spn.printStackTrace();
                continue;
            } catch (SharkException sse) {
                L.w("cannot create KEP message: " + sse.getMessage(), this);
                continue;
            }

            if (sConn != null /*&& !fromPool*/) {
                this.asipStub.handleStream(sConn);
            }

            // one kep message is enough
            if (message != null) {
                return message;
            }
        }

        return null;
    }


    /**
     * That message iterates all remote peers in kp kepInterest and exposes that
     * kepInterest to them
     *
     * @param kp
     * @throws net.sharkfw.system.SharkSecurityException
     * @throws java.io.IOException
     */
    public void publishKP(KnowledgePort kp) throws SharkSecurityException, IOException {
        L.d("publishKP() started", this);
        /**
         * If no relay address is set
         * send the kepInterest to every peer
         * on the REMOTEPEER dimension.
         */
        SharkCS kepInterest = null;
        ASIPInterest asipInterest = null;
        PeerSTSet recipients = null;

        if (kp.getAsipInterest() != null) {
            asipInterest = kp.getAsipInterest();
            recipients = asipInterest.getReceivers();
        } else {
            return;
        }

        try {
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
        Iterator<ASIPPort> kpIter = this.ports.iterator();

        // publish one by one to the environment
        while (kpIter.hasNext()) {
            ASIPPort kp = kpIter.next();

            this.publishKP((KnowledgePort) kp);
        }

    }

    public void publishAllKP(PeerSemanticTag recipient) throws SharkSecurityException, SharkKBException, IOException {
        L.d("Publishing all KPs", this);

        // Find all KPs
        Iterator<ASIPPort> kpEnum = this.ports.iterator();

        // Publish them one by one to the recipient
        while (kpEnum.hasNext()) {
            ASIPPort kp = kpEnum.next();
            this.publishKP((KnowledgePort) kp, recipient);
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

    public abstract void persist() throws SharkKBException;

    public static final String STRING_ENCODING = "ISO-8859-1";
    private static final String PERSISTED_PORT_PROPERTY_NAME = "SharkFW_INTERNAL_PERSISTED_ASIPPORT_NAMES";
    private static final String SHARK_ENGINE_STRING_SEPARATOR = "_SHARK_FW_DELIM";

    private Set<String> persistPortNames;

    private void persistPersistedPorts() throws SharkException {
        SharkKB storageKB = this.getStorage();
        if (storageKB == null) {
            throw new SharkException("Storage KB no set - cannot persist port mementos");
        }

        Iterator<String> iterator = this.persistPortNames.iterator();
        StringBuilder buf = new StringBuilder();

        if (iterator.hasNext()) {
            buf.append(iterator.next());
        }

        while (iterator.hasNext()) {
            buf.append(SHARK_ENGINE_STRING_SEPARATOR);
            buf.append(iterator.next());
        }

        if (buf.length() == 0) {
            storageKB.setProperty(PERSISTED_PORT_PROPERTY_NAME, null);
        } else {
            storageKB.setProperty(PERSISTED_PORT_PROPERTY_NAME, buf.toString(), false);
        }
    }

    private void refreshPersistedPortNames() throws SharkException {
        SharkKB storageKB = this.getStorage();
        if (storageKB == null) {
            throw new SharkException("Storage KB no set - cannot persist port mementos");
        }

        String nameList = storageKB.getProperty(PERSISTED_PORT_PROPERTY_NAME);
        if (nameList == null) {
            // no list, no names, no persistence - that's ok
            return;
        }

        // StringTokenizer was buggy.. don't ask me why... do it my myself..

        // split it
//        StringTokenizer st = new StringTokenizer(nameList, SHARK_ENGINE_STRING_SEPARATOR);
//        
//        while(st.hasMoreTokens()) {
//            String name = st.nextToken();
//            if(name.length() > 0) {
//                this.persistPortNames.add(name);
//            }
//        }

        int i = nameList.indexOf(SHARK_ENGINE_STRING_SEPARATOR);
        if (i == -1) {
            this.persistPortNames.add(nameList);
            return;
        }

        do {
            String name = nameList.substring(0, i);
            this.persistPortNames.add(name);
            nameList = nameList.substring(i);
            i = nameList.indexOf(SHARK_ENGINE_STRING_SEPARATOR + 1);
        } while (i != -1);

        // now take out the last one
        String name = nameList.substring(SHARK_ENGINE_STRING_SEPARATOR.length());
        this.persistPortNames.add(name);
    }

    private static final String SHARK_ENGINE_CLASSNAME = "net.sharkfw.peer.SharkEngine";

    /**
     * This method re-creates ASIP ports from their dormant state. Note
     * that happens after each call. Calling th
     */
    private void refreshPersistedASIPPort() {
        try {
            // drop names, if any
            this.persistPortNames = new HashSet<>();

            // fill list
            this.refreshPersistedPortNames();

            if (this.persistPortNames.isEmpty()) {
                return;
            }

            Class sharkEngineClass = Class.forName(SHARK_ENGINE_CLASSNAME);
            Class mementoClass = null;
            Class[] constructorParam = null;

            SharkKB storageKB = this.getStorage();

            // walk
            Iterator<String> portNameIter = this.persistPortNames.iterator();
            while (portNameIter.hasNext()) {
                String portNameString = portNameIter.next();

                // extract class name
                String[] array = Util.string2array(portNameString);
                String className;
                if (array == null) {
                    className = portNameString;
                } else {
                    className = array[0];
                }

//                int i = portNameString.indexOf(SHARK_ENGINE_STRING_SEPARATOR);
//                String className;
//                if(i < 0) {
//                    className = portNameString;
//                } else {
//                    className = portNameString.substring(0, i);
//                }

                Class portClass = Class.forName(className);

                // get Memento
                String mementoString = storageKB.getProperty(portNameString);

                byte[] mementoByte = null;

                if (mementoString != null) {
                    mementoByte = mementoString.getBytes(STRING_ENCODING);
                } else {
                    L.w("memento string was empty for " + portNameString, this);
                }

                // create memento
                ASIPPortMemento memento = new ASIPPortMemento(className, mementoByte);

                if (constructorParam == null) {
                    // that class is always the same. Taking it once is enough.
                    mementoClass = memento.getClass();
                    constructorParam = new Class[]{sharkEngineClass, mementoClass};
                }

                // get constructor
                Constructor constructor =
                        portClass.getConstructor(constructorParam);

                // pack constructor parameters
                Object[] newParams = new Object[]{this, memento};
                // create an object
                ASIPPort newPort = (ASIPPort) constructor.newInstance(newParams);

            }
        } catch (SharkException | ClassNotFoundException
                | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | UnsupportedEncodingException ex) {
            L.e("could not refresh persisted port: \n" + ex.getClass().getName() + "\n" + ex.getLocalizedMessage(), this);
        }
    }

    private String getPersistPortName(ASIPPort port) throws SharkException {
        SharkKB storageKB = this.getStorage();
        if (storageKB == null) {
            throw new SharkException("Storage KB no set - cannot persist port mementos");
        }

        String uniqueMementoObjectName = port.getUniqueMementoObjectName();
        String canonicalName = port.getClass().getCanonicalName();

        String portName;

        if (uniqueMementoObjectName != null) {
            String[] tempS = new String[]{canonicalName, uniqueMementoObjectName};
            portName = Util.array2string(tempS);
        } else {
            portName = canonicalName;
        }

        return portName;
    }

    public void persistPort(ASIPPort port) throws SharkException {
        if (port == null) return;

        byte[] memento = port.getMemento();
        if (memento == null) return;

        SharkKB storageKB = this.getStorage();
        if (storageKB == null) {
            throw new SharkException("Storage KB no set - cannot persist port mementos");
        }

        try {
            String mementoString = new String(memento, SharkEngine.STRING_ENCODING);

            String propertyName = this.getPersistPortName(port);

            // save it - can be overwritten..
            storageKB.setProperty(propertyName, mementoString, false);

            // remember that key
            this.persistPortNames.add(propertyName);

            // persist rememer
            this.persistPersistedPorts();

        } catch (UnsupportedEncodingException ex) {
            throw new SharkException(ex.getLocalizedMessage());
        }
    }

    public void removePersistedPort(ASIPPort port) throws SharkException {
        if (port == null) return;

        SharkKB storageKB = this.getStorage();
        if (storageKB == null) {
            throw new SharkException("Storage KB no set - cannot persist port mementos");
        }

        String propertyName = this.getPersistPortName(port);

        // save it - can be overwritten..
        storageKB.setProperty(propertyName, null);

        // remember that key
        this.persistPortNames.remove(propertyName);

        // persist rememer
        this.persistPersistedPorts();
    }

    public enum SecurityLevel {MUST, IF_POSSIBLE, NO}

    public enum SecurityReplyPolicy {SAME, TRY_SAME, AS_DEFINED}

    ////////////////////////////////////////////////////////////////////////
    //                    don't sent information again                    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Knowledge can be sent. It is a number of context points.
     * There are two tests before sending knowledge:
     * <p>
     * <ul>
     * <li> Are information attaced to CPs already sent to recipient. Those
     * information are removed from knowledge
     * <li> Does a context point contain any information. Knowledge is sent if
     * at least a single non-empty context point exists.
     * </ul>
     * <p>
     * This second rule can be overwritten with this methode. It can
     * be allowed sending empty cps. This can be useful when semantic tags are
     * the real content that shall be transmitted.
     * <p>
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

    private final HashMap<Integer, String> deliveredInformation =
            new HashMap<>();

    private boolean allowEmptyContextPoints = true;

    public final static int DEFAULT_SILTENT_PERIOD = 500;

    /////////////////////////////////////////////////////////////////
    //                 remember unsent messages                    //
    /////////////////////////////////////////////////////////////////

    private SharkKB unsentMessagesKB;
    private static final String UNSENTMESSAGE_SI = "http://www.sharksystem.net/vocabulary/unsentMesssages";
    private SemanticTag unsentMessagesST = InMemoSharkKB.createInMemoSemanticTag("UnsentMessage", UNSENTMESSAGE_SI);

    private static final String INTEREST_CONTENT_TYPE = "x-shark/kepInterest";
    private static final String KNOWLEDGE_CONTENT_TYPE = "x-shark/knowledge";

    public void setUnsentMessagesKB(SharkKB kb) {
        this.unsentMessagesKB = kb;
    }

    private XMLSerializer xs = null;

    private XMLSerializer getXMLSerializer() {
        if (this.xs == null) {
            this.xs = new XMLSerializer();
        }

        return this.xs;
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

