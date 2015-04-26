package net.sharkfw.peer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.sharkfw.kep.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine.SecurityLevel;
import net.sharkfw.peer.SharkEngine.SecurityReplyPolicy;
import net.sharkfw.pki.SharkPublicKeyStorage;
import net.sharkfw.protocols.*;
import net.sharkfw.system.*;


/**
 * KEPRequests are constructed by a stream or a message.
 * Objects of this class parseKnowledge message on demand.
 *
 * First: Protocol version and format is parsed.
 * Second: Sender information are parsed.
 * Third: Context Space, Context Map and Certificate are parsed (if interest, offer, accept)
 * or Knowledge is parsed.
 *
 * Context Space, Context Map and Certificate are created as in memory
 * objects. Knowledge objects tend to become large. ....
 *
 * This class should become immutable soon. Some KPs are messing around
 * with the interest and knowledge from inside this class, thereby changing
 * it permanently also for KPs which will yet get this request. This might
 * be harmful and cause errativ behaviour!
 *
 * @author thsc
 * @author mfi
 */


public class KEPInMessage extends KEPMessage implements KEPConnection {
    public static String SENDER_SI_STRING_PROPERTY = "sharkfw_senderSIString";

    // message source
    private MessageStub mStub = null;
    private StreamConnection con = null;

    // set in constructor
    private SharkInputStream is;
    private KnowledgeSerializer ks;

    // information directly parsed from message
    private SharkCS receivedInterest = null;
    private Knowledge knowledge = null;

    // derived information
    private String version = null;
    private int kFormat = KEPMessage.XML;

    private int cmd;

    private boolean keepOpen = true;

    // can be set by a knowledge port to change commication channel
//    private String replyAddress = null;
    private KEPStub kepStub;
    private SharkEngine se;

    /**
     * An answer to this request
     */
    private KEPOutMessage response;

    /**
     * The <code>KEPHandler</code> is needed in order to execute methods on <code>KEPResponse</code>
     * objects.
     */
    private KnowledgePort kp;

    // For KEPResponseFactory, if set, the response will be sent to this address
    private PeerAddress recipientAddress = null;

    // If set, all messages will be sent to all addresses of this peer
    private PeerSemanticTag receiverForAllMessages = null;
    private boolean encrypted = false;
    private boolean signed = false;
    private SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private PublicKey publicKeyRemotePeer;
    private PrivateKey privateKey;
    private InputStream underSigningInputStream;
    private SharkPublicKeyStorage publicKeyStorage;
    private SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;
    
    private String[] remotePeerSI = null;
    
    KEPInMessage(SharkEngine se, int kepCmdType, SharkCS receivedInterest, 
            StreamConnection con, KEPStub kepStub) {
        
      this.receivedInterest = receivedInterest;
      
      this.cmd = kepCmdType;

      this.ks = se.getKnowledgeSerializer();

      this.kepStub = kepStub;

      this.se = se;
      
      this.con = con;
    }

    /**
     * constructor is called by mStub to create an initial internal request
     * @param interest KP description which was found
     * @param mStub
     */
    KEPInMessage(SharkEngine se, int kepCmdType, SharkCS receivedInterest, KEPStub kepStub) {
      this.receivedInterest = receivedInterest;
      this.cmd = kepCmdType;

      this.ks = se.getKnowledgeSerializer();

      this.kepStub = kepStub;

      this.se = se;
    }

    /* Constructor */
    public KEPInMessage(SharkEngine se, byte[] msg, MessageStub stub) throws SharkNotSupportedException, IOException {
      this.mStub = stub;
      this.se = se;
      this.is = new StandardSharkInputStream(new ByteArrayInputStream(msg));
//      this.replyAddress = stub.getReplyAddressString();
      //this.parse();
      //this.checkReplyAddressesNew();
    }

    public KEPInMessage(SharkEngine se, StreamConnection con) throws IOException, SharkNotSupportedException {
      L.d("Constructor with exisiting stream.", this);
      this.se = se;
      this.con = con;
//      L.d("Trying to get input stream", this);
      this.is = con.getInputStream();

//      L.d("Trying to get reply address", this);
      /*
       * Default address is the local one. Should be overwritten by parse() which is called subsequently.
       */
//      L.d("This stream got reply address: " + con.getReplyAddressString(), this);
//      this.replyAddress = con.getReplyAddressString();

      L.d("Start parsing", this);
      //this.parse();

      //this.checkReplyAddressesNew();
    }


