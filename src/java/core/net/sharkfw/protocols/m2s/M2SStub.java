package net.sharkfw.protocols.m2s;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * This class makes a message based communication protocol to a stream based
 * protocol. It is an intermediate layer which actually performs a little
 * protocol, the message to stream protocol. 
 * 
 * This class plays the RequestHandler role for underlaying MessageStub which 
 * send incomming message to this class.
 * 
 * A complete KEP message can be in a single message but also distributed
 * over a number of messages. Once all message parts are arrived, a stream is
 * created and publised to KEPStub which provides the whole KEP message.
 * 
 * @author thsc
 */
public class M2SStub implements StreamStub, RequestHandler {
  
    public static final int INT_TRUE = 1;
    public static final int INT_FALSE = 0;

    public static final int M2S_INSERT = 1;
    public static final int M2S_ASK = 0;
    
    private final MessageStub mStub;
    private RequestHandler handler;
    private int ids = 0; // any connection gets its own id.
    private final MessageStorage storage;
    

    /*************************************************************************
     *                                ID handling                            * 
     *************************************************************************/ 
    
    String getNewID() {
        String idString = String.valueOf(ids++) + ":" + 
                this.mStub.getReplyAddressString();
        
        return idString;
    }
    
    private String extractAddressString(String id) {
        int index = id.indexOf(":");
        return id.substring(index+1);
    }
    
    /*************************************************************************
     *                                others                                 * 
     *************************************************************************/ 
    /**
     * @param mStub MessageStub which will be used to stream the data
     * @param maxSize maximum size of each Mail in byte
     * @param handler KEPStub which handles the data
     */
    public M2SStub(MessageStorage storage, MessageStub mStub, 
            RequestHandler handler) {
        
        this.storage = storage;
        this.mStub = mStub;
        this.handler = handler;
        
        this.mStub.setHandler(this);
    }
    
    /**
     * Methode is called if a peer wants to establish a new connection to
     * transfer KEP messeges to a remote peer.
     * @param receiverAddress
     * @return
     * @throws IOException 
     */
    @Override
    public StreamConnection createStreamConnection(String receiverAddress) throws IOException {
        
        // Generate new id once for this connection
        String id = this.getNewID();
        
        // create new connection
        M2SConnection sConn = new M2SConnection(
                this.storage, receiverAddress, this, this.mStub, id);
        
        return sConn;
        
    }

    @Override
    public String getLocalAddress() {
        return this.mStub.getReplyAddressString();
    }

    @Override
    public void stop() {
        this.mStub.stop();
    }

    
    /**
     * This method is called by a message stub - it has got a message
     * that has to be processed. This methode creates a new connection if needed 
     * or uses an already existing streamConnection to add these data to the
     * stream
     * 
     * @param msg
     * @param stub 
     */
    @Override
    public void handleMessage(byte[] msg, MessageStub stub) {
      
      ByteArrayInputStream bais = new ByteArrayInputStream(msg);
      
        try {
//            if(this.storage instanceof SharkKBMessageStorage) {
//                SharkKBMessageStorage kbStorage = (SharkKBMessageStorage)this.storage;
//                
//                L.d("storage when entering M2SSub.handleMessage: " + 
//                        L.kbSpace2String(kbStorage.getKB()), this);
//            }
            
            DataInputStream dis = new DataInputStream(bais);
            // read ID
            String id = dis.readUTF();
            
            L.d("found message with id: " + id, this);
            
            // read command
            int m2sCmd = dis.readInt();
            L.d("m2s command: " + m2sCmd, this);
            
            // end parsing header
            if(m2sCmd == M2SStub.M2S_INSERT) {
                // received something 
                
                // read remaining insert header fields
                
                // read package number
                int packageNumber = dis.readInt();

                // write finished
                boolean isLast = dis.readBoolean();
                
                L.d("package number: " + packageNumber, this);
                L.d("is last package: " + isLast, this);
                
                /*
                 * Usually, a KEP message will fit into a single message
                 * Check if this is the case
                 */
                if(packageNumber == M2SMessage.FIRST_PACKAGE_NUMBER && isLast) {
                    // that's all: this message contains the whole KEP message

                    // create a stream connection
                    StreamConnection con = 
                            new M2SConnection(this.storage, bais, 
                            this.extractAddressString(id), this, stub, id);

                    this.handler.handleStream(con);
                } else { // received message part
                    try {
                        // save this part
                        this.storage.savePart(id, packageNumber, isLast, bais);

                        // last part ?
                        if(this.storage.completelyReceived(id)) {
                            L.d("receveid last message part - transmit whole message to KEPStub", this);
                            StreamConnection con = 
                                    new M2SConnection(this.storage, 
                                    this.extractAddressString(id), this, stub, id);

                            this.handler.handleStream(con);
                        } else {
                            // ask other peer for next packet
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(baos);

                            // write ID
                            dos.writeUTF(id);

                            // aks remote peer for next packet
                            dos.writeInt(M2SStub.M2S_ASK);

                            dos.writeUTF(stub.getReplyAddressString());

                            this.mStub.sendMessage(baos.toByteArray(), this.extractAddressString(id));
                        }
                    }
                    catch(SharkException e) {
                        L.d("couldn't save message part: " + e.getMessage(), this);
                    }
                }
            } else {
                // asked for next package
                String replyAddress = dis.readUTF();
                try {
                    int nextPackageNumber = this.storage.nextPackageNumberToSend(id);
                    String recipientAddress = this.storage.getRecipientAddress(id);
                    int packageSize = this.storage.getMaxPackageSize(id);
                    
                    int headerLen = id.length() + 20; // TODO 20 is a guess
                    
                    L.d("processing ASK request", this);
                    
                    // we have got the package: stream next part into next message
                    int remainingBytes = this.storage.remainingNumberOfBytes(id, nextPackageNumber);
                    L.d("recipient / packageSize / remaining Bytes: " + recipientAddress + " / " + packageSize + " / " + remainingBytes, this);
                    
                    boolean isLastPackage;    
                    int size;
                    
                    if(remainingBytes + headerLen <= packageSize) {
                        // last package
                        isLastPackage = true;
                        size = remainingBytes;
                        
                    } else {
                        // next chunk to send
                        isLastPackage = false;
                        size = remainingBytes - headerLen;
                    }
                    L.d("isLastPackage / size: " + isLastPackage + " / " + size, this);
                    
                    // create header
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    M2SMessage.writeM2SHeader(baos, id, nextPackageNumber, isLastPackage);

                    // stream payload
                    this.storage.streamNextPackageToSend(baos, id, size);

                    byte[] newMsg = baos.toByteArray();

                    this.mStub.sendMessage(newMsg, replyAddress);
                }
                catch(SharkException e) {
                    L.d("couldn't found message part - already removed or never existed", this);
                }
            }
            
            if(this.storage instanceof SharkKBMessageStorage) {
                SharkKBMessageStorage kbStorage = (SharkKBMessageStorage)this.storage;
                
                L.d("storage after entering M2SSub.handleMessage: " + 
                        L.kb2String(kbStorage.getKB()), this);
            }

        } catch (IOException ex) {
          L.d("IOException while handling message: " + ex.getMessage(), this);
        }
    }

    @Override
    public void handleStream(StreamConnection con) {
        throw new UnsupportedOperationException(
                "wrong usage of this class: don't wrap a Stream Stub with this class: " 
                + this.getClass().getName());
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
    
    public void start() throws IOException {
        this.mStub.start();
    }
    
    public boolean started() {
        return this.mStub.started();
    }    

    @Override
    public void handleNewConnectionStream(StreamConnection con) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
