package net.sharkfw.protocols.m2s;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sharkfw.protocols.ConnectionListenerManager;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.PeerAddress;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class M2SConnection extends ConnectionListenerManager implements StreamConnection {
    
    private String localAddress = null;
    private final String receiverAddress;
    private final String id;
    private final MessageStub mStub;
    
    private InputStream is;
    private OutputStream os;
    private MessageStorage storage = null;
    private ByteArrayInputStream bais = null;
    private final int maxSize;

    private final M2SStub m2sStub;
    
    M2SConnection(MessageStorage storage, String receiverAddress, 
            M2SStub m2sStub, MessageStub mStub, String id) {
        
        this.receiverAddress = receiverAddress;
        this.m2sStub = m2sStub;
        this.mStub = mStub;
        this.id = id;
        this.localAddress = mStub.getReplyAddressString();
        
        // max size in bytes
        this.maxSize = PeerAddress.getMaxSize(receiverAddress) * 1024;
        
        this.storage = storage;
        
        this.bais = null;
    }

    M2SConnection(MessageStorage storage, ByteArrayInputStream bais, String addressString, 
            M2SStub m2sStub, MessageStub mStub, String id) {
        
        this(storage, addressString, m2sStub, mStub, id);
        
        this.bais = bais;
    }


    @Override
    public InputStream getInputStream() {
        return this.is;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.os;
    }

    @Override
    public void sendMessage(byte[] msg) throws IOException {
        this.mStub.sendMessage(msg, this.receiverAddress);
    }

    @Override
    public String getReplyAddressString() {
        return this.mStub.getReplyAddressString();
    }

    @Override
    public void close() {
        try {
            if(this.is != null) {
              this.is.close();
            }
            if(this.os != null) {
              this.os.close();
            }

            this.is = null;
            this.os = null;
            
            L.d("StreamConnection closed", this);
            
            this.notifyConnectionClosed();
        } catch (IOException ex) {
          L.e(ex.getMessage(), this);
        }
    }

    public String getReceiverAddressString() {
      return this.receiverAddress;
    }

    @Override
    public String getLocalAddressString() {
        return this.localAddress;
    }

    @Override
    public void setLocalAddressString(String localAddress) {
        this.localAddress = localAddress;
    }
}