    /**
     * Let the Request which handler actually handles it.
     * 
     * @param kp The <code>KEPHandler</code> in question.
     */
    public void setKEPHandler(KnowledgePort kp) {
      this.kp = kp;
    }

    /**
     * Returns whether the <code>keepOpen</code> flag is set or not.
     *
     * @return True if flag is set. False otherwise.
     */
    public boolean keepOpen() {
        return this.keepOpen;
    }

    /**
     * Check if whether a <code>MessageStub</code> or a <code>StreamConnection</code> is available.
     *
     * @see net.sharkfw.protocols.MessageStub
     * @see net.sharkfw.protocols.StreamConnection
     *
     * @return True if a stub is available. False otherwise.
     */
    public boolean hasStub() {
        return (this.mStub != null || this.con != null);
    }

    /**
     * Check if a <code>StreamConnection</code> is available.
     *
     * @return True if the <code>StreamConnection</code> is != null. False otherwise.
     */
    public boolean isConnected() {
        return (this.con != null);
    }

    /**
     * Set the <code>MessageStub</code> manually.
     *
     * @see net.sharkfw.protocols.MessageStub
     *
     * @param mStub The new <code>MessageStub</code> to use
     */
    public void setMessageStub(MessageStub mStub) {
        this.mStub = mStub;
    }

    /**
     * Set <code>StreamConnection</code> manually.
     *
     * @see net.sharkfw.protocols.StreamConnection
     *
     * @param con The <code>StreamConnection</code> to use.
     */
    public void setStreamConnection(StreamConnection con) {
        this.con = con;
    }


    /**
     * read header: KEP version, address, command, format
     */
    private void parseHeader() throws IOException {

//      L.d("Parsing header", this);
      
      boolean exit = false;
      
      try {

        // Check if bytes are available on stream.
        if(is.getInputStream().available() > 0) {
//          L.d("Available bytes on stream: " + is.getInputStream().available(), this);
        } else {
          L.d("No more bytes on stream!", this);          
//          throw new IOException("No more bytes on Stream!");
        }

        // Read version
//        L.d("Parsing version", this);
        this.version = is.readUTF8();
        L.d("parse version: " + this.version, this);

        // Read replyAddress
//        this.replyAddress = is.readUTF8();
//        L.d("parse Header: replyAddress: " + this.replyAddress, this);

        // Read cmd
        String cmdString = is.readUTF8();
        this.cmd = Integer.parseInt(cmdString);
        if(this.cmd == -1) {
            exit = true;
//        	throw new IOException("KEPRequest.parseHeader: no more data to read");
        }
        L.d("cmd: " + this.cmd, this);

        // Read format
        String formatString = is.readUTF8();
        this.kFormat = Integer.parseInt(formatString);
        if(this.kFormat == -1) {
        	exit = true;
            throw new IOException("KEPRequest.parseHeader: no more data to read");
        }
        L.d("kepFormat: " + this.kFormat, this);

      } catch (NumberFormatException nfe) {
        // Can't translate String to number - String broken -> Stream at an end?
        if(this.is.available() <= 0) {
//          L.d("End of stream reached", this);
          throw new IOException("Stream ended!");
        }
      }

      // read remote peer si.
      String siString = this.is.readUTF8(); 
      if(!siString.equalsIgnoreCase("n")) {
          this.remotePeerSI = Utils.deserialize(siString);
          L.d("remote peer si parsed: " + this.remotePeerSI[0], this);
      } // else - no remote peer si.
      
      // signed?
      String signedString = this.is.readUTF8();
      if(signedString.equalsIgnoreCase("n")) {
          this.signed = false;
      } else {
          this.signed = true;
      }
      
      // encryption?
      String sessionKeyLenString = this.is.readUTF8();
      L.d("\n session key len: " + sessionKeyLenString, this);
      
      int sessionKeyLen = Integer.parseInt(sessionKeyLenString);
      if(sessionKeyLen > 0) {
          
        try {
            // we have an encryption key - convert
            byte[] sessionKeyBytes = new byte[sessionKeyLen];
            this.is.getInputStream().read(sessionKeyBytes);

            L.printByte(sessionKeyBytes, "session key bytes (received)");

            // encrypt
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, this.privateKey);
            this.sessionKey = (SecretKey) cipher.unwrap(sessionKeyBytes, "AES", Cipher.SECRET_KEY);
            this.encrypted = true;
            
//            System.out.println(">>>>>>>>>>>>>>>> session key encrypted <<<<<<<<<<<<<<<<");
        } catch (Exception ex) {
//            System.out.println(">>>>>>>>>>>>>>>> session key not encrypted <<<<<<<<<<<<<<<<");
            L.e(ex.getMessage(), this);
        }
      } else {
          this.encrypted = false;
      }

