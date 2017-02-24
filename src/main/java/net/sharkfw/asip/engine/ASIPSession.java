package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPStub;
import net.sharkfw.asip.serialization.ASIPSerializationHolder;
import net.sharkfw.asip.serialization.ASIPSerializerException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Streamer;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

/**
 * Created by j4rvis on 21.03.16.
 */
public class ASIPSession extends Thread {

    private SharkEngine engine;
    private StreamConnection connection;
    private ASIPStub stub;

    private PrivateKey privateKey;
    //    private SharkPkiStorage sharkPkiStorage;
    private SharkEngine.SecurityLevel encryptionLevel;
    private SharkEngine.SecurityLevel signatureLevel;
    private SharkEngine.SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;

    ASIPSession(SharkEngine engine, StreamConnection connection, ASIPStub stub){
        this.engine = engine;
        this.connection = connection;
        this.stub = stub;
    }

    public void initSecurity(PrivateKey privateKey/*, SharkPkiStorage sharkPkiStorage*/,
                             SharkEngine.SecurityLevel encryptionLevel,
                             SharkEngine.SecurityLevel signatureLevel,
                             SharkEngine.SecurityReplyPolicy replyPolicy,
                             boolean refuseUnverifiably) {
        this.privateKey = privateKey;
//        this.sharkPkiStorage = sharkPkiStorage;
        this.encryptionLevel = encryptionLevel;
        this.signatureLevel = signatureLevel;
        this.replyPolicy = replyPolicy;
        this.refuseUnverifiably = refuseUnverifiably;
    }

    @Override
    public void run() {
        boolean handled = false;
        int looper = 3;
        int currentLoop = 0;

//        ASIPInMessage inMessage = null;
//        try {
//            inMessage = new ASIPInMessage(this.engine, this.connection);
//        } catch (SharkKBException e) {
//            e.printStackTrace();
//        }
//        L.d("Session started for " + this.engine.getOwner().getName(), this);

        do {
            try {
                ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
                boolean parse = inMessage.parse();

                if(parse){
                    handled = this.stub.callListener(inMessage);
                    handled = handled && inMessage.keepOpen();
                }

            } catch (IOException | SharkException e) {
                handled = false;
                e.printStackTrace();
            }

//            try {
//                L.d("IS available for " + this.engine.getOwner().getName() + ": " + (this.connection.getInputStream().available()>0), this);
//                if(this.connection.getInputStream().available()>0){
//                    inMessage.parse();
//                }
//
//                if(inMessage.isParsed()){
//                    handled = this.stub.callListener(inMessage);
//                    handled = handled && inMessage.keepOpen();
//                }
//
//            } catch (IOException | SharkSecurityException | ASIPSerializerException e) {
//                handled = false;
//                L.d(e.getMessage(), this);
//                e.printStackTrace();
//            }

            if(!handled) {
                // maybe there is another KEP methode in the stream
                try {
                    if (this.connection.getInputStream().available() > 0) {
                        L.d("More bytes available on inputstream", this);
                        handled = true;
                    } else {
                        // maybe remote peer wasn't fast enough - give it some time
                        L.d("Waiting for remotepeer for max. : " + engine.getConnectionTimeOut(), this);
                        long duration = engine.getConnectionTimeOut() / looper;
                        while (currentLoop < looper) {
                            Thread.sleep(duration);
                            currentLoop++;
                            if (this.connection.getInputStream().available() > 0) {
                                handled = true;
                                currentLoop = 0;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore and go ahead
                }
            }
        } while (handled);

        try {
            final InputStream inputStream = this.connection.getInputStream();
            if (inputStream.available() > 0) {
                L.e("Closing connection although there is more data on the stream: ", this);
                Streamer.stream(inputStream, System.err, 5);
            }
        } catch (IOException e) {
            L.l("Closing connection although there is more data on the stream: " + e.getMessage(), this);
        }
        this.connection.close();
    }
}
