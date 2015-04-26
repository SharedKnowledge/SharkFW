package net.sharkfw.kep;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.SharkEngine.SecurityLevel;
import net.sharkfw.peer.SharkEngine.SecurityReplyPolicy;
import net.sharkfw.pki.SharkPublicKeyStorage;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Streamer;

/**
 * This class handles communications over an established <code>StreamConnection</code>.
 * Its job is to listen for incoming requests on that connection. If an error occurrs
 * the session will wait and try again. If no more data is sent or more errors occurr the session closes down.
 *
 * @see net.sharkfw.protocols.StreamConnection
 * @see net.sharkfw.kep.KEPStub
 * @see net.sharkfw.peer.SharkEngine
 *
 * @author thsc
 * @author mfi
 */
public class KEPSession extends Thread {
    private KEPStub kepStub;
    private StreamConnection con;
    private SharkEngine se;
    private SharkPublicKeyStorage publicKeyStorage;
    private SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;

    KEPSession(SharkEngine se, StreamConnection con, KEPStub kepStub) {
        this.se = se;
        this.con = con;
        this.kepStub = kepStub;
    }

    /**
     * Start to listen for incoming <code>KEPRequest</code>s
     */
    @Override
    public void run() {
    	L.d("Started.", this);
    	boolean handled = false;

    	do {
    		L.d("Next run starts.", this);
    		try { 
    			L.d("Creating KEPRequest from connection replyaddress: " + this.con.getReplyAddressString(), this);
    			KEPInMessage inMsg = new KEPInMessage(this.se, this.con);
    			inMsg.initSecurity(this.privateKey, this.publicKeyStorage,
                                this.encryptionLevel, this.signatureLevel,
                                this.replyPolicy, this.refuseUnverifiably);
    			inMsg.parse();
    			L.d("Created KEPRequest object", this);
    			handled = this.kepStub.callListener(inMsg);
    			handled = handled && inMsg.keepOpen();
    		} catch (SharkNotSupportedException e) {
    			L.e("unsupported KEP format: " + e.getMessage(), this);
//              e.printStackTrace();
    		} catch (IOException ioe) {
    			// connection closed - bye
    			handled = false;
    			L.d("IOException while handling KEP Request - go ahead", this);
    		} catch (SharkSecurityException ioe) {
    			// connection closed - bye
    			handled = false;
    			L.d("Security Exception", this);
    		}
            catch (SharkKBException ioe) {
            	// connection closed - bye
            	handled = false;
            	L.d("SharkKB Exception", this);
            }
            catch(RuntimeException re) {
            	L.d("connection refused - peer already gone", this);
            }

    		L.d("Handled = " + handled, this);
    		if(!handled) {
    			L.d("Checking for more KEP-Commands", this);
                // no listener handled that request
                // maybe there is another KEP methode in the stream
                try {
                    if(this.con.getInputStream().available() > 0) {
                    	L.d("More bytes available on inputstream" , this);
                    	handled = true;
                    } else {
                        // maybe remote peer wasn't fast enough - give it some time
                        L.d("Waiting for remotepeer for: " + se.getConnectionTimeOut(), this);
                        Thread.sleep(se.getConnectionTimeOut());
                        if(this.con.getInputStream().available() > 0) {
                        	handled = true;
                        }
                    }
                }
                catch(Exception e) {
                    // ignore and go ahead
                }
            }
        } while (handled);

        try {
            final InputStream inputStream = this.con.getInputStream().getInputStream();
            if (inputStream.available() > 0) {
                L.e("Closing TCPConnection although there is more data on the stream: ", this);
                Streamer.stream(inputStream, System.err, 5);
            }
        } catch (IOException e) {
            L.l("Closing TCPConnection although there is more data on the stream: " + e.getMessage(), this);
        }

        this.kepStub.removeStreamConnection(con);
        this.con.close();
    }

    private SecurityLevel signatureLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private SecurityLevel encryptionLevel = SharkEngine.SecurityLevel.IF_POSSIBLE;
    private PublicKey publicKeyRemotePeer;
    private PrivateKey privateKey;

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
}