      if(exit) {
    	  throw new IOException("Stream ended!");
      }
}

    private SecretKey sessionKey;
    private DigestInputStream digestStream = null;
    private VerifyingInputStream sin = null;
    
    private void setupSecurity() throws SharkSecurityException {
        if (this.encryptionLevel != SharkEngine.SecurityLevel.NO
                && this.sessionKey != null) {
            
            InputStream decryptingIS = new DecryptingInputStream(
                    this.is.getInputStream(), this.sessionKey);
            
            this.is.set(decryptingIS);
        }

        // we need a valid underSign... later. Keep this.
        this.underSigningInputStream = this.is.getInputStream();
        if (this.signatureLevel != SharkEngine.SecurityLevel.NO) {
            // try to find public key remote peer
            
            // this code can throw an runtime shark security exception
            try {
                if(this.publicKeyStorage == null) {
                    throw new SharkSecurityException("no public key storage found");
                }
                
                this.publicKeyRemotePeer = 
                        this.publicKeyStorage.getPublicKey(this.remotePeerSI);
                
                // we have a key
                this.sin = new VerifyingInputStream(this.is.getInputStream(), 
                        this.publicKeyRemotePeer);
                this.is.set(this.sin);
            } catch(SharkException sse) {
                if(this.refuseUnverifiably) {
                    throw new SharkSecurityException(sse.getMessage());
                    
                } else {
                    // ignore
                }
            }
        }
    }
    
    /**
     * 
     * @return true if signature could be verified or no signature was transmitted
     */
    boolean signatureOK() {
        return this.signatureOK;
    }
    
    public void parse() throws SharkNotSupportedException, IOException, SharkSecurityException, SharkKBException {
        this.parseHeader();
        
        //PeerSemanticTag sender = this.getSender();
//        if(sender != null) {
//            L.d("Found sender tag and StreamConnection. Adding connection to the pool.", this);
//            this.se.getKepStub().addConnection(replyAddress, con);
//        }
      
      ///////////////////////////////////////////////////////////////////
      //              set up security protocol stack here              //
      ///////////////////////////////////////////////////////////////////

        try {
            // if message is encrypted - add decrypting stream
            if(this.encrypted) {
                if(this.encryptionLevel == SecurityLevel.NO) {
                    // is encrypted but must not be
                    throw new SharkSecurityException("message is encrypted but encrypted messages are not excepted");
                }
                
                InputStream decryptingIS = new DecryptingInputStream(
                        this.is.getInputStream(), this.sessionKey);

                this.is.set(decryptingIS);
            } else {
                if(this.encryptionLevel == SecurityLevel.MUST) {
                    // is encrypted but must not be
                    throw new SharkSecurityException("message is not encrypted but non encrypted messages are not excepted");
                }
                
            }

            // we need a valid underSign... later. Keep this.
            this.underSigningInputStream = this.is.getInputStream();

            if (this.signed) {
                if(this.signatureLevel == SecurityLevel.NO) {
                    // is encrypted but must not be
                    throw new SharkSecurityException("message is signed but signed messages are not excepted");
                }
                // try to find public key remote peer

                // this code can throw an runtime shark security exception
                try {
                    if(this.publicKeyStorage == null) {
                        throw new SharkSecurityException("no public key storage found");
                    }

                    this.publicKeyRemotePeer = 
                            this.publicKeyStorage.getPublicKey(this.remotePeerSI);

                    // we have a key
                    this.sin = new VerifyingInputStream(this.is.getInputStream(), 
                            this.publicKeyRemotePeer);
                    this.is.set(this.sin);
                } catch(SharkException sse) {
                    if(this.refuseUnverifiably) {
                        throw new SharkSecurityException(sse.getMessage());
                    } else {
                        // ignore
                    }
                }
            } else {
                if(this.signatureLevel == SecurityLevel.MUST) {
                    // is encrypted but must not be
                    throw new SharkSecurityException("message is not signed but unsigned messages are not excepted");
                }
            }
        } catch(SharkSecurityException sse) {
              // log 
              L.d("security exception while reading message:" + sse.getMessage(), this);
              // and finish in case of exception
              throw sse;
        }
        
        // security is set up here ///////////////////////////

        /////////////////////////////////////////////////////////////////
        //                        parse content                        //
        /////////////////////////////////////////////////////////////////
        if(KEPMessage.validKEPCommand(this.cmd)) {

            this.ks = this.se.getKnowledgeSerializer(this.kFormat);

            if(this.cmd == KEPMessage.KEP_EXPOSE) {
                try {
                    // parseKnowledge message
                    InMemoSharkKB tempKB = new InMemoSharkKB();
                    this.receivedInterest = ks.parseSharkCS(tempKB, this.is);
                } catch (SharkKBException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

            } 
            else if(this.cmd == KEPMessage.KEP_INSERT) {
                /* 
                 * knowledge is read from string when getKnowledge is called.
                 * Prefetching must only take place if a signature has to be
                 * verified first.
                 */

                if(signed) { // prefetch !
                    // read whole message from stream 
                    this.getKnowledge();
                }
            }

        } else {
            throw new SharkNotSupportedException("unknown KEP command: " + this.cmd);
        }

        /////////////////////////////////////////////////////////////////
        //                    parse signature - if any                 //
        /////////////////////////////////////////////////////////////////
        // read signature if there is one
        if(this.signed) {
            // there is a signature - read it from stream
            try {
                // calculate digest before reading signature
//                byte[] md = this.digestStream.getMessageDigest().digest();
                
                // first hang out verifier
                this.is.set(this.underSigningInputStream);
                
                // read length as string
                String sigLenString = this.is.readUTF8();
                int sigLen = Integer.parseInt(sigLenString);
                
                byte[] signature = new byte[sigLen];
                this.is.getInputStream().read(signature);
                
                L.d(L.byteArrayToString(signature, "received signature"), this);
                
                // verify
                if(this.sin != null) {
                    // this happens if we don't have a public key but we accept 
                    // message which signatures we cannot verify
                    this.signatureOK = this.sin.verify(signature);
                } else {
                    this.signatureOK = true;
                }
            } catch (IOException ex) {
                L.d(ex.getMessage(), this);
            }
        }
        
        if(!this.signatureOK()) {
            L.l("wrong signature in KEP message; throw security exception", this);
            throw new SharkSecurityException("wrong signature in KEP message");
        }
    }
    
    private boolean signatureOK = true;
    
    private boolean sendingPropertiesSet = false;
    
    public static void setHiddenProperties(SystemPropertyHolder element, 
            HashMap<String, String> map) throws SharkKBException {
        
        if(map != null) {
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<String, String> property = iterator.next();
                
                element.setProperty(property.getKey(), property.getValue(), false);
            }
        }
    }
    
    public static void setPropertiesToEachElement(STSet stSet, HashMap<String, String> properties) throws SharkKBException {
        Enumeration<SemanticTag> tags = stSet.tags();
        if(tags != null) {
            while(tags.hasMoreElements()) {
                SemanticTag tag = tags.nextElement();
                
                setHiddenProperties(tag, properties);
            }
        }
    }
    
    
    public static void setPropertiesToEachElement(SharkCS interest, HashMap<String, String> properties) throws SharkKBException {
        if(interest == null) {
            return;
        }
        
        // topics
        STSet stSet = interest.getTopics();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // peers
        stSet = interest.getPeers();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // remote peers
        stSet = interest.getRemotePeers();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // locations
        stSet = interest.getLocations();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // originator
        SemanticTag originator = interest.getOriginator();
        if(originator != null) {
            setHiddenProperties(originator, properties);
        }
    }
    
    public static void setPropertiesToEachElement(Knowledge k, HashMap<String, String> properties) throws SharkKBException {
        SharkVocabulary context = k.getVocabulary();
        
        // topics
        STSet stSet = context.getTopicSTSet();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // peers
        stSet = context.getPeerSTSet();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // locations
        stSet = context.getSpatialSTSet();
        if(stSet != null) {
            setPropertiesToEachElement(stSet, properties);
        }
        
        // originator
        SemanticTag owner = context.getOwner();
        if(owner != null) {
            setHiddenProperties(owner, properties);
        }
        
        Enumeration<ContextPoint> contextPoints = k.contextPoints();
        if(contextPoints != null) {
            while(contextPoints.hasMoreElements()) {
                ContextPoint cp = contextPoints.nextElement();
                
                setHiddenProperties(cp, properties);
                
                Enumeration<Information> enumInformation = cp.enumInformation();
                if(enumInformation != null) {
                    while(enumInformation.hasMoreElements()) {
                        Information info = enumInformation.nextElement();
                        
                        setHiddenProperties(info, properties);
                    }
                }
            }
        }
    }
    
    private HashMap<String, String> getSendingProperties() {
        HashMap<String, String> properties = new HashMap();

        // get time
        long time = System.currentTimeMillis();
        String timeValue = Long.toString(time);

        properties.put(AbstractSharkKB.SHARKFW_TIME_RECEIVED_PROPERTY, timeValue);
        try {
            PeerSemanticTag sender = this.getSender();
            if(sender != null) {
                String siString = Util.array2string(sender.getSI());

                properties.put(AbstractSharkKB.SHARKFW_SENDER_PROPERTY, siString);
            }
        } catch (SharkKBException ex) {
            // ignore
        }
        
        return properties;
    }
    
    /**
     * Returns the <code>ReceivedInterest</code> if one has been found and parsed successfully.
     *
     * @return The "unmarshaled" <code>ReceivedInterest</code> or <code>null</code>
     */
    public SharkCS getInterest() {
        if(!this.sendingPropertiesSet && this.receivedInterest != null) {
            this.sendingPropertiesSet = true;
            HashMap<String, String> sendingProperties = this.getSendingProperties();
            
            try {
                KEPInMessage.setPropertiesToEachElement(this.receivedInterest, 
                    sendingProperties);
            }
            catch(SharkKBException e) {
                // ignore - I don't like to stop that process for that reason
            }
        }
        
        return this.receivedInterest;
    }

    /**
     * Parses the <code>Knowledge</code> from the stream.
     * Knowledge is not parsed before this method is called to keep memory usage low.
     *
     * @return A <code>Knowledge</code> object.
     * @throws IOException
     */
    public Knowledge getKnowledge() throws IOException, SharkKBException {
        if(this.knowledge == null) {
            this.knowledge = this.ks.parseKnowledge(is);
        }

        // set sender as property
        PeerSemanticTag senderPeer = this.getSender();
        if(senderPeer != null) {
            String[] senderSI = senderPeer.getSI();
            String senderSIString = Util.array2string(senderSI);
    
            // set sender to each cp
            Enumeration cpEnum = this.knowledge.contextPoints();
                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = (ContextPoint) cpEnum.nextElement();
                    cp.setProperty(KEPInMessage.SENDER_SI_STRING_PROPERTY, senderSIString, false);
                }
        }
        
        if(this.knowledge != null && !this.sendingPropertiesSet) {
            this.sendingPropertiesSet = true;
            
            HashMap<String, String> sendingProperties = this.getSendingProperties();
            
            KEPInMessage.setPropertiesToEachElement(this.knowledge, 
                    sendingProperties);
        }
        
        return this.knowledge;
    }

    /**
     * Return the KEP-Command.
     *
     * @see net.sharkfw.kep.KEPMessage
     *
     * @return An integer representing the KEP-command
     */
    public int getCmd() {
        return this.cmd;
    }


    /**
     * A call to this method checks if a stream is available inside
     * this request. If it is, the stream gets passed to the KEPStub.
     */
    public void finished() {
        if(this.kepStub != null && this.con != null) {
            this.kepStub.handleStream(this.con);
        }
    }


    /**
     * Returns the reference to the <code>MessageStub</code> that this object has.
     * <em>NOTE:</em> This reference might be null if a <code>StreamConnection</code> is used.
     *
     * @see net.sharkfw.protocols.MessageStub
     * 
     * @return Reference to a <code>MessageStub</code> if one is used. <code>null</code> otherwise.
     */
    public MessageStub getMessageStub() {
      return this.mStub;
    }

    /**
     * Returns the reference to the <code>StreamConnection</code> that this object has.
     * <em>NOTE:</em> This reference might be null if a <code>MessageStub</code> is used.
     *
     * @see net.sharkfw.protocols.StreamConnection
     *
     * @return Reference to a <code>StreamConnection</code> if one is used. null otherwise.
     */
    public StreamConnection getStreamConnection() {
      return this.con;
    }

    /**
     * @param interest The interest to send.
     */
    @Override
    public void expose(SharkCS interest) {
         try {
           STSet remotepeers = interest.getSTSet(SharkCS.DIM_REMOTEPEER);
           Enumeration rPeers = null;
           
            if(remotepeers != null) {
                rPeers = remotepeers.tags();
            }
           
           if(rPeers == null || !rPeers.hasMoreElements()) {
               // there are no peer at all - maybe we got it through a stream
               this.expose(interest, (String[]) null);
               return;
           }

           // Send interest to every peer
           while (rPeers.hasMoreElements()) {
                PeerSemanticTag rpst = (PeerSemanticTag) rPeers.nextElement();
                // try every address of that peer
                String[] adr = rpst.getAddresses();
                if (adr == null) {
                    L.e("Peer has no addresses. Unable to proceed.", this);
                    continue;
                }
                
                this.expose(interest, adr);
            }
         } catch (SharkException ex) {
           // KB Error
           L.e(ex.getMessage(), this);
        }
    }

  /**
   * This method is called by both <code>createInsertResponse(Knowledge k, String receiveraddress)</code> and
   * <code>createExposeResponse(ExposedInterest interest, String receiveraddress)</code> to create an empty
   * <code>KEPResponse</code> with all communication parameters set.
   *
   * @param receiveraddress The address of the peer where the KEPResponse shall go.
   * @return A new instance of <code>KEPResponse</code> with the communication parameters preset.
   * @throws SharkException If no communication channel for the <code>KEPResponse</code> can be created.
   */
  private KEPOutMessage createResponse(String[] receiveraddress) throws SharkException {
      return this.se.createKEPOutResponse(this.con, receiveraddress, publicKeyRemotePeer, remotePeerSI, encrypted, signed);
      
//      
//    /**
//     * Create a new communication channel to the the address provided in the method.
//     */
//    L.d("Creating new KEPResponse to:" + receiveraddress, this);
//    MessageStub _mStub = null;
//    StreamStub sStub = null;
//    StreamConnection sCon = null;
//    KEPOutMessage _response = null;
//    
//    try {
//      // Get a stub to that address
//      Stub stub = this.se.getStub(receiveraddress);
//      L.d("Found protocol stub for: " + receiveraddress , this);
//      try {
//        // Check if it a message stub by trying to cast the stub
//        _mStub = (MessageStub) stub;
//        _response = new KEPOutMessage(this.se, _mStub, this.se.getKnowledgeSerializer(), receiveraddress);
//        L.d("Stub is a message stub", this);
//
//      } catch (ClassCastException cce) {
//        // Not a MessageStub. Streambased then.
//        try {
//          /*
//           * TODO: Try to get a connection from the connection pool?
//           */
//          sStub = (StreamStub) stub;
//          /*
//           * FIXME: the commented code causes trouble when testing TestPMwith3Peers?
//           */
//          sCon = this.se.getKepStub().getConnectionByAddress(receiveraddress);
//          boolean fromPool = true;
//          if(sCon == null) {
//            sCon = sStub.createStreamConnection(receiveraddress);
//            fromPool = false;
//          }
//
//          if(!fromPool){
//            this.se.getKepStub().handleStream(sCon);
//          }
//
//          _response = new KEPOutMessage(this.se, sCon, 
//                  this.se.getKnowledgeSerializer());
//          L.d("Stub is a stream connection", this);
//
//        } catch (IOException ex) {
//          // Comm mistake!
//          L.e(ex.getMessage(), this);
//          ex.printStackTrace();
//        }
//      } // if this fails we need to see the exception! We won't catch it here.
//
//    } catch (SharkProtocolNotSupportedException ex) {
//        // Protocol not supported on platform
//        L.e(ex.getMessage(), this);
//      }
//
//    ///////////////////////////////////////////////////////////////////
//    //                       setting up security                     //
//    ///////////////////////////////////////////////////////////////////
//    
//    if(_response != null) {
//        // check reply policy what todo
//        
//        // pre set all parameter with those are found in original request
//        PrivateKey useThisPrivateKey = this.privateKey;
//        
//        String[] useThisSI = null;
//        PeerSemanticTag seOwner = this.se.getOwner();
//        if(seOwner != null) {
//            useThisSI = seOwner.getSI();
//        }
//
//        PublicKey useThisPublicKey = this.publicKeyRemotePeer;
//        boolean sign = true;
//
//        ///////////////////////////////////////////////////////////
//        //                 "as defined" policy                   //
//        ///////////////////////////////////////////////////////////
//        if(this.replyPolicy == SecurityReplyPolicy.AS_DEFINED) {
//            
//            // encryption - set or unset public key remote peer ///
//            
//            if(this.encryptionLevel == SecurityLevel.NO) {
//                // no encryption at all
//                useThisPublicKey = null;
//            } 
//            else if(this.encryptionLevel == SecurityLevel.MUST) {
//                if(useThisPublicKey == null && this.remotePeerSI != null) {
//                    // there is no public key - maybe we have it on oki store
//                    useThisPublicKey = this.publicKeyStorage.getPublicKeyBySI(
//                                                            this.remotePeerSI);
//                }
//                
//                if(useThisPublicKey == null) {
//                    throw new SharkSecurityException("security policy declares encryption a MUST but no public key of remote peer can be found - fatal - message not sent");
//                }
//            }
//            // else SecurityLevel.IF_POSSIBLE; - nothing todo
//            
//            // signing - set or unset private key and si to identify this peer
//            
//            if(this.signatureLevel == SecurityLevel.NO) {
//                // no signing - remove public key and si
//                sign = false;
//            }
//            else if(this.signatureLevel == SecurityLevel.MUST) {
//                if(useThisPrivateKey == null || useThisSI == null) {
//                    throw new SharkSecurityException("security policy declares encryption a MUST but no private key set or no SI of this peer found - fatal - message not sent");
//                }
//            }
//            // else SecurityLevel.IF_POSSIBLE; - nothing todo
//        } 
//        
//        ///////////////////////////////////////////////////////////
//        //                 "(try) same" policy                   //
//        ///////////////////////////////////////////////////////////
//        else {
//            // encryption
//
//            // if message wasn't encrypted - don't encrypt either
//            if(!this.encrypted) {
//                useThisPublicKey = null;
//            } else {
//                if(this.replyPolicy == SecurityReplyPolicy.SAME) {
//                    // we must encrypt
//                    if(useThisPublicKey == null && this.remotePeerSI != null) {
//                        // there is no public key - maybe we have it on oki store
//                        useThisPublicKey = this.publicKeyStorage.getPublicKeyBySI(
//                                                                this.remotePeerSI);
//                    }
//                    if(useThisPublicKey == null) {
//                        throw new SharkSecurityException("security policy is SAME AS MESSAGE and message was encrypted but cannot find public key - fatal - message not sent");
//                    }
//                } // TRY_SAME. nothing todo
//            }
//
//            // siging
//            if(!signed) {
//                sign = false;
//            } else {
//                // we like to sign
//                sign = true;
//                if(useThisPrivateKey == null || useThisSI == null) {
//                    if(this.replyPolicy == SecurityReplyPolicy.SAME) {
//                        // we must sign - test must not fail
//                        throw new SharkSecurityException("security policy is SAME AS MESSAGE and message was signed but no private key set or no SI of this peer found - fatal - message not sent");
//                    } else {
//                        // we wanted but cannot
//                        sign = false;
//                    }
//                }
//            }
//        }
//        
//        _response.initSecurity(useThisPrivateKey, useThisPublicKey, useThisSI, sign);
//  
//      return _response;
//    } else {
//      throw new SharkException("Unable to create KEPResponse for address:" + receiveraddress);
//    }
  }

  /**
   * Sending insert with the given knowledge to the given address.
   * Without checking if a connection to that address is already established.
   * A new connection is created.
   *
   * @param k The <code>Knowledge</code> to send.
   * @param receiveraddress The receiver's address.
   */
    @Override
  public void insert(Knowledge k, String receiveraddress) throws SharkException {
        this.insert(k, new String[] {receiveraddress});
  }

  /**
   * Sends the interest to the given address.
   * Without chekcing if a connection to that address already exists.
   * A new connection is established, and the interest is sent.
   * 
   * @param interest The interest to send.
   * @param receiveraddress The recipient's address
   */
    @Override
    public void expose(SharkCS interest, String receiveraddress) throws SharkException {
        this.expose(interest, new String[] { receiveraddress });
    }

  /**
   * Return if a response has been sent.
   * @return True if <code>response!=null && response.responseSent()</code>. False otherwise.
   */
    @Override
  public boolean responseSent() {
    if(this.response == null) {
      return false;
    }
    return response.responseSent();
  }

  /**
   * Setting internal <code>response<code> object to <code>null</code>.
   */
  public void resetResponse() {
    this.response = null;
  }

  /**
   * The sender of this KEPRequest is - if set - the one peer,
   * that can be found in either the PEER dimension of the interest (if this is an expose command)
   * or inside the PEER dimension of the context map of the knowledge (if this is an insert command).
   * If neither are set we return null.
   *
   * @return
   */
  public PeerSemanticTag getSender() throws SharkKBException {
    PeerSTSet peer = null;
    PeerSemanticTag sender = null;

    if(this.receivedInterest != null) {
      try {
        // We have an interest!
        peer = (PeerSTSet) this.receivedInterest.getSTSet(SharkCS.DIM_PEER);
      } catch (SharkKBException ex) {
        ex.printStackTrace();
      }

    } else if (this.knowledge != null && this.knowledge.getVocabulary() != null) {
      try {
        // We have knowledge
        peer = (PeerSTSet) this.knowledge.getVocabulary().getPeerSTSet();
      } catch (SharkKBException ex) {
        ex.printStackTrace();
      }
    }
//    try {
      if(peer != null){
        Enumeration<SemanticTag> tags = peer.tags();
        /*
         * There must only be one tag on this dimension.
         */
        if(tags != null && tags.hasMoreElements()) {
          sender = (PeerSemanticTag) tags.nextElement();
        }
      }
//    } catch (SharkNotSupportedException ex) {
//      ex.printStackTrace();
//    }
    // Here sender has either been set and contains a value or not, and returns null
    return sender;
  }

  /**
   * Call the method for single addresses subsequently until all addresses have been triggered.
   * @param interest The interest to send
   * @param receiveraddresses
     * @throws net.sharkfw.system.SharkException
   */
    @Override
    public void expose(SharkCS interest, String[] receiveraddresses) throws SharkException {
        KEPOutMessage newResponse = this.createResponse(receiveraddresses);
        if(newResponse != null) {
            try {
                newResponse.expose(interest);
            }
            catch(IOException e) {
                throw new SharkKBException(e.getMessage());
            }
        }
    }

  /**
   * @param k The knowledge to send
   * @param receiveraddresses An Array of Strings containing addresses to which the knowledge shall be sent
     * @throws net.sharkfw.system.SharkException
   */
    @Override
    public void insert(Knowledge k, String[] receiveraddresses) throws SharkException {
        KEPOutMessage newResponse = this.createResponse(receiveraddresses);
        if(newResponse != null) {
            try {
                newResponse.insert(k);
            }
            catch(IOException e) {
                throw new SharkKBException(e.getMessage());
            }
        }
    }

  public void setRecipientAddress(PeerAddress address) {
    this.recipientAddress = address;
  }

    @Override
  public void sendToAllAddresses(PeerSemanticTag pst) {
    this.receiverForAllMessages = pst;
  }

    /**
     * 
     * @param privateKey need for encryption to unwrap session key
     * @param publicKeyStorage
     * @param encryptionLevel
     * @param signatureLevel
     * @param replyPolicy
     * @param refuseUnverifiably 
     */
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

    @Override
    public boolean receivedMessageEncrypted() {
        return this.encrypted;
    }

    @Override
    public boolean receivedMessageSigned() {
        return this.signed;
    }
}
