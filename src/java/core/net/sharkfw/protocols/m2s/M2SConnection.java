package net.sharkfw.protocols.m2s;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sharkfw.protocols.ConnectionListenerManager;
import net.sharkfw.protocols.ConnectionStatusListener;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.PeerAddress;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.protocols.StandardSharkInputStream;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.UTF8SharkOutputStream;
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
    
    private SharkInputStream sis = null;
    private SharkOutputStream sos = null;
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
    public SharkInputStream getInputStream() {
        if(sis == null) {
            if(this.bais == null) {
                this.is = new MessagesToStreamInputStream(this.storage, this.id);
            } else {
                this.is = bais;
            }

            this.sis = new StandardSharkInputStream(this.is);
        }
        
        return this.sis;
    }

    @Override
    public SharkOutputStream getOutputStream() {
        if(this.sos == null) {
            
            // give it a new id - an id identifies NOT a session but a message
            String newID = this.m2sStub.getNewID();
            
            this.os = new StreamToMessageOutputStream(this, newID, maxSize, this.receiverAddress, storage);

            L.d("Creating new wrapped stream connection to address string: " + this.receiverAddress, this);
            this.sos = new UTF8SharkOutputStream(this.os); // new MessageStubOutputStream(this, this.id, this.maxSize);
        }
        
        return this.sos;
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
