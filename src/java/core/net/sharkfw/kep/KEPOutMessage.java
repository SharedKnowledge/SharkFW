package net.sharkfw.kep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import javax.crypto.*;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.UTF8SharkOutputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.Utils;

/**
 * This class provides some serializations for responses to <code>KEPRequests</code>.
 * <code>KEPResponse</code>s are created through <code>KEPResponseFactory</code> implementations.
 * The default implementation for this is <code>KEPRequest</code>, because the request already contains
 * a lot of important information for creating and addressing answers properly.
 *
 * The really hardcore serialization however happens in an instance of <code>KnowledgeSerializer</code>.
 * 
 * @see net.sharkfw.kep.KnowledgeSerializer
 * @see net.sharkfw.kep.format.XMLSerializer
 * @see net.sharkfw.kep.KEPMessage
 * @see net.sharkfw.peer.KEPRequest
 *
 *
 * @author thsc
 * @author mfi
 */
public class KEPOutMessage extends KEPMessage implements KEPEngine {
    private MessageStub outStub = null;
    private SharkOutputStream os = null;
    private ByteArrayOutputStream baos = null;
    private StreamConnection con = null;

    private KnowledgeSerializer ks = null;

    private String recipientAddress;

    private final SharkEngine se;
    private PublicKey publicKeyRecipient = null;
    private PrivateKey privateKey;
    private SecretKey sessionKey = null;
    private String sendingPeerSIString = null;
    private boolean sign;

    /** 
     * Message will be sent via message based protocol
     */
    public KEPOutMessage(SharkEngine se, MessageStub outStub, KnowledgeSerializer ks, String address) {
        this.se = se;
        this.outStub = outStub;
        this.ks = ks;
        this.recipientAddress = address;
        this.baos = new ByteArrayOutputStream();
        this.os = new UTF8SharkOutputStream(baos);
        
        
    }

    /** 
     * Message will be send over a already created output stream
     */
    public KEPOutMessage(SharkEngine se, StreamConnection con, KnowledgeSerializer ks) {
      L.d("Created KEPResponse with stream connection to: " + con.getReplyAddressString(), this);
        this.se = se;
        this.con = con;
//        L.d("Trying to get output stream", this);
        this.os = this.con.getOutputStream();
//        L.d("Success.", this);
        this.ks = ks;        
        this.recipientAddress = con.getReceiverAddressString();
    }

    private byte[] versionByte = null;

    /**
     * Methode ist called when anything is written into the
     * outputstream os. There are three constructor. Thus,
     * there are two possibilities what had happend:
     *
     * <ul>
     * <li>message was written into an existing stream </li>
     * <li>message was written into a byte buffer and Message Stub exists</li>
     * </ul>
     */
    private void sent() throws IOException {
        // we are done 
        
        // have we created a message digest?
        if(this.sign() && this.sos != null) {
//            try {
                // append signature
                byte[] signature = sos.getSignature();

                L.d(L.byteArrayToString(signature, "signature to be sent"), this);
                
                // done with signing - hang out digest stream
                this.os.set(this.underDigestStream);

                // write signature length as string!
                String sigLenString = Integer.toString(signature.length);
                this.os.write(sigLenString);

                // write signature
                this.underDigestStream.write(signature);
//            } catch (IOException ex) {
//                L.d(ex.getMessage(), this);
//            }
        }
        
        // everything is done - signature was last thing to encrypt.
        // force padding or whatever in encryption stream if any
        if(this.encryptingStream != null) {
            try {
                this.encryptingStream.doFinal();
            } catch (IOException ex) {
                L.d(ex.getMessage(), this);
            }
        }
        
        // Do I work on a message stub ?
        if(outStub != null) {
            if(this.baos == null) { /* impossible */ return; }
            
            // there is a message stub - send message which was streamed into the buffer
//            try {
                // Use a byte[] to avoid encoding issues. Encoding is handled on a higher level (Shark[Output|Input]Stream).
                this.outStub.sendMessage(this.baos.toByteArray(), this.recipientAddress);
//            }
//            catch(IOException ioe) {
//                // TODO - failure on protocol level...
//            }
            // Nothing todo any more. An answer would come through the message stub
//            return;
        }
        else {
            if(this.os != null) {
//                try {
                    this.os.getOutputStream().flush();
//                } catch (IOException ex) {
//                    L.l(ex.getMessage(), this);
//                }
            }
        }
    }

