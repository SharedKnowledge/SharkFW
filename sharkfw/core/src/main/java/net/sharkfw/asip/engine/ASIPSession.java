package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPStub;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Streamer;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

/**
 * Created by msc on 21.03.16.
 */
public class ASIPSession extends Thread {

    private SharkEngine engine;
    private StreamConnection connection;
    private ASIPStub stub;

    private PrivateKey privateKey;
    private SharkPkiStorage sharkPkiStorage;
    private SharkEngine.SecurityLevel encryptionLevel;
    private SharkEngine.SecurityLevel signatureLevel;
    private SharkEngine.SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;

    ASIPSession(SharkEngine engine, StreamConnection connection, ASIPStub stub){
        this.engine = engine;
        this.connection = connection;
        this.stub = stub;
    }

    public void initSecurity(PrivateKey privateKey, SharkPkiStorage sharkPkiStorage,
                             SharkEngine.SecurityLevel encryptionLevel,
                             SharkEngine.SecurityLevel signatureLevel,
                             SharkEngine.SecurityReplyPolicy replyPolicy,
                             boolean refuseUnverifiably) {
        this.privateKey = privateKey;
        this.sharkPkiStorage = sharkPkiStorage;
        this.encryptionLevel = encryptionLevel;
        this.signatureLevel = signatureLevel;
        this.replyPolicy = replyPolicy;
        this.refuseUnverifiably = refuseUnverifiably;
    }

    @Override
    public void run() {
        boolean handled = false;

        do{
            try {
                ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
//                inMessage.initSecurity(this.privateKey, this.sharkPkiStorage, this.encryptionLevel,
//                        this.signatureLevel, this.replyPolicy, this.refuseUnverifiably);
                inMessage.parse();

                handled = this.stub.callListener(inMessage);
                handled = handled && inMessage.keepOpen();
            } catch (SharkKBException e) {
                handled=false;
                e.printStackTrace();
            } catch (SharkSecurityException e) {
                handled=false;
                e.printStackTrace();
            } catch (IOException e) {
                handled=false;
                e.printStackTrace();
            }

            if(!handled) {
                L.d("Checking for more KEP-Commands", this);
                // no listener handled that request
                // maybe there is another KEP methode in the stream
                try {
                    if(this.connection.getSharkInputStream().available() > 0) {
                        L.d("More bytes available on inputstream" , this);
                        handled = true;
                    } else {
                        // maybe remote peer wasn't fast enough - give it some time
                        L.d("Waiting for remotepeer for: " + engine.getConnectionTimeOut(), this);
                        Thread.sleep(engine.getConnectionTimeOut());
                        if(this.connection.getSharkInputStream().available() > 0) {
                            handled = true;
                        }
                    }
                }
                catch(Exception e) {
                    // ignore and go ahead
                }
            }
        } while(handled);

        try {
            final InputStream inputStream = this.connection.getSharkInputStream().getInputStream();
            if (inputStream.available() > 0) {
                L.e("Closing TCPConnection although there is more data on the stream: ", this);
                Streamer.stream(inputStream, System.err, 5);
            }
        } catch (IOException e) {
            L.l("Closing TCPConnection although there is more data on the stream: " + e.getMessage(), this);
        }

        this.connection.close();
    }
}
