package net.sharkfw.protocols.m2s;

import java.io.IOException;
import java.io.InputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 *<p>This InputStream implementation listens for incoming messages from a message
 * based communication stub. The messages are buffered in a vector in the order
 * of their packet#. Every read() returns a single byte from the current packet.
 * If the end of a packet has been reached the next packet will be read (if available)
 * or the read() call blocks until more packets have been received.</p> 
 * 
 * <p>If the last packet has been read completely subsequent read() calls will return -1.</p>
 * 
 * @author thsc
 */
public class MessagesToStreamInputStream extends InputStream {
    private final MessageStorage storage;
    private final String id;
    private InputStream currentStream;
    private int size;
    
    /**
     * @param storage 
     */
    MessagesToStreamInputStream(MessageStorage storage, String id) {
        this.storage = storage;
        this.id = id;
        
        this.currentStream = null;
    }
    
    private boolean completionCheck = false;
    
    @Override
    public int read() throws IOException {
        try {
            if(!this.completionCheck) {
                if(!this.storage.completelyReceived(this.id)) {
                    L.d("KEP message hasn't arrived yet completely", this);
                    throw new IOException("KEP message hasn't arrived yet completely");
                } else {
                    this.completionCheck = true;
                }
            }
        
            // message is completely stored - iterate parts
            if(currentStream == null) {
                this.size = this.storage.getNextPackageSizeToRead(id);
                if(this.size == -1) {
                    return -1;
                }

                this.currentStream = this.storage.getNextPartInputStream(id);
                
                // there is no other information left
                if(this.currentStream == null) {
                    // remove temporary storage
                    this.storage.removeToRead(this.id);
                    return -1;
                }
            }
        }
        catch(SharkException e) {
            throw new IOException(e.getMessage());
        }

        // try to read
        int b = this.currentStream.read();
        if(b == -1) {
            L.d("try switching input stream after bytes: " + this.size, this);
            this.currentStream = null;
            return this.read();
        }
        
        this.size++;
        
        return b;
    }
}