    private void writeHeader(int cmd, int format) throws IOException {
      //Base64.OutputStream bos = new Base64.OutputStream(os);
      // write version
//      L.d("Writing Header", this);
        
        if(this.versionByte == null) {
            this.versionByte = KEPMessage.THISVERSION.getBytes("UTF-8");
        }

        this.os.write(KEPMessage.THISVERSION);
        L.d("Wrote version: " + KEPMessage.THISVERSION, this);

/*        
        String replyAddress = null;
        if(this.outStub != null) {
          // If another replyaddress-String is needed it has to be set in the outstub!
            replyAddress = this.outStub.getReplyAddressString();
        } else if(this.con != null) {
            replyAddress = this.con.getReplyAddressString();
        }

        if(replyAddress != null) {
            this.os.write(replyAddress);
            L.d("Wrote replyaddress: " + replyAddress, this);
        } else {
            this.os.write("0");
            L.d("Wrote replyaddress: 0", this);
        }
*/
        
        // write cmd
        os.write(Integer.toString(cmd));
//        L.d("Wrote cmd " + cmd, this);

        // write format
        os.write(Integer.toString(format));
//        L.d("Wrote format:" + format, this);
        
        if((this.sign() || this.encrypt()) && (this.sendingPeerSIString != null)) { 
            /* 
             * si is required to allow receiving peer finding public key - 
             * either to verify signature or to unwrap session key
             */
            L.d("write peer SI String: " + this.sendingPeerSIString, this);
            this.os.write(this.sendingPeerSIString);
        } else {
            L.d("no peer SI String written", this);
            this.os.write("n");
        }
        
        if(this.sign()) {
            L.d("message will be signed", this);
            this.os.write("s");
        } else {
            L.d("message won't be signed", this);
            this.os.write("n");
        }

        // do we want to encrypt ?
        if(this.encrypt()) {
            L.d("create session key", this);
            // encryption needed - setup session key
            try {
                // create AES session key
                KeyGenerator gen = KeyGenerator.getInstance("AES");
                
                this.sessionKey = gen.generateKey();
            } catch (NoSuchAlgorithmException ex) {
                L.d(ex.getMessage(), this);
            }
        } else { // make sure not to encrypt
            this.sessionKey = null;
        }
        
        String encryptedSessionKey = null;
        // do we encrypt that stuff?
        if(this.sessionKey != null) {
            try {
                // encrypt that thing
                L.d("wrap session key", this);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.WRAP_MODE, this.publicKeyRecipient);
                byte[] sessionKeyBytes = cipher.wrap(this.sessionKey);
                
//                System.out.println("\n session key bytes len (sent): " + sessionKeyBytes.length);
                L.printByte(sessionKeyBytes, "wrapped session key bytes (sent)");
                
                this.os.write(String.valueOf(sessionKeyBytes.length));
                this.os.getOutputStream().write(sessionKeyBytes);
                
            } catch (GeneralSecurityException ex) {
                
                L.e(ex.getMessage(), this);
                this.os.write("0");
            } catch(IOException ioe) {
                // I don't know
            } 
        } else {
            // make sure there won't be a session key
            this.os.write("0");
        }
        
        // we are done here - set up encrypting protocol stack
        
        // add encryption stream if necessary
        if (this.encrypt()) {
            L.d("put encryption stream on top of output stream", this);
            this.encryptingStream = new EncryptingOutputStream(this.os.getOutputStream(), this.sessionKey);
            this.os.set(this.encryptingStream);
        }

        // add signing stream if necessary
        if (this.sign()) {
            L.d("put signing stream on top", this);
            this.underDigestStream = this.os.getOutputStream();
            
            this.sos = new SigningOutputStream(this.os.getOutputStream(), this.privateKey);
            this.os.set(sos);
        }
    }
    
    private SigningOutputStream sos = null;
    private Cipher cipher;
    private EncryptingOutputStream encryptingStream;
    
    private DigestOutputStream digestStream = null;
    private OutputStream underDigestStream = null;
        
    private boolean encrypt() {
        return (this.publicKeyRecipient != null && this.sendingPeerSIString != null);
    }
    
    private boolean sign() {
        return (this.sign && this.sendingPeerSIString != null);
    }
    
    /********************************************************************
     *                 KEP Protocol Primitives (KEPEngine)              *
     ********************************************************************/

    private boolean responseSent = false;

    /**
     * Return whether the sending of the response was successfull or not.
     * 
     * @return True if successfull, false otherwise.
     */
    public boolean responseSent() {
        return this.responseSent;
    }

    /**
     * Create an insert command containing the <code>Knowledge</code> passed.
     *
     * @param k The <code>Knowledge</code> to send
     * @param kepHandler The <code>KEPHandler</code> to use.
     */
    public void insert(Knowledge k) throws IOException {
        /*
         * Before checking anything else:
         * Is this message allowed to be sent by message accounting?
         */
//        if(!this.se.getKepStub().knowledgeAllowed(k)){
//          // It is not allowed due to silence period.
//          L.d("Message not allowed to be sent!", this);
//          return;
//        }
        
      String address = null;
      if(con != null) {
          String localAddress = con.getLocalAddressString();
          if(this.recipientAddress != null && localAddress != null && localAddress.equalsIgnoreCase(this.recipientAddress)) {
            address = con.getReplyAddressString();
          } else {
              address = this.recipientAddress;
          }
      } else if(outStub != null) {
          address = this.recipientAddress;
      }

//      k = this.se.removeSentInformation(k, address);
      
      if(k == null) {
        // All information have already been sent to the given recipient. Nothing to do.
        this.responseSent = true; // Everything has been sent already (though not now).
        L.w("knowledge should be send but knowledge had no content or was already sent to recipient - don't send anything", this);
        
//        try {
//            this.writeHeader(-1, -1);
//        } catch(IOException e) {
//            L.e(e.getMessage(), this);
//        }
        
        return;
      }
      
      // Having reached this point shows that some information must still be sent
      L.d(">>>>>>>>>>>> send insert", this);        
//      try {
        // write header
        this.writeHeader(KEPMessage.KEP_INSERT, this.se.getKnowledgeFormat());
//          L.d("Wrote header.", this);
        try {
            this.ks.write(k, os);
        }
        catch(SharkKBException e) {
            throw new IOException(e.getMessage());
        }
//          L.d("Wrote knowledge", this);
      // notify we are done with that message
        this.sent();
        L.d(">>>>>>>>>>> insert sent", this);
        L.d(L.knowledge2String(k.contextPoints()), this);

        // Remember that this knowledge has been sent
        this.se.setSentInformation(k, address); 

        // notify message accounting
        this.se.getKepStub().sentKnowledge(k);
//      }
//      catch(Exception e) {
//        e.printStackTrace();
//          this.responseSent =  false;
//      }

      this.responseSent =  true;
    }

    /**
     * Create an expose command with the <code>ExposedInterest</code> passed.
     *
     * @param interest The <code>Interest</code> that shall be sent inside the expose command.
     * 
     * @param kp The <code>KEPHandler</code>
     */
    @Override
    public void expose(SharkCS interest) throws IOException {
//      if(!this.se.getKepStub().interestAllowed(interest)){
//        // It is not allowed due to silence period.
//        L.d("Message not allowed to be sent!", this);
//        return;
//      }
        
//      L.d("expose() called.", this);
//      try {
        // write header
//        L.d("Writing header ... ", this);
        this.writeHeader(KEPMessage.KEP_EXPOSE, this.se.getKnowledgeFormat());
//        L.d("Wrote header", this);
        try {
            this.ks.write(interest, os);
        }
        catch(SharkKBException e) {
            throw new IOException(e.getMessage());
        }

//        L.d("Wrote interest", this);
        // notify we are done with that message
        this.sent();
        
        // notify message accounting
        this.se.getKepStub().sentInterest(interest);
        
        L.d(">>>>>>>>>>> expose sent", this);
//      }
//      catch(Exception e) {
//        this.responseSent = false;
//          e.printStackTrace();
//      }

      this.responseSent = true;
    }

    /**
     * @param privateKey must be set if signing or encryption is wanted
     * @param publicKeyRemotePeer if set - message will be encrypted.
     * @param sendingPeerSIString must be set if signing or encryption is wanted
     */
    public void initSecurity(PrivateKey privateKey, PublicKey publicKeyRemotePeer,
            String[] sendingPeerSIString, boolean sign) {

        this.publicKeyRecipient = publicKeyRemotePeer;
        this.privateKey = privateKey;
        if(sendingPeerSIString != null) {
            this.sendingPeerSIString = Utils.serialize(sendingPeerSIString);
        }
        
        this.sign = sign;
    }
    
}
